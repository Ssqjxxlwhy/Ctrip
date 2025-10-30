package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PaymentTabView(private val context: Context) : PaymentTabContract.View {

    private lateinit var presenter: PaymentTabContract.Presenter
    private var paymentData by mutableStateOf<PaymentData?>(null)
    private var isLoading by mutableStateOf(false)
    private var confirmationData: InfoConfirmationData? = null
    private var paymentSuccessCallback: ((PaymentData, InfoConfirmationData) -> Unit)? = null

    fun initialize(confirmationData: InfoConfirmationData) {
        this.confirmationData = confirmationData
        val model = PaymentTabModelImpl(context)
        presenter = PaymentTabPresenter(model)
        presenter.attachView(this)
        presenter.loadPaymentData(confirmationData)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun PaymentTabScreen(
        onBackPressed: () -> Unit = {},
        onNavigateToPaymentSuccess: (PaymentData, InfoConfirmationData) -> Unit = { _, _ -> }
    ) {
        // 保存回调到成员变量
        LaunchedEffect(Unit) {
            paymentSuccessCallback = onNavigateToPaymentSuccess
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ComposeColor(0xFFF5F5F5))
            ) {
                TopBarSection(onBackPressed)
                
                if (paymentData != null) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { PaymentHeaderSection() }
                        item { PaymentMethodsSection() }
                        item { FinancialServicesSection() }
                        item { Spacer(modifier = Modifier.height(80.dp)) } // Bottom button space
                    }
                }
            }
            
            // Bottom Payment Button
            paymentData?.let { data ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    BottomPaymentButton(data)
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
    private fun TopBarSection(onBackPressed: () -> Unit = {}) {
        TopAppBar(
            title = {
                Text(
                    text = "安全收银台",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = ComposeColor(0xFF333333)
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = ComposeColor.White
            )
        )
    }
    
    @Composable
    private fun PaymentHeaderSection() {
        paymentData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Remaining Time
                    Text(
                        text = "剩余时间 ${data.remainingTime}",
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF666666)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Payment Amount
                    Text(
                        text = "¥${data.amount}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor(0xFFFF5722)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Discount Info
                    Text(
                        text = "立减1-30元",
                        fontSize = 12.sp,
                        color = ComposeColor(0xFF4CAF50),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ComposeColor(0xFF4CAF50).copy(alpha = 0.1f))
                            .padding(8.dp, 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Hotel Info
                    Text(
                        text = "${data.hotelName} 详情",
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    
    @Composable
    private fun PaymentMethodsSection() {
        paymentData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "支付方式",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Ctrip Payment Section
                    Column {
                        Text(
                            text = "程支付",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ComposeColor(0xFF333333)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        data.paymentMethods.filter { it.type == PaymentMethodType.CTRIP_PAY || it.type == PaymentMethodType.BANK_CARD }
                            .forEach { method ->
                                PaymentMethodItem(method)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Alipay
                    data.paymentMethods.filter { it.type == PaymentMethodType.ALIPAY }
                        .forEach { method ->
                            PaymentMethodItem(method)
                        }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Other Payment Methods
                    Text(
                        text = "其他支付方式",
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF4A90E2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { presenter.onOtherPaymentMethodsClicked() }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
    
    @Composable
    private fun PaymentMethodItem(method: PaymentMethod) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { presenter.onPaymentMethodClicked(method.id) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio Button
            Icon(
                if (method.isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (method.isSelected) ComposeColor(0xFF4A90E2) else ComposeColor(0xFF666666),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Payment Method Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = method.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = ComposeColor(0xFF333333)
                    )
                    
                    method.discountInfo?.let { discount ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "（$discount）",
                            fontSize = 12.sp,
                            color = ComposeColor(0xFFFF5722)
                        )
                    }
                }
                
                method.description.let { desc ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = desc,
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF666666)
                    )
                }
            }
        }
    }
    
    @Composable
    private fun FinancialServicesSection() {
        paymentData?.let { data ->
            if (data.financialServices.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Header with discount info
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "金融服务",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ComposeColor(0xFF333333)
                            )
                            
                            data.financialServices.firstOrNull()?.discountInfo?.let { discount ->
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "新人专享，$discount",
                                    fontSize = 12.sp,
                                    color = ComposeColor(0xFFFF5722),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ComposeColor(0xFFFF5722).copy(alpha = 0.1f))
                                        .padding(4.dp, 2.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        data.financialServices.forEach { service ->
                            FinancialServiceItem(service)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun FinancialServiceItem(service: FinancialService) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { presenter.onFinancialServiceClicked(service.id) }
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (service.isSelected) ComposeColor(0xFF4A90E2).copy(alpha = 0.1f)
                    else ComposeColor.Transparent
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Radio Button
                Icon(
                    if (service.isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (service.isSelected) ComposeColor(0xFF4A90E2) else ComposeColor(0xFF666666),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = service.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ComposeColor(0xFF333333)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        service.subtitle.let { subtitle ->
                            Text(
                                text = "（$subtitle）",
                                fontSize = 12.sp,
                                color = ComposeColor(0xFF4A90E2)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    service.discountInfo?.let { discount ->
                        Text(
                            text = discount,
                            fontSize = 12.sp,
                            color = ComposeColor(0xFFFF5722)
                        )
                    }
                    
                    // Installment Options
                    service.installmentOptions?.let { options ->
                        Spacer(modifier = Modifier.height(8.dp))
                        options.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        presenter.onInstallmentOptionClicked(service.id, option.months)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = ComposeColor(0xFF666666),
                                    modifier = Modifier.size(16.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Text(
                                    text = option.description,
                                    fontSize = 14.sp,
                                    color = ComposeColor(0xFF666666),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = service.description,
                            fontSize = 12.sp,
                            color = ComposeColor(0xFF999999)
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun BottomPaymentButton(data: PaymentData) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "使用银行卡支付",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ComposeColor(0xFF333333)
                )
                
                Text(
                    text = "¥${data.amount}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ComposeColor(0xFFFF5722)
                )
            }
            
            Button(
                onClick = { presenter.onPaymentConfirmed() },
                modifier = Modifier
                    .width(140.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ComposeColor(0xFFFF5722)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "立即支付 ¥${data.amount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ComposeColor.White
                )
            }
        }
    }
    
    // View interface implementations
    override fun showPaymentData(data: PaymentData) {
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
    
    override fun onPaymentMethodSelected(methodId: String) {
        // UI updates handled through state
    }
    
    override fun onFinancialServiceSelected(serviceId: String) {
        // UI updates handled through state
    }
    
    override fun onInstallmentOptionSelected(serviceId: String, months: Int) {
        // UI updates handled through state
    }
    
    override fun onPaymentConfirmed() {
        // Navigate to payment success page
        paymentData?.let { payment ->
            confirmationData?.let { confirmation ->
                paymentSuccessCallback?.invoke(payment, confirmation) ?: run {
                    Toast.makeText(context, "支付成功！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onBackPressed() {
        // Handle back navigation
    }
    
    override fun navigateBack() {
        // Navigate back to previous screen
    }
}