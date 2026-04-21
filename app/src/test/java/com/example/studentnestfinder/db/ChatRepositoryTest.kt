package com.example.studentnestfinder.db

import com.example.studentnestfinder.db.dao.ChatMessageDao
import com.example.studentnestfinder.db.entities.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRepositoryTest {

    @Test
    fun conversationId_isDeterministic() {
        val repo = ChatRepository(FakeChatMessageDao())
        val idA = repo.conversationId(2, 51, 26)
        val idB = repo.conversationId(51, 2, 26)
        assertEquals("uid_2-51_listing_26", idA)
        assertEquals(idA, idB)
    }

    @Test
    fun sendMessage_persistsIntoRoom() = runTest {
        val dao = FakeChatMessageDao()
        val repo = ChatRepository(dao)

        repo.sendMessage(
            senderId = 1,
            receiverId = 51,
            listingId = 1,
            text = "hello"
        )

        val messages = repo.observeConversation(1, 51, 1).first()
        assertEquals(1, messages.size)
        assertEquals("uid_1-51_listing_1", messages.first().conversationId)
        assertEquals("hello", messages.first().message)
    }

    @Test
    fun markRead_updatesUnreadCount() = runTest {
        val dao = FakeChatMessageDao()
        val repo = ChatRepository(dao)

        dao.insert(
            ChatMessage(
                id = "1",
                conversationId = "uid_1-51_listing_1",
                senderId = 51,
                receiverId = 1,
                listingId = 1,
                message = "ping",
                isRead = false
            )
        )

        repo.markRead(1, 51, 1, currentUserId = 1)

        val unread = repo.observeUnreadCount(1).first()
        assertEquals(0, unread)
        val updated = repo.observeConversation(1, 51, 1).first()
        assertTrue(updated.all { it.isRead })
    }
}

private class FakeChatMessageDao : ChatMessageDao {
    private val state = MutableStateFlow<List<ChatMessage>>(emptyList())

    override suspend fun insert(message: ChatMessage) {
        upsert(listOf(message))
    }

    override suspend fun insertAll(messages: List<ChatMessage>) {
        upsert(messages)
    }

    override fun getConversation(conversationId: String): Flow<List<ChatMessage>> =
        state.map { all ->
            all.filter { it.conversationId == conversationId }
                .sortedBy { it.timestamp }
        }

    override fun getConversationList(userId: Int): Flow<List<ChatMessage>> =
        state.map { all ->
            all.filter { it.senderId == userId || it.receiverId == userId }
                .groupBy { it.conversationId }
                .values
                .mapNotNull { conversation -> conversation.maxByOrNull { it.timestamp } }
                .sortedByDescending { it.timestamp }
        }

    override suspend fun markConversationRead(conversationId: String, userId: Int) {
        state.value = state.value.map { msg ->
            if (msg.conversationId == conversationId && msg.receiverId == userId) {
                msg.copy(isRead = true)
            } else {
                msg
            }
        }
    }

    override fun getUnreadCount(userId: Int): Flow<Int> =
        state.map { all -> all.count { it.receiverId == userId && !it.isRead } }

    override suspend fun clearConversation(conversationId: String) {
        state.value = state.value.filterNot { it.conversationId == conversationId }
    }

    private fun upsert(messages: List<ChatMessage>) {
        val merged = state.value.associateBy { it.id }.toMutableMap()
        messages.forEach { merged[it.id] = it }
        state.value = merged.values.toList()
    }
}
