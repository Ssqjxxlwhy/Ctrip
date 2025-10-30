package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TicketPaymentTabView(private val context: Context) : TicketPaymentTabContract.View {

    private lateinit var presenter: TicketPaymentTabContract.Presenter
    private var paymentData by mutableStateOf<TicketPaymentTabContract.PaymentData?>(null)
    private var isLoading by mutableStateOf(false)
    private var showPriceDetails by mutableStateOf(false)
    private var paymentSuccessCallback: ((TicketPaymentTabContract.PaymentData) -> Unit)? = null

    fun initialize(confirmData: InfoConfirmData) {
        val model = TicketPaymentTabModel(context)
        presenter = TicketPaymentTabPresenter(model)
        presenter.attachView(this)
        presenter.loadPaymentData(confirmData)
    }

    @Composable
    fun TicketPaymentTabScreen(
        onClose: () -> Unit = {},
        onNavigateToPaymentSuccess: (TicketPaymentTabContract.PaymentData) -> Unit = {}
    ) {
        // 保存回调到成员变量
        LaunchedEffect(Unit) {
            paymentSuccessCallback = onNavigateToPaymentSuccess
        }
        Box(modifier = Modifier.fillMaxSize()) {
            paymentData?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                ) {
                    // 顶部区域
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        TopBar(data, onClose)
                        Spacer(modifier = Modifier.height(16.dp))
                        TripInfoCard(data)
                        Spacer(modifier = Modifier.height(16.dp))
                        CancelOrderButton()
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 服务保障区域
                    ServiceGuaranteeSection()

                    Spacer(modifier = Modifier.weight(1f))

                    // 底部支付栏
                    BottomPaymentBar(data)
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
    private fun TopBar(data: TicketPaymentTabContract.PaymentData, onClose: () -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onClose() }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "待支付",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "建议您尽快完成支付",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            Icon(
                Icons.Default.Info,
                contentDescription = "预订须知",
                modifier = Modifier
                    .size(24.dp)
                    .clickable { presenter.onBookingNotesClicked() },
                tint = Color(0xFF666666)
            )
        }
    }

    @Composable
    private fun TripInfoCard(data: TicketPaymentTabContract.PaymentData) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 时间和日期信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${data.departureTime}—${data.arrivalTime}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = data.departureDate,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = data.duration,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = data.trainNumber,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 车站信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.departureStation,
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF999999)
                    )
                    Text(
                        text = data.arrivalStation,
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 乘客和座位信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "乘客 ${data.passengerName}",
                            fontSize = 13.sp,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = "${data.seatType} ¥${data.ticketPrice}",
                            fontSize = 13.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CancelOrderButton() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    Color(0xFFF5F5F5),
                    RoundedCornerShape(22.dp)
                )
                .clickable { presenter.onCancelOrderClicked() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "取消订单",
                fontSize = 16.sp,
                color = Color(0xFF666666)
            )
        }
    }

    @Composable
    private fun ServiceGuaranteeSection() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "安心订 放心行",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceItem("退改透明", Icons.Default.Info)
                    ServiceItem("售后保障", Icons.Default.CheckCircle)
                    ServiceItem("出行安心", Icons.Default.Star)
                }
            }
        }
    }

    @Composable
    private fun ServiceItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(80.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF4A90E2)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    private fun BottomPaymentBar(data: TicketPaymentTabContract.PaymentData) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 价格明细
            if (showPriceDetails) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        PriceDetailItem("票价", "¥${data.ticketPrice}")
                        PriceDetailItem("保险", "¥${data.insurancePrice}")
                        PriceDetailItem("优惠", "-¥${data.discountAmount}", Color(0xFFFF6600))
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        PriceDetailItem("总计", "¥${data.totalPrice}", Color(0xFF333333), fontWeight = true)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "¥${data.finalPrice}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6600)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "已优惠¥${data.discountAmount}",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showPriceDetails = !showPriceDetails }
                    ) {
                        Text(
                            text = "明细",
                            fontSize = 12.sp,
                            color = Color(0xFF4A90E2)
                        )
                        Icon(
                            if (showPriceDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }

                Button(
                    onClick = { presenter.onPayClicked() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6600)
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .width(120.dp)
                ) {
                    Text(
                        text = "去支付",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    private fun PriceDetailItem(
        label: String,
        price: String,
        color: Color = Color(0xFF666666),
        fontWeight: Boolean = false
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = color
            )
            Text(
                text = price,
                fontSize = 14.sp,
                fontWeight = if (fontWeight) FontWeight.Bold else FontWeight.Normal,
                color = color
            )
        }
    }

    // View interface implementations
    override fun showPaymentData(data: TicketPaymentTabContract.PaymentData) {
        paymentData = data
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
        // Handle by parent
    }

    override fun navigateToPaymentSuccess() {
        paymentData?.let { data ->
            paymentSuccessCallback?.invoke(data) ?: run {
                Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun showBookingNotes() {
        Toast.makeText(context, "预订须知", Toast.LENGTH_SHORT).show()
    }

    override fun showPriceDetails() {
        showPriceDetails = !showPriceDetails
    }

    override fun cancelOrder() {
        Toast.makeText(context, "取消订单", Toast.LENGTH_SHORT).show()
    }
}