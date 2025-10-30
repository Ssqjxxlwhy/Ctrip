package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class TrainPaymentSuccessTabView(private val context: Context) : TrainPaymentSuccessTabContract.View {

    private lateinit var presenter: TrainPaymentSuccessTabContract.Presenter
    private var successData by mutableStateOf<TrainPaymentSuccessTabContract.PaymentSuccessData?>(null)
    private var isLoading by mutableStateOf(false)
    private var continueShoppingCallback: (() -> Unit)? = null

    fun initialize(paymentData: TicketPaymentTabContract.PaymentData) {
        val model = TrainPaymentSuccessTabModel(context)
        presenter = TrainPaymentSuccessTabPresenter(model)
        presenter.attachView(this)
        presenter.loadPaymentSuccessData(paymentData)
    }

    @Composable
    fun TrainPaymentSuccessTabScreen(onClose: () -> Unit = {}, onContinueShopping: () -> Unit = {}) {
        // 保存回调到成员变量
        LaunchedEffect(Unit) {
            continueShoppingCallback = onContinueShopping
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            successData?.let { data ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 顶部返回按钮
                    TopBar(onClose)

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))

                    // 成功图标
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFF4CAF50), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "成功",
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 支付成功文字
                    Text(
                        text = "支付成功",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 支付金额
                    Text(
                        text = "¥${data.paymentAmount}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A90E2)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 支付方式
                    Text(
                        text = data.paymentMethod,
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // 订单信息卡片
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "订单已生成",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "您的火车票订单已提交成功",
                                fontSize = 14.sp,
                                color = Color(0xFF666666)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 订单详情
                            OrderDetailRow("订单号", data.orderId.takeLast(12))
                            Spacer(modifier = Modifier.height(8.dp))
                            OrderDetailRow("车次", data.trainNumber)
                            Spacer(modifier = Modifier.height(8.dp))
                            OrderDetailRow("出行日期", data.departureDate)
                            Spacer(modifier = Modifier.height(8.dp))
                            OrderDetailRow("出发时间", data.departureTime)
                            Spacer(modifier = Modifier.height(8.dp))
                            OrderDetailRow("乘客", data.passengerName)
                            Spacer(modifier = Modifier.height(8.dp))
                            OrderDetailRow("座位", data.seatType)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 按钮区域
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 查看订单按钮
                        OutlinedButton(
                            onClick = { presenter.onViewOrderClicked() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF4A90E2)
                            )
                        ) {
                            Text(
                                text = "查看订单",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // 继续购票按钮
                        Button(
                            onClick = { presenter.onContinueShoppingClicked() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4A90E2)
                            )
                        ) {
                            Text(
                                text = "继续购票",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
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

    @Composable
    private fun TopBar(onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onClose() },
                    tint = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "支付结果",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
        }
    }

    @Composable
    private fun OrderDetailRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color(0xFF999999)
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
        }
    }

    // View interface implementations
    override fun showPaymentSuccess(data: TrainPaymentSuccessTabContract.PaymentSuccessData) {
        successData = data
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

    override fun navigateToOrderDetail() {
        Toast.makeText(context, "查看订单详情", Toast.LENGTH_SHORT).show()
    }

    override fun navigateToHome() {
        continueShoppingCallback?.invoke() ?: run {
            Toast.makeText(context, "返回首页", Toast.LENGTH_SHORT).show()
        }
    }
}
