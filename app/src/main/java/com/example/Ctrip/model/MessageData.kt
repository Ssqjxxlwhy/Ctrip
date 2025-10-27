package com.example.Ctrip.model

data class MessageData(
    val messageActions: List<MessageAction>,
    val systemMessages: List<SystemMessage>,
    val conversationMessages: List<ConversationMessage>
)

data class MessageAction(
    val id: String,
    val title: String,
    val icon: String,
    val color: String,
    val hasUnread: Boolean = false,
    val unreadCount: Int = 0
)

data class SystemMessage(
    val id: String,
    val title: String,
    val content: String,
    val icon: String,
    val color: String,
    val time: String? = null,
    val isNew: Boolean = false
)

data class ConversationMessage(
    val id: String,
    val title: String,
    val content: String,
    val time: String,
    val avatar: String,
    val hasUnread: Boolean = false,
    val unreadCount: Int = 0,
    val tags: List<String> = emptyList()
)