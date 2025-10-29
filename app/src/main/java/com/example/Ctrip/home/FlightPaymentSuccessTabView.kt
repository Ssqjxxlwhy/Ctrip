package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

class FlightPaymentSuccessTabView(private val context: Context) : FlightPaymentSuccessTabContract.View {

    private lateinit var presenter: FlightPaymentSuccessTabContract.Presenter
    private var paymentSuccessData by mutableStateOf<FlightPaymentSuccessData?>(null)
    private var isLoading by mutableStateOf(false)

    fun initialize(serviceData: ServiceSelectData) {
        val model = FlightPaymentSuccessTabModel(context)
        presenter = FlightPaymentSuccessTabPresenter(model)
        presenter.attachView(this)
        presenter.loadPaymentSuccessData(serviceData)
    }

    @Composable
    fun FlightPaymentSuccessTabScreen(
        onNavigateToOrderList: () -> Unit = {},
        onNavigateToHome: () -> Unit = {},
        onBack: () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 顶部返回按钮
                TopBarSection(onBack = onBack)

                Spacer(modifier = Modifier.height(40.dp))

                paymentSuccessData?.let { data ->
                    // 成功图标
                    SuccessIconSection()

                    Spacer(modifier = Modifier.height(32.dp))

                    // 支付成功文字
                    Text(
                        text = "支付成功",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 支付金额
                    Text(
                        text = "¥${data.paymentInfo.amount}.00",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A90E2)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 支付方式
                    Text(
                        text = data.paymentInfo.paymentMethod,
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // 订单信息卡片
                    OrderInfoSection(data.orderInfo)

                    Spacer(modifier = Modifier.height(48.dp))

                    // 按钮区域
                    ButtonSection(
                        onViewOrder = {
                            presenter.onViewOrderClicked()
                            onNavigateToOrderList()
                        },
                        onContinueShopping = {
                            presenter.onContinueShoppingClicked()
                            onNavigateToHome()
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

                // 标题
                Text(
                    text = "支付成功",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    private fun SuccessIconSection() {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFF4A90E2), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "支付成功",
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun OrderInfoSection(orderInfo: OrderSuccessInfo) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 订单状态
                Text(
                    text = orderInfo.status,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 订单描述
                Text(
                    text = orderInfo.statusDescription,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 航班信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "航班信息：",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = orderInfo.flightInfo,
                        fontSize = 14.sp,
                        color = Color(0xFF333333),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 预计送达时间
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "预计出票：",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = orderInfo.estimatedDate,
                        fontSize = 14.sp,
                        color = Color(0xFF4A90E2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    @Composable
    private fun ButtonSection(
        onViewOrder: () -> Unit,
        onContinueShopping: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 查看订单按钮
            OutlinedButton(
                onClick = onViewOrder,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF4A90E2)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "查看订单",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 继续购物按钮
            Button(
                onClick = onContinueShopping,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "继续购物",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }

    // View interface implementations
    override fun showPaymentSuccessData(data: FlightPaymentSuccessData) {
        paymentSuccessData = data
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

    override fun navigateToOrderList() {
        Toast.makeText(context, "跳转到订单列表", Toast.LENGTH_SHORT).show()
    }

    override fun navigateToHome() {
        // 导航到首页
    }

    override fun onBack() {
        // 返回上一页
    }
}
