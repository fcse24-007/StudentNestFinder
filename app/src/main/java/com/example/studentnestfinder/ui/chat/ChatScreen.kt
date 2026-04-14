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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.studentnestfinder.db.ChatRepository
import com.example.studentnestfinder.db.dao.UserDao
import com.example.studentnestfinder.db.entities.ChatMessage as ChatMessageEntity
import com.example.studentnestfinder.ui.navigation.AppOverflowMenu
import com.example.studentnestfinder.ui.theme.BorderLightColor
import com.example.studentnestfinder.ui.theme.NeutralColor
import com.example.studentnestfinder.ui.theme.PrimaryColor
import com.example.studentnestfinder.ui.theme.SecondaryColor
import com.example.studentnestfinder.ui.theme.TextSecondaryColor
import com.example.studentnestfinder.validation.InputValidator
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
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf<String?>(null) }

    // format: uid_{min}-{max}_listing_{listingId}
    val parsed = remember(conversationId, currentUserId) {
        val regex = Regex("^uid_(\\d+)-(\\d+)_listing_(\\d+)$")
        regex.find(conversationId)?.destructured?.let { (a, b, listing) ->
            val userA = a.toIntOrNull() ?: -1
            val userB = b.toIntOrNull() ?: -1
            val listingId = listing.toIntOrNull() ?: -1
            val recipientId = if (userA == currentUserId) userB else userA
            ParsedConversation(userA, userB, recipientId, listingId)
        }
    }

    if (parsed == null || parsed.listingId < 0 || parsed.userA < 0 || parsed.userB < 0) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Invalid conversation.", color = NeutralColor)
        }
        return
    }

    val messagesList by remember(parsed) { chatRepository.observeConversation(parsed.userA, parsed.userB, parsed.listingId) }
        .collectAsState(initial = emptyList())

    LaunchedEffect(conversationId) {
        chatRepository.markRead(parsed.userA, parsed.userB, parsed.listingId, currentUserId)
    }

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SecondaryColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralColor)
                    }
                },
                title = {
                    Column {
                        Text(recipientName, color = NeutralColor, style = MaterialTheme.typography.titleSmall)
                        Text("Active now", color = PrimaryColor, style = MaterialTheme.typography.labelSmall)
                    }
                },
                actions = {
                    AppOverflowMenu(
                        onSettingsClick = onSettingsClick,
                        onHelpClick = onHelpClick,
                        onFaqClick = onFaqClick,
                        onLogout = onLogout
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SecondaryColor)
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

            HorizontalDivider(color = BorderLightColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = InputValidator.sanitizeText(it, 500) },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    placeholder = { Text("Type a message...", color = TextSecondaryColor) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = BorderLightColor,
                        focusedTextColor = NeutralColor,
                        unfocusedTextColor = NeutralColor
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Attachment",
                            tint = TextSecondaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                val scope = rememberCoroutineScope()
                IconButton(
                    onClick = {
                        val validationError = InputValidator.validateChatMessage(messageInput)
                        if (validationError == null) {
                            val text = messageInput.trim()
                            messageInput = ""
                            messageError = null
                            scope.launch {
                                chatRepository.sendMessage(
                                    senderId = currentUserId,
                                    receiverId = parsed.recipientId,
                                    listingId = parsed.listingId,
                                    text = text
                                )
                            }
                        } else {
                            messageError = validationError
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            messageError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
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
                containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    message.message,
                    color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else NeutralColor,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                    color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else TextSecondaryColor,
                    style = MaterialTheme.typography.labelSmall
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
    userDao: UserDao,
    onChatSelect: (conversationId: String, recipientName: String) -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogout: () -> Unit
) {
    val conversations by chatRepository.observeConversationList(userId)
        .collectAsState(initial = emptyList())

    Scaffold(
        containerColor = SecondaryColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SecondaryColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = NeutralColor)
                    }
                },
                title = { Text("Messages", color = NeutralColor, style = MaterialTheme.typography.titleMedium) },
                actions = {
                    AppOverflowMenu(
                        onSettingsClick = onSettingsClick,
                        onHelpClick = onHelpClick,
                        onFaqClick = onFaqClick,
                        onLogout = onLogout
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SecondaryColor)
        ) {
            items(conversations) { message ->
                val recipientId = if (message.senderId == userId) message.receiverId else message.senderId
                var recipientName by remember(recipientId) { mutableStateOf("User $recipientId") }
                LaunchedEffect(recipientId) {
                    recipientName = userDao.getById(recipientId)?.name ?: "User $recipientId"
                }
                ConversationItem(
                    conversation = ChatConversation(
                        id = message.conversationId,
                        name = recipientName,
                        lastMessage = message.message,
                        time = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(message.timestamp))
                    ),
                    onClick = { onChatSelect(message.conversationId, recipientName) }
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        conversation.name,
                        color = NeutralColor,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        conversation.lastMessage,
                        color = TextSecondaryColor,
                        style = MaterialTheme.typography.bodySmall,
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
                    color = TextSecondaryColor,
                    style = MaterialTheme.typography.labelSmall
                )
                Surface(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        "1",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall
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

private data class ParsedConversation(
    val userA: Int,
    val userB: Int,
    val recipientId: Int,
    val listingId: Int
)
