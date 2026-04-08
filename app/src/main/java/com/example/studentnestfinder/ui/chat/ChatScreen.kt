package com.example.studentnestfinder.ui.chat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.studentnestfinder.db.ChatRepository
import com.example.studentnestfinder.db.entities.ChatMessage as ChatMessageEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    recipientName: String,
    currentUserId: Int,
    chatRepository: ChatRepository,
    onBack: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }
    
    // Parse conversationId to get recipientId and listingId
    // format: "uid_{min}-{max}_listing_{listingId}"
    val parts = conversationId.split("_")
    val uidRange = parts[1].split("-")
    val listingId = parts[3].toInt()
    val userA = uidRange[0].toInt()
    val userB = uidRange[1].toInt()
    val recipientId = if (userA == currentUserId) userB else userA

    val messagesList by chatRepository.observeConversation(userA, userB, listingId)
        .collectAsState(initial = emptyList())

    LaunchedEffect(conversationId) {
        chatRepository.markRead(userA, userB, listingId, currentUserId)
    }

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                title = {
                    Column {
                        Text(recipientName, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Active now", color = Color(0xFFBB86FC), fontSize = 12.sp)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF121212))
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messagesList) { message ->
                    ChatMessageBubble(message, isCurrentUser = message.senderId == currentUserId)
                }
            }

            HorizontalDivider(color = Color(0xFF252525))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    placeholder = { Text("Type a message...", color = Color.Gray) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFBB86FC),
                        unfocusedBorderColor = Color(0xFF252525),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                val scope = rememberCoroutineScope()
                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            val text = messageInput
                            messageInput = ""
                            scope.launch {
                                chatRepository.sendMessage(
                                    senderId = currentUserId,
                                    receiverId = recipientId,
                                    listingId = listingId,
                                    text = text
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFBB86FC), shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessageEntity, isCurrentUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (isCurrentUser) 16.dp else 4.dp,
                topEnd = if (isCurrentUser) 4.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) Color(0xFFBB86FC) else Color(0xFF1E1E1E)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    message.message,
                    color = if (isCurrentUser) Color.Black else Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                    color = if (isCurrentUser) Color.Black.copy(alpha = 0.7f) else Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    userId: Int,
    chatRepository: ChatRepository,
    onChatSelect: (conversationId: String, recipientName: String) -> Unit,
    onBack: () -> Unit
) {
    val conversations by chatRepository.observeConversationList(userId)
        .collectAsState(initial = emptyList())

    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                title = { Text("Messages", color = Color.White, fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF121212))
        ) {
            items(conversations) { message ->
                // In a real app, we'd need to fetch the recipient's name from User table
                // For this mock/MVP, we'll use a placeholder or part of the message
                ConversationItem(
                    conversation = ChatConversation(
                        id = message.conversationId,
                        name = "User ${if (message.senderId == userId) message.receiverId else message.senderId}",
                        lastMessage = message.message,
                        time = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(message.timestamp))
                    ),
                    onClick = { onChatSelect(message.conversationId, "User ${if (message.senderId == userId) message.receiverId else message.senderId}") }
                )
            }
        }
    }
}

data class ChatConversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val time: String
)

@Composable
fun ConversationItem(conversation: ChatConversation, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFFBB86FC)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        conversation.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        conversation.lastMessage,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    conversation.time,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Surface(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFBB86FC)
                ) {
                    Text(
                        "1",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ChatScreenPreview() {
    // ChatScreen(
    //     conversationId = "conv_1",
    //     recipientName = "Nobantu Kasane",
    //     currentUserId = 1,
    //     onBack = {}
    // )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun ChatListScreenPreview() {
    // ChatListScreen(onChatSelect = { _, _ -> }, onBack = {})
}

