package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class CabinTypeSelectTabView(private val context: Context) : CabinTypeSelectTabContract.View {
    
    private lateinit var presenter: CabinTypeSelectTabContract.Presenter
    private var cabinTypeData by mutableStateOf<CabinTypeSelectData?>(null)
    private var isLoading by mutableStateOf(false)
    private var selectedCabinType by mutableStateOf("economy")
    
    fun initialize(flightId: String) {
        val model = CabinTypeSelectTabModel(context)
        presenter = CabinTypeSelectTabPresenter(model)
        presenter.attachView(this)
        presenter.loadCabinTypeData(flightId)
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CabinTypeSelectTabScreen(
        onNavigateToOrderForm: (String) -> Unit = {},
        onBack: () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 顶部导航栏
                TopBarSection(onBack = onBack)
                
                // 主要内容区域
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    cabinTypeData?.let { data ->
                        // 航班基本信息
                        item {
                            FlightBasicInfoSection(
                                flightInfo = data.flightInfo,
                                onFavoriteClick = { presenter.onFavoriteClicked() }
                            )
                        }
                        
                        // 行程提示
                        item {
                            TravelTipSection(
                                travelTip = data.travelTip,
                                onClick = { presenter.onTravelTipClicked() }
                            )
                        }
                        
                        // 会员权益
                        item {
                            MembershipBenefitSection(
                                membershipBenefit = data.membershipBenefit,
                                onClick = { presenter.onMembershipBenefitClicked() }
                            )
                        }
                        
                        // 舱型选择
                        item {
                            CabinTypeTabsSection(
                                cabinTypes = data.cabinTypes,
                                selectedCabinType = selectedCabinType,
                                onCabinTypeSelected = { cabinTypeId ->
                                    selectedCabinType = cabinTypeId
                                    presenter.onCabinTypeSelected(cabinTypeId)
                                }
                            )
                        }
                        
                        // 票价选项列表
                        items(data.ticketOptions) { ticketOption ->
                            TicketOptionCard(
                                ticketOption = ticketOption,
                                onTicketSelected = { presenter.onTicketOptionSelected(ticketOption.id) },
                                onBookTicket = { 
                                    presenter.onBookTicket(ticketOption.id)
                                    onNavigateToOrderForm(ticketOption.id)
                                },
                                onInsuranceClick = { presenter.onInsuranceClicked() }
                            )
                        }
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
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBarSection(onBack: () -> Unit) {
        val routeTitle = cabinTypeData?.flightInfo?.route ?: "上海 — 北京"
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按钮
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF333333)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 标题
                Text(
                    text = routeTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
                
                // 右侧按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { presenter.onReminderClicked() }
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "提醒",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF666666)
                        )
                        Text(
                            text = "提醒",
                            fontSize = 10.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { presenter.onMoreOptionsClicked() }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "更多",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF666666)
                        )
                        Text(
                            text = "更多",
                            fontSize = 10.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FlightBasicInfoSection(
        flightInfo: FlightBasicInfo,
        onFavoriteClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 日期和收藏按钮行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = flightInfo.date,
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "信息",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4A90E2)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "部分充电宝禁止携带提醒",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    
                    Icon(
                        if (flightInfo.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "收藏",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onFavoriteClick() },
                        tint = if (flightInfo.isFavorite) Color(0xFFFF4081) else Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 航班时间和航站楼信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛫",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    Text(
                        text = "${flightInfo.departureTime} — ${flightInfo.arrivalTime}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Text(
                        text = "${flightInfo.departure}-${flightInfo.arrival}",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 航班号和服务信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${flightInfo.airline}${flightInfo.flightNumber}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    flightInfo.services.forEach { service ->
                        Text(
                            text = service,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        if (service != flightInfo.services.last()) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TravelTipSection(
        travelTip: TravelTip,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${travelTip.title}:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = travelTip.content,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (travelTip.isExpandable) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "展开",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF666666)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MembershipBenefitSection(
        membershipBenefit: MembershipBenefit,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💎",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${membershipBenefit.title}:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A90E2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = membershipBenefit.benefits,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.weight(1f)
                )
                if (membershipBenefit.isUpgradeable) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "升级",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF666666)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CabinTypeTabsSection(
        cabinTypes: List<CabinTypeOption>,
        selectedCabinType: String,
        onCabinTypeSelected: (String) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                cabinTypes.forEach { cabinType ->
                    Column(
                        modifier = Modifier.clickable { onCabinTypeSelected(cabinType.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cabinType.name,
                                fontSize = 16.sp,
                                fontWeight = if (cabinType.id == selectedCabinType) FontWeight.Bold else FontWeight.Normal,
                                color = if (cabinType.id == selectedCabinType) Color(0xFF333333) else Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cabinType.priceFrom,
                                fontSize = 14.sp,
                                color = Color(0xFF4A90E2)
                            )
                            cabinType.description?.let { desc ->
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = desc,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(
                                            Color(0xFFFF6600),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        
                        if (cabinType.id == selectedCabinType) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(2.dp)
                                    .background(Color(0xFF4A90E2))
                            )
                        }
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TicketOptionCard(
        ticketOption: TicketOption,
        onTicketSelected: () -> Unit,
        onBookTicket: () -> Unit,
        onInsuranceClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTicketSelected() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 特殊标签（如人群特惠）
                ticketOption.specialTag?.let { tag ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👥",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            color = Color(0xFF4A90E2),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // 左侧价格和信息
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // 价格
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🛫",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = ticketOption.price,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4A90E2)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 行李信息
                        Text(
                            text = ticketOption.baggageInfo,
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                        
                        // 退改签信息
                        Text(
                            text = "${ticketOption.refundInfo},${ticketOption.changeInfo}",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        
                        // 折扣信息
                        Text(
                            text = ticketOption.discount,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // 积分和优惠信息
                        ticketOption.pointsInfo?.let { points ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎁",
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = points,
                                    fontSize = 12.sp,
                                    color = Color(0xFF4A90E2)
                                )
                            }
                        }
                        
                        // 其他优惠
                        if (ticketOption.benefits.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎯",
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = ticketOption.benefits.joinToString(" "),
                                    fontSize = 12.sp,
                                    color = Color(0xFF4A90E2)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 限制条件
                        ticketOption.restrictions?.let { restrictions ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = restrictions,
                                    fontSize = 10.sp,
                                    color = Color(0xFF666666),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    
                    // 右侧按钮
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Button(
                            onClick = onBookTicket,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ticketOption.buttonType == TicketButtonType.BOOK) 
                                    Color(0xFFFF6600) else Color(0xFF4A90E2)
                            ),
                            modifier = Modifier.width(60.dp)
                        ) {
                            Text(
                                text = ticketOption.buttonText,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // 保险选项
                ticketOption.insurancePrice?.let { insurance ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "全能保障服务",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = insurance,
                                fontSize = 12.sp,
                                color = Color(0xFF4A90E2)
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅ 改期立减",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✅ 延误补贴",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "✅ 航班取消",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "展开",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onInsuranceClick() },
                                tint = Color(0xFF666666)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "全能保障服务 更换",
                        fontSize = 12.sp,
                        color = Color(0xFF4A90E2),
                        modifier = Modifier.clickable { onInsuranceClick() }
                    )
                }
            }
        }
    }
    
    // View interface implementations
    override fun showCabinTypeData(data: CabinTypeSelectData) {
        cabinTypeData = data
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
    
    override fun navigateToOrderForm(ticketOptionId: String) {
        // 导航到订单表单页面
    }
    
    override fun onBack() {
        // 返回上一页
    }
    
    override fun showTravelTipDetails() {
        Toast.makeText(context, "行程提示详情", Toast.LENGTH_SHORT).show()
    }
    
    override fun showMembershipUpgrade() {
        Toast.makeText(context, "会员升级", Toast.LENGTH_SHORT).show()
    }
    
    override fun showInsuranceDetails() {
        Toast.makeText(context, "保险详情", Toast.LENGTH_SHORT).show()
    }
    
    override fun showReminderSettings() {
        Toast.makeText(context, "提醒设置", Toast.LENGTH_SHORT).show()
    }
    
    override fun showMoreOptions() {
        Toast.makeText(context, "更多选项", Toast.LENGTH_SHORT).show()
    }
    
    override fun toggleFavorite() {
        Toast.makeText(context, "收藏状态已切换", Toast.LENGTH_SHORT).show()
    }
}