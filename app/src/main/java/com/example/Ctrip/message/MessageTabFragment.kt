@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.Ctrip.message

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.Ctrip.model.*

class MessageTabView(private val context: Context) : MessageTabContract.View {
    
    private lateinit var presenter: MessageTabContract.Presenter
    private var messageData by mutableStateOf<MessageData?>(null)
    private var isLoading by mutableStateOf(false)
    
    fun initialize() {
        val model = MessageTabModelImpl(context)
        presenter = MessageTabPresenter(model)
        presenter.attachView(this)
        presenter.loadMessageData()
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MessageTabScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeColor(0xFFF5F5F5))
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            TopBarSection()
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    MessageActionsSection()
                }
                
                item {
                    SystemMessagesSection()
                }
                
                item {
                    ConversationMessagesSection()
                }
                
                item {
                    NoMoreMessagesSection()
                }
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBarSection() {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "消息",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    messageData?.conversationMessages?.let { messages ->
                        val unreadCount = messages.count { it.hasUnread }
                        if (unreadCount > 0) {
                            Text(
                                text = "($unreadCount)",
                                fontSize = 16.sp,
                                color = ComposeColor(0xFF666666)
                            )
                        }
                    }
                }
            },
            actions = {
                IconButton(onClick = { presenter.onCustomerServiceClicked() }) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "客服",
                        tint = ComposeColor(0xFF666666)
                    )
                }
                IconButton(onClick = { presenter.onSettingsClicked() }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = ComposeColor(0xFF666666)
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = ComposeColor(0xFFF0F8FF)
            )
        )
    }
    
    @Composable
    private fun MessageActionsSection() {
        messageData?.let { data ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                LazyRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(data.messageActions) { action ->
                        MessageActionItem(action) {
                            presenter.onMessageActionClicked(action.id)
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun MessageActionItem(action: MessageAction, onClick: () -> Unit) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Card(
                    modifier = Modifier.size(56.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ComposeColor(android.graphics.Color.parseColor(action.color))
                    ),
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = action.icon,
                            fontSize = 24.sp,
                            color = ComposeColor.White
                        )
                    }
                }
                
                if (action.hasUnread && action.unreadCount > 0) {
                    Badge(
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = action.unreadCount.toString(),
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = action.title,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    
    @Composable
    private fun SystemMessagesSection() {
        messageData?.let { data ->
            data.systemMessages.forEach { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { presenter.onSystemMessageClicked(message.id) },
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ComposeColor(android.graphics.Color.parseColor(message.color))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = message.icon,
                                fontSize = 20.sp,
                                color = ComposeColor.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = message.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = message.content,
                                fontSize = 14.sp,
                                color = ComposeColor(0xFF666666),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun ConversationMessagesSection() {
        messageData?.let { data ->
            data.conversationMessages.forEach { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { presenter.onConversationMessageClicked(message.id) },
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ComposeColor(0xFFF0F0F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = message.avatar,
                                    fontSize = 24.sp
                                )
                            }
                            
                            if (message.hasUnread) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(ComposeColor.Red)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = message.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = message.time,
                                    fontSize = 12.sp,
                                    color = ComposeColor(0xFF999999)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (message.tags.isNotEmpty()) {
                                    message.tags.forEach { tag ->
                                        Card(
                                            modifier = Modifier.padding(end = 4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = ComposeColor(0xFF4CAF50)
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = tag,
                                                modifier = Modifier.padding(4.dp, 2.dp),
                                                fontSize = 10.sp,
                                                color = ComposeColor.White
                                            )
                                        }
                                    }
                                }
                                
                                Text(
                                    text = message.content,
                                    fontSize = 14.sp,
                                    color = ComposeColor(0xFF666666),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun NoMoreMessagesSection() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "没有更多了",
                fontSize = 14.sp,
                color = ComposeColor(0xFF999999)
            )
        }
    }
    
    override fun showMessageData(data: MessageData) {
        messageData = data
    }
    
    override fun showLoading() {
        isLoading = true
    }
    
    override fun hideLoading() {
        isLoading = false
    }
    
    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToOrderTravel() {
        Toast.makeText(context, "导航到订单出行", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToInteractiveMessage() {
        Toast.makeText(context, "导航到互动消息", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToAccountNotification() {
        Toast.makeText(context, "导航到账户通知", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToMemberService() {
        Toast.makeText(context, "导航到会员服务", Toast.LENGTH_SHORT).show()
    }
    
    override fun onSystemMessageClick(messageId: String) {
        Toast.makeText(context, "点击系统消息: $messageId", Toast.LENGTH_SHORT).show()
    }
    
    override fun onConversationMessageClick(messageId: String) {
        Toast.makeText(context, "点击会话消息: $messageId", Toast.LENGTH_SHORT).show()
    }
    
    override fun onCustomerServiceClick() {
        Toast.makeText(context, "联系客服", Toast.LENGTH_SHORT).show()
    }
    
    override fun onSettingsClick() {
        Toast.makeText(context, "打开设置", Toast.LENGTH_SHORT).show()
    }
}