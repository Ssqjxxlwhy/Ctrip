package com.example.Ctrip.my

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.Ctrip.model.*

class MyTabView(private val context: Context) : MyTabContract.View {
    
    private lateinit var presenter: MyTabContract.Presenter
    private var userData by mutableStateOf<UserData?>(null)
    private var isLoading by mutableStateOf(false)
    
    fun initialize() {
        val model = MyTabModelImpl(context)
        presenter = MyTabPresenter(model)
        presenter.attachView(this)
        presenter.loadUserData()
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyTabScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeColor(0xFFF5F5F5))
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            TopBarSection()
            
            if (userData != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        UserProfileSection()
                    }
                    
                    item {
                        MembershipSection()
                    }
                    
                    item {
                        StatisticsSection()
                    }
                    
                    item {
                        MenuItemsSection()
                    }
                    
                    item {
                        WalletSection()
                    }
                    
                    item {
                        PromotionsSection()
                    }
                    
                    item {
                        MyPublishSection()
                    }
                    
                    userData?.bottomPromotion?.let { promotion ->
                        item {
                            BottomPromotionSection(promotion)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("加载中...")
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
            title = { },
            actions = {
                IconButton(onClick = { presenter.onSignInClicked() }) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "签到",
                        tint = ComposeColor(0xFF666666)
                    )
                }
                IconButton(onClick = { presenter.onScanClicked() }) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "扫一扫",
                        tint = ComposeColor(0xFF666666)
                    )
                }
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
    private fun UserProfileSection() {
        userData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(ComposeColor(0xFF87CEEB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "👤",
                                fontSize = 32.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = data.userProfile.displayName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Text(
                            text = "${data.userProfile.memberTitle} >",
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF666666),
                            modifier = Modifier.clickable { presenter.onProfileClicked() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem("粉丝", data.userProfile.followers)
                        StatItem("关注", data.userProfile.following)
                        StatItem("获赞", data.userProfile.likes)
                        StatItem("赞过", data.userProfile.praised)
                    }
                }
            }
        }
    }
    
    @Composable
    private fun StatItem(label: String, count: Int) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = ComposeColor(0xFF666666)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
    
    @Composable
    private fun MembershipSection() {
        userData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFFE8F4FD)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { presenter.onMemberCenterClicked() }
                        ) {
                            Text(
                                text = "💎",
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${data.membershipInfo.level} 会员中心 >",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFFFF6B35)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "🔥赚5倍积分 >",
                                modifier = Modifier.padding(8.dp, 4.dp),
                                fontSize = 12.sp,
                                color = ComposeColor.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        data.membershipInfo.benefits.forEach { benefit ->
                            MemberBenefitItem(benefit = benefit)
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun MemberBenefitItem(benefit: MemberBenefit) {
        Column(
            modifier = Modifier.clickable { },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ComposeColor(android.graphics.Color.parseColor(benefit.color))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = benefit.icon,
                    fontSize = 24.sp,
                    color = ComposeColor.White
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = benefit.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = benefit.subtitle,
                fontSize = 11.sp,
                color = ComposeColor(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
    
    @Composable
    private fun StatisticsSection() {
        userData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem("收藏", data.statistics.favorites)
                    StatisticItem("浏览历史", data.statistics.browsingHistory)
                    StatisticItem("积分", data.statistics.points)
                    StatisticItem("优惠券", data.statistics.coupons)
                }
            }
        }
    }
    
    @Composable
    private fun StatisticItem(title: String, count: Int) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { }
        ) {
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = ComposeColor(0xFF666666)
            )
        }
    }
    
    @Composable
    private fun MenuItemsSection() {
        userData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // First row - 5 items
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        data.menuItems.take(5).forEach { item ->
                            MenuItemView(item) {
                                presenter.onMenuItemClicked(item.id)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Second row - 5 items  
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        data.menuItems.drop(5).take(5).forEach { item ->
                            MenuItemView(item) {
                                presenter.onMenuItemClicked(item.id)
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun MenuItemView(item: MenuItem, onClick: () -> Unit) {
        Column(
            modifier = Modifier
                .clickable { onClick() }
                .width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.icon,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.title,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    
    @Composable
    private fun WalletSection() {
        userData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.wallet.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "现金 · 返现 · 礼品卡",
                                fontSize = 12.sp,
                                color = ComposeColor(0xFF666666)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ">",
                                fontSize = 14.sp,
                                color = ComposeColor(0xFF666666)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        data.wallet.walletItems.forEach { walletItem ->
                            WalletItemView(walletItem = walletItem) {
                                presenter.onWalletItemClicked(walletItem.id)
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun WalletItemView(
        walletItem: WalletItem,
        onClick: () -> Unit
    ) {
        Column(
            modifier = Modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = walletItem.amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (walletItem.hasPromotion) ComposeColor(0xFFFF6B35) else ComposeColor.Black
            )
            Text(
                text = walletItem.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = walletItem.description,
                fontSize = 11.sp,
                color = ComposeColor(0xFF999999)
            )
            
            if (walletItem.hasPromotion) {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFFFF6B35)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = walletItem.promotionText,
                        modifier = Modifier.padding(6.dp, 2.dp),
                        fontSize = 10.sp,
                        color = ComposeColor.White
                    )
                }
            }
        }
    }
    
    @Composable
    private fun PromotionsSection() {
        userData?.let { data ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                data.promotions.forEach { promotion ->
                    PromotionItemView(
                        promotion = promotion,
                        modifier = Modifier.weight(1f)
                    ) {
                        presenter.onPromotionClicked(promotion.id)
                    }
                }
            }
        }
    }
    
    @Composable
    private fun PromotionItemView(
        promotion: PromotionItem,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Card(
            modifier = modifier
                .height(80.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = ComposeColor(android.graphics.Color.parseColor(promotion.backgroundColor))
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = promotion.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = promotion.subtitle,
                    fontSize = 10.sp,
                    color = ComposeColor(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    
    @Composable
    private fun MyPublishSection() {
        userData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.myPublish.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${data.myPublish.moreText} >",
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF666666)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        data.myPublish.items.forEach { item ->
                            PublishItemView(
                                item = item,
                                modifier = Modifier.weight(1f)
                            ) {
                                presenter.onPublishItemClicked(item.id)
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun PublishItemView(
        item: PublishItem,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Column(
            modifier = modifier.clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ComposeColor(android.graphics.Color.parseColor(item.color))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.icon,
                    fontSize = 20.sp,
                    color = ComposeColor.White
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = item.title,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
    
    @Composable
    private fun BottomPromotionSection(promotion: BottomPromotion) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { presenter.onBottomPromotionClicked() },
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
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ComposeColor(0xFF4A90E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎢",
                        fontSize = 32.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = promotion.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = promotion.subtitle,
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF666666)
                    )
                }
                
                Button(
                    onClick = { presenter.onBottomPromotionClicked() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ComposeColor(0xFF00BCD4)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = promotion.buttonText,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
    
    override fun showUserData(data: UserData) {
        userData = data
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
    
    override fun navigateToProfile() {
        Toast.makeText(context, "查看个人主页", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToMemberCenter() {
        Toast.makeText(context, "会员中心", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToMenuItem(menuId: String) {
        Toast.makeText(context, "菜单项: $menuId", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToWalletItem(walletId: String) {
        Toast.makeText(context, "钱包项目: $walletId", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToPromotion(promotionId: String) {
        Toast.makeText(context, "推广项目: $promotionId", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToPublishItem(publishId: String) {
        Toast.makeText(context, "发布项目: $publishId", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToBottomPromotion() {
        Toast.makeText(context, "底部推广活动", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToSettings() {
        Toast.makeText(context, "设置", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToSignIn() {
        Toast.makeText(context, "签到", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToScan() {
        Toast.makeText(context, "扫一扫", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToCustomerService() {
        Toast.makeText(context, "客服", Toast.LENGTH_SHORT).show()
    }
}