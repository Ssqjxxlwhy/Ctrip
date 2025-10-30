package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TrainDetailTabView(private val context: Context) : TrainDetailTabContract.View {

    private lateinit var presenter: TrainDetailTabContract.Presenter
    private var trainDetailData by mutableStateOf<TrainDetailData?>(null)
    private var isLoading by mutableStateOf(false)
    private var showInfoConfirm by mutableStateOf(false)
    private var showTicketPayment by mutableStateOf(false)
    private var showPaymentSuccess by mutableStateOf(false)
    private var selectedTicketOption by mutableStateOf<TrainTicketOption?>(null)
    private var currentTrainId by mutableStateOf("")
    private var currentConfirmData by mutableStateOf<InfoConfirmData?>(null)
    private var currentPaymentData by mutableStateOf<TicketPaymentTabContract.PaymentData?>(null)

    fun initialize(trainId: String) {
        currentTrainId = trainId
        val model = TrainDetailTabModel(context)
        presenter = TrainDetailTabPresenter(model)
        presenter.attachView(this)
        presenter.loadTrainDetail(trainId)
    }

    @Composable
    fun TrainDetailTabScreen(onClose: () -> Unit = {}) {
        // 如果显示支付成功页面，则渲染支付成功页面
        if (showPaymentSuccess && currentPaymentData != null) {
            val paymentSuccessView = remember { TrainPaymentSuccessTabView(context) }
            LaunchedEffect(currentPaymentData) {
                paymentSuccessView.initialize(currentPaymentData!!)
            }
            paymentSuccessView.TrainPaymentSuccessTabScreen(
                onClose = {
                    showPaymentSuccess = false
                    currentPaymentData = null
                    // 返回到首页
                    onClose()
                },
                onContinueShopping = {
                    showPaymentSuccess = false
                    currentPaymentData = null
                    // 返回到首页
                    onClose()
                }
            )
            return
        }

        // 如果显示支付页面，则渲染支付页面
        if (showTicketPayment && currentConfirmData != null) {
            val ticketPaymentView = remember { TicketPaymentTabView(context) }
            LaunchedEffect(currentConfirmData) {
                ticketPaymentView.initialize(currentConfirmData!!)
            }
            ticketPaymentView.TicketPaymentTabScreen(
                onClose = {
                    showTicketPayment = false
                    currentConfirmData = null
                },
                onNavigateToPaymentSuccess = { paymentData ->
                    showTicketPayment = false
                    currentPaymentData = paymentData
                    showPaymentSuccess = true
                }
            )
            return
        }

        // 如果显示信息确认页，则渲染信息确认页
        if (showInfoConfirm && selectedTicketOption != null && trainDetailData != null) {
            val infoConfirmView = remember { InfoConfirmTabView(context) }
            LaunchedEffect(selectedTicketOption) {
                val selectedSeatType = trainDetailData!!.seatTypes.find { it.isSelected }?.type ?: "二等座"
                infoConfirmView.initialize(
                    currentTrainId,
                    selectedTicketOption!!,
                    selectedSeatType
                )
            }
            infoConfirmView.InfoConfirmTabScreen(
                onClose = {
                    showInfoConfirm = false
                    selectedTicketOption = null
                },
                onNavigateToPayment = { confirmData ->
                    showInfoConfirm = false
                    selectedTicketOption = null
                    currentConfirmData = confirmData
                    showTicketPayment = true
                }
            )
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            trainDetailData?.let { data ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { TopBar(data, onClose) }
                    item { TrainInfoSection(data) }
                    item { SupportInfoSection(data) }
                    item { NewCustomerGiftBanner() }
                    item { SeatTypeSection(data) }
                    items(data.ticketOptions) { option ->
                        TicketOptionCard(option)
                    }
                    item { BottomNoticeSection() }
                    item { ServiceGuaranteeSection() }
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

    @Composable
    private fun TopBar(data: TrainDetailData, onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按钮
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    modifier = Modifier
                        .clickable { onClose() }
                        .size(24.dp),
                    tint = Color(0xFF333333)
                )

                // 日期信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { }
                ) {
                    Text(
                        text = "${data.departureDate}出发",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 右侧功能按钮
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { presenter.onNoticeClicked() }
                    ) {
                        Text(text = "≡", fontSize = 18.sp)
                        Text(text = "须知", fontSize = 10.sp, color = Color(0xFF666666))
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { presenter.onShareClicked() }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "分享",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "分享", fontSize = 10.sp, color = Color(0xFF666666))
                    }
                }
            }
        }
    }

    @Composable
    private fun TrainInfoSection(data: TrainDetailData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 时间和车站
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 出发时间
                    Column {
                        Text(
                            text = data.departureTime,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = data.departureStation,
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }

                    // 历时
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = data.duration,
                            fontSize = 11.sp,
                            color = Color(0xFF999999)
                        )
                        // 车次
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { }
                        ) {
                            Text(
                                text = "${data.trainNumber} 经停",
                                fontSize = 13.sp,
                                color = Color(0xFF4A90E2)
                            )
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF4A90E2)
                            )
                        }
                    }

                    // 到达时间
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = data.arrivalTime,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = data.arrivalStation,
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SupportInfoSection(data: TrainDetailData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (data.supports12306Points) {
                    SupportChip("✓ 支持12306积分兑换", Color(0xFF4CAF50))
                }
                if (data.hasRefundPolicy) {
                    SupportChip("退改说明", Color(0xFF666666))
                }
                if (data.isHighSpeed) {
                    SupportChip("复兴号", Color(0xFF666666))
                }
            }
        }
    }

    @Composable
    private fun SupportChip(text: String, textColor: Color) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { }
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                color = textColor
            )
            if (!text.startsWith("✓")) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF999999)
                )
            }
        }
    }

    @Composable
    private fun NewCustomerGiftBanner() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable { },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎁", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "新客礼包",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6600)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "优惠 ¥10立减  权益 ¥80VIP抢票、¥25安心退改",
                        fontSize = 10.sp,
                        color = Color(0xFF999999),
                        maxLines = 1
                    )
                }
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF999999)
                )
            }
        }
    }

    @Composable
    private fun SeatTypeSection(data: TrainDetailData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(data.seatTypes) { seatType ->
                    SeatTypeCard(seatType)
                }
            }
        }
    }

    @Composable
    private fun SeatTypeCard(seatType: SeatType) {
        val backgroundColor = if (seatType.isSelected) Color(0xFFE3F2FD) else Color.White
        val borderColor = if (seatType.isSelected) Color(0xFF4A90E2) else Color(0xFFE0E0E0)

        Card(
            modifier = Modifier
                .width(90.dp)
                .clickable { presenter.onSeatTypeSelected(seatType.type) },
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            border = androidx.compose.foundation.BorderStroke(
                width = if (seatType.isSelected) 2.dp else 1.dp,
                color = borderColor
            ),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = seatType.type,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (seatType.discount.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = seatType.discount,
                            fontSize = 10.sp,
                            color = Color(0xFFFF6600)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥${seatType.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6600)
                )
                Text(
                    text = if (seatType.type == "无座") "充足" else "${seatType.availableCount}张",
                    fontSize = 10.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }

    @Composable
    private fun TicketOptionCard(option: TrainTicketOption) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 左侧：价格和优惠信息
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "¥${option.price}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6600)
                        )
                        if (option.specialTag.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VS",
                                fontSize = 12.sp,
                                color = Color(0xFF999999)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "¥${option.price + 25}",
                                fontSize = 14.sp,
                                color = Color(0xFF999999),
                                style = androidx.compose.ui.text.TextStyle(
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 优惠标签
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (option.platformDiscount.isNotEmpty()) {
                            PriceTag("平台立减 ${option.platformDiscount}", Color(0xFFFF6600))
                        }
                        if (option.subsidy.isNotEmpty()) {
                            PriceTag("已补贴 ${option.subsidy}", Color(0xFFFF0000))
                        }
                        if (option.specialTag.isNotEmpty()) {
                            PriceTag(option.specialTag, Color(0xFF4A90E2))
                        }
                    }

                    if (option.insurancePrice.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = option.insurancePrice,
                            fontSize = 11.sp,
                            color = Color(0xFF4A90E2)
                        )
                    }
                }

                // 中间：服务权益
                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(horizontal = 8.dp)
                ) {
                    option.benefits.forEach { benefit ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = if (benefit.contains("退改")) "📋" else if (benefit.contains("视频") || benefit.contains("音乐")) "🎬" else if (benefit.contains("旅行") || benefit.contains("酒店")) "🎁" else "•",
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = benefit,
                                fontSize = 10.sp,
                                color = Color(0xFF666666),
                                maxLines = 1
                            )
                        }
                    }
                }

                // 右侧：订票按钮
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { presenter.onTicketOptionClicked(option.id) }
                ) {
                    Button(
                        onClick = { presenter.onTicketOptionClicked(option.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "订",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "剩${option.availableCount}张",
                        fontSize = 10.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        }
    }

    @Composable
    private fun PriceTag(text: String, color: Color) {
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = text,
                fontSize = 10.sp,
                color = color
            )
        }
    }

    @Composable
    private fun BottomNoticeSection() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "•", fontSize = 12.sp, color = Color(0xFF999999))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "无需取票，直接刷证件进站",
                        fontSize = 11.sp,
                        color = Color(0xFF999999)
                    )
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFF999999)
                    )
                }
            }
        }
    }

    @Composable
    private fun ServiceGuaranteeSection() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "安心订 🚗 放心行",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A90E2)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ServiceItem("😊", "退改透明")
                ServiceItem("🛡️", "售后保障")
                ServiceItem("✈️", "出行安心")
            }
        }
    }

    @Composable
    private fun ServiceItem(icon: String, text: String) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = text, fontSize = 11.sp, color = Color(0xFF666666))
        }
    }

    override fun showTrainDetail(data: TrainDetailData) {
        trainDetailData = data
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

    override fun navigateBack() {
        Toast.makeText(context, "返回", Toast.LENGTH_SHORT).show()
    }

    override fun showRefundPolicy() {
        Toast.makeText(context, "查看退改说明", Toast.LENGTH_SHORT).show()
    }

    override fun showNotice() {
        Toast.makeText(context, "查看须知", Toast.LENGTH_SHORT).show()
    }

    override fun share() {
        Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show()
    }

    override fun navigateToOrderConfirm(ticketOption: TrainTicketOption) {
        selectedTicketOption = ticketOption
        showInfoConfirm = true
    }
}
