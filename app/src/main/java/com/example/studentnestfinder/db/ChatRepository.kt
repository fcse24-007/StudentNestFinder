package com.example.studentnestfinder.db

import com.example.studentnestfinder.db.dao.ChatMessageDao
import com.example.studentnestfinder.db.entities.ChatMessage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * ChatRepository
 *
 * Strategy:
 *  - Room is the single source of truth for chat reads and writes.
 *  - UI observes Room Flows directly for local-first updates.
 *
 * conversationId format: "uid_{min}-{max}_listing_{listingId}"
 * This makes the ID deterministic regardless of who opens the chat first.
 */
class ChatRepository(
    private val chatMessageDao: ChatMessageDao
) {

    // ─── Conversation ID helper ───────────────────────────────────────────────

    fun conversationId(userA: Int, userB: Int, listingId: Int): String {
        return conversationIdFor(userA, userB, listingId)
    }

    // ─── Send a message ───────────────────────────────────────────────────────

    suspend fun sendMessage(
        senderId: Int,
        receiverId: Int,
        listingId: Int,
        text: String
    ) {
        val convId = conversationId(senderId, receiverId, listingId)
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = convId,
            senderId = senderId,
            receiverId = receiverId,
            listingId = listingId,
            message = text,
            timestamp = System.currentTimeMillis()
        )
        chatMessageDao.insert(msg)
    }

    // ─── Observe a conversation (Room-backed) ─────────────────────────────────

    fun observeConversation(
        userA: Int,
        userB: Int,
        listingId: Int
    ): Flow<List<ChatMessage>> {
        val convId = conversationId(userA, userB, listingId)
        return chatMessageDao.getConversation(convId)
    }

    // ─── Mark messages as read ────────────────────────────────────────────────

    suspend fun markRead(userA: Int, userB: Int, listingId: Int, currentUserId: Int) {
        val convId = conversationId(userA, userB, listingId)
        chatMessageDao.markConversationRead(convId, currentUserId)
    }

    // ─── Conversation list (for inbox screen) ─────────────────────────────────

    fun observeConversationList(userId: Int): Flow<List<ChatMessage>> =
        chatMessageDao.getConversationList(userId)

    fun observeUnreadCount(userId: Int): Flow<Int> =
        chatMessageDao.getUnreadCount(userId)
}
