package com.example.studentnestfinder.db

import com.example.studentnestfinder.db.dao.ChatMessageDao
import com.example.studentnestfinder.db.entities.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ChatRepository
 *
 * Strategy:
 *  - Firestore is the source of truth for real-time delivery.
 *  - Room is used as a local cache for offline reads and UI observation.
 *  - When a Firestore snapshot arrives we sync it into Room, so the UI
 *    can just observe the Room Flow and get both online and offline data.
 *
 * conversationId format: "uid_{min}-{max}_listing_{listingId}"
 * This makes the ID deterministic regardless of who opens the chat first.
 */
class ChatRepository(
    private val chatMessageDao: ChatMessageDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // ─── Conversation ID helper ───────────────────────────────────────────────

    fun conversationId(userA: Int, userB: Int, listingId: Int): String {
        val min = minOf(userA, userB)
        val max = maxOf(userA, userB)
        return "uid_${min}-${max}_listing_${listingId}"
    }

    // ─── Send a message ───────────────────────────────────────────────────────

    suspend fun sendMessage(
        senderId: Int,
        receiverId: Int,
        listingId: Int,
        text: String
    ) {
        val convId = conversationId(senderId, receiverId, listingId)
        val docRef = firestore.collection("conversations")
            .document(convId)
            .collection("messages")
            .document()                 // Firestore generates the ID

        val msg = ChatMessage(
            id = docRef.id,
            conversationId = convId,
            senderId = senderId,
            receiverId = receiverId,
            listingId = listingId,
            message = text,
            timestamp = System.currentTimeMillis()
        )

        // Write to Firestore first; Room cache is updated via the snapshot listener
        val firestoreMap = mapOf(
            "id" to msg.id,
            "conversationId" to convId,
            "senderId" to senderId,
            "receiverId" to receiverId,
            "listingId" to listingId,
            "message" to text,
            "isRead" to false,
            "timestamp" to msg.timestamp
        )
        docRef.set(firestoreMap).await()

        // Optimistic local insert so the UI updates immediately without waiting for Firestore
        chatMessageDao.insert(msg)
    }

    // ─── Observe a conversation (Room-backed, synced from Firestore) ──────────

    fun observeConversation(
        userA: Int,
        userB: Int,
        listingId: Int
    ): Flow<List<ChatMessage>> {
        val convId = conversationId(userA, userB, listingId)

        // Attach a real-time Firestore listener that feeds into Room
        firestore.collection("conversations")
            .document(convId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val messages = snapshot.documents.mapNotNull { doc ->
                    try {
                        ChatMessage(
                            id = doc.id,
                            conversationId = convId,
                            senderId = (doc.getLong("senderId") ?: 0).toInt(),
                            receiverId = (doc.getLong("receiverId") ?: 0).toInt(),
                            listingId = (doc.getLong("listingId") ?: 0).toInt(),
                            message = doc.getString("message") ?: "",
                            isRead = doc.getBoolean("isRead") ?: false,
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    } catch (e: Exception) { null }
                }
                // Sync into Room (runs in a coroutine — fire and forget is fine here)
                kotlinx.coroutines.GlobalScope.launch {
                    chatMessageDao.insertAll(messages)
                }
            }

        // The UI observes Room — it gets updates whenever Room is written to
        return chatMessageDao.getConversation(convId)
    }

    // ─── Mark messages as read ────────────────────────────────────────────────

    suspend fun markRead(userA: Int, userB: Int, listingId: Int, currentUserId: Int) {
        val convId = conversationId(userA, userB, listingId)
        chatMessageDao.markConversationRead(convId, currentUserId)

        // Also update Firestore so the sender can see the read receipt
        val batch = firestore.batch()
        val msgs = firestore.collection("conversations")
            .document(convId)
            .collection("messages")
            .whereEqualTo("receiverId", currentUserId)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        msgs.documents.forEach { doc ->
            batch.update(doc.reference, "isRead", true)
        }
        if (!msgs.isEmpty) batch.commit().await()
    }

    // ─── Conversation list (for inbox screen) ─────────────────────────────────

    fun observeConversationList(userId: Int): Flow<List<ChatMessage>> =
        chatMessageDao.getConversationList(userId)

    fun observeUnreadCount(userId: Int): Flow<Int> =
        chatMessageDao.getUnreadCount(userId)
}