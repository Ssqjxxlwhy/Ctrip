package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ServiceSelectTabView(private val context: Context) : ServiceSelectTabContract.View {

    private lateinit var presenter: ServiceSelectTabContract.Presenter
    private var serviceSelectData by mutableStateOf<ServiceSelectData?>(null)
    private var isLoading by mutableStateOf(false)
    private var currentTotalPrice by mutableStateOf(0)

    fun initialize(orderFormData: OrderFormData) {
        val model = ServiceSelectTabModel(context)
        presenter = ServiceSelectTabPresenter(model)
        presenter.attachView(this)
        presenter.loadServiceSelectData(orderFormData)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ServiceSelectTabScreen(
        onNavigateToPayment: (ServiceSelectData) -> Unit = {},
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
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    serviceSelectData?.let { data ->
                        // 航班信息和价格
                        item {
                            FlightPriceSection(
                                flightInfo = data.flightInfo,
                                totalPrice = currentTotalPrice,
                                onDetailsClick = { presenter.onPriceDetailsClicked() }
                            )
                        }

                        // 推广横幅
                        item {
                            PromotionBannerSection(banner = data.promotionBanner)
                        }

                        // 服务选项列表
                        items(data.serviceOptions.size) { index ->
                            val service = data.serviceOptions[index]
                            ServiceOptionCard(
                                service = service,
                                onToggle = { presenter.onServiceToggled(service.id) },
                                onInfoClick = { presenter.onServiceInfoClicked(service.id) }
                            )
                        }

                        // 查看更多服务
                        item {
                            ShowMoreServicesSection(
                                onClick = { presenter.onShowMoreServicesClicked() }
                            )
                        }

                        // 报销凭证信息
                        item {
                            ReceiptInfoSection(
                                receiptInfo = data.paymentInfo.receiptInfo,
                                onClick = { presenter.onReceiptInfoClicked() }
                            )
                        }

                        // 协议勾选
                        item {
                            AgreementSection(
                                agreements = data.agreements,
                                onAgreementToggled = { agreementId ->
                                    presenter.onAgreementToggled(agreementId)
                                },
                                onAgreementClicked = { agreementId ->
                                    presenter.onAgreementClicked(agreementId)
                                }
                            )
                        }
                    }
                }

                // 底部支付栏
                serviceSelectData?.let { data ->
                    BottomPaymentSection(
                        paymentInfo = data.paymentInfo.copy(totalPrice = currentTotalPrice),
                        onPaymentClick = {
                            presenter.onPaymentClicked()
                            onNavigateToPayment(data)
                        }
                    )
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

                Spacer(modifier = Modifier.width(16.dp))

                // 流程导航
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "填订单",
                        fontSize = 16.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = " → ",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = "选服务",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A90E2)
                    )
                    Text(
                        text = " → ",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = "支付",
                        fontSize = 16.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FlightPriceSection(
        flightInfo: ServiceFlightInfo,
        totalPrice: Int,
        onDetailsClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onDetailsClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¥$totalPrice",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${flightInfo.route}  ${flightInfo.date}",
                    fontSize = 16.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "展开",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF999999)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PromotionBannerSection(banner: ServicePromotionBanner) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = banner.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = banner.subtitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A90E2)
                    )
                }

                // 插图（使用emoji代替）
                Text(
                    text = "🛂",
                    fontSize = 60.sp
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ServiceOptionCard(
        service: ServiceOption,
        onToggle: () -> Unit,
        onInfoClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 标题行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    if (service.hasInfo) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "说明",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onInfoClick() },
                            tint = Color(0xFF999999)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 价格和选择按钮
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = service.price,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                        if (service.priceNote.isNotEmpty()) {
                            Text(
                                text = service.priceNote,
                                fontSize = 10.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 选择圆圈
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(
                                2.dp,
                                if (service.isSelected) Color(0xFF4A90E2) else Color(0xFFCCCCCC),
                                CircleShape
                            )
                            .clickable { onToggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (service.isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFF4A90E2), CircleShape)
                            )
                        }
                    }
                }

                // 描述文字
                if (service.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = service.description,
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        lineHeight = 18.sp
                    )
                }

                // 特性列表
                if (service.features.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        service.features.chunked(2).forEach { row ->
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { feature ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = Color(0xFF4CAF50)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = feature,
                                            fontSize = 12.sp,
                                            color = Color(0xFF666666)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 额外说明信息
                service.additionalInfo?.let { info ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF999999)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = info,
                            fontSize = 10.sp,
                            color = Color(0xFF999999),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ShowMoreServicesSection(onClick: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "查看更多服务",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "展开",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF666666)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ReceiptInfoSection(
        receiptInfo: ReceiptInfo,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF4A90E2)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = receiptInfo.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = receiptInfo.description,
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    lineHeight = 16.sp
                )
            }
        }
    }

    @Composable
    private fun AgreementSection(
        agreements: List<Agreement>,
        onAgreementToggled: (String) -> Unit,
        onAgreementClicked: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 统一的勾选框
            val allChecked = agreements.all { it.isChecked }
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        1.dp,
                        if (allChecked) Color(0xFF4A90E2) else Color(0xFFCCCCCC),
                        RoundedCornerShape(4.dp)
                    )
                    .background(
                        if (allChecked) Color(0xFF4A90E2) else Color.Transparent,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable {
                        // 切换所有协议的勾选状态
                        agreements.forEach { onAgreementToggled(it.id) }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (allChecked) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "已同意",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 协议文本
            Column {
                Text(
                    text = buildString {
                        append("我已阅读并同意")
                        agreements.forEachIndexed { index, agreement ->
                            append(agreement.name)
                            if (index < agreements.size - 1) {
                                append("，")
                            }
                        }
                    },
                    fontSize = 11.sp,
                    color = Color(0xFF666666),
                    lineHeight = 16.sp,
                    modifier = Modifier.clickable {
                        // 点击文本也可以查看协议详情
                        if (agreements.isNotEmpty()) {
                            onAgreementClicked(agreements[0].id)
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BottomPaymentSection(
        paymentInfo: PaymentInfo,
        onPaymentClick: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧价格信息
                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "¥${paymentInfo.totalPrice}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = paymentInfo.cabinType,
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }

                    if (paymentInfo.hasDetails) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "明细",
                                fontSize = 12.sp,
                                color = Color(0xFF4A90E2)
                            )
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "查看明细",
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFF4A90E2)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 右侧支付按钮
                Button(
                    onClick = onPaymentClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(140.dp)
                        .height(50.dp)
                ) {
                    val minutes = paymentInfo.countdownSeconds / 60
                    val seconds = paymentInfo.countdownSeconds % 60
                    Text(
                        text = "去支付 ${String.format("%02d:%02d", minutes, seconds)}",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // View interface implementations
    override fun showServiceSelectData(data: ServiceSelectData) {
        serviceSelectData = data
        currentTotalPrice = data.totalPrice
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

    override fun navigateToPayment(orderData: ServiceSelectData) {
        // 导航到支付页面
    }

    override fun onBack() {
        // 返回上一页
    }

    override fun showServiceInfo(serviceId: String) {
        Toast.makeText(context, "查看服务详情: $serviceId", Toast.LENGTH_SHORT).show()
    }

    override fun showPriceDetails() {
        Toast.makeText(context, "查看价格明细", Toast.LENGTH_SHORT).show()
    }

    override fun showReceiptInfo() {
        Toast.makeText(context, "查看报销凭证信息", Toast.LENGTH_SHORT).show()
    }

    override fun updateServiceSelection(serviceId: String, isSelected: Boolean) {
        serviceSelectData?.let { data ->
            val updatedServices = data.serviceOptions.map { service ->
                if (service.id == serviceId) {
                    service.copy(isSelected = isSelected)
                } else {
                    service
                }
            }
            serviceSelectData = data.copy(serviceOptions = updatedServices)
        }
    }

    override fun updateTotalPrice(newPrice: Int) {
        currentTotalPrice = newPrice
    }

    override fun showCountdownExpired() {
        Toast.makeText(context, "订单已超时，请重新预订", Toast.LENGTH_LONG).show()
    }

    override fun showAgreementDetails(agreementId: String) {
        Toast.makeText(context, "查看协议详情: $agreementId", Toast.LENGTH_SHORT).show()
    }
}
