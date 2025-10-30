package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Ctrip.model.*

class InfoConfirmationTabView(private val context: Context) : InfoConfirmationTabContract.View {

    private lateinit var presenter: InfoConfirmationTabContract.Presenter
    private var confirmationData by mutableStateOf<InfoConfirmationData?>(null)
    private var isLoading by mutableStateOf(false)
    private var showPaymentScreen by mutableStateOf(false)
    private var paymentTabView: PaymentTabView? = null
    private var showHotelPaymentSuccess by mutableStateOf(false)
    private var hotelPaymentSuccessTabView: HotelPaymentSuccessTabView? = null
    
    fun initialize(roomType: RoomType, searchParams: HotelListSearchParams) {
        val model = InfoConfirmationTabModelImpl(context)
        presenter = InfoConfirmationTabPresenter(model)
        presenter.attachView(this)
        presenter.loadInfoConfirmationData(roomType, searchParams)
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InfoConfirmationTabScreen(
        onBackPressed: () -> Unit = {}
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (showHotelPaymentSuccess && hotelPaymentSuccessTabView != null) {
                // Show Hotel Payment Success Screen
                hotelPaymentSuccessTabView?.HotelPaymentSuccessTabScreen(
                    onClose = {
                        showHotelPaymentSuccess = false
                        showPaymentScreen = false
                        paymentTabView = null
                        hotelPaymentSuccessTabView = null
                    },
                    onContinueShopping = {
                        // Return to home
                        showHotelPaymentSuccess = false
                        showPaymentScreen = false
                        paymentTabView = null
                        hotelPaymentSuccessTabView = null
                        onBackPressed()
                    }
                )
            } else if (showPaymentScreen && paymentTabView != null) {
                // Show Payment Screen
                paymentTabView?.PaymentTabScreen(
                    onBackPressed = {
                        showPaymentScreen = false
                        paymentTabView = null
                    },
                    onNavigateToPaymentSuccess = { paymentData, confirmData ->
                        // Navigate to payment success
                        val successTabView = HotelPaymentSuccessTabView(context)
                        successTabView.initialize(paymentData, confirmData)
                        hotelPaymentSuccessTabView = successTabView
                        showHotelPaymentSuccess = true
                    }
                )
            } else {
                // Show Confirmation Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ComposeColor(0xFFF5F5F5))
                ) {
                    TopBarSection(onBackPressed)
                    
                    if (confirmationData != null) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { HotelInfoSection() }
                            item { BookingInfoSection() }
                            item { BenefitsSection() }
                            item { UrgencyMessageSection() }
                            item { Spacer(modifier = Modifier.height(80.dp)) } // Bottom button space
                        }
                    }
                }
                
                // Bottom Payment Button
                confirmationData?.let { data ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ) {
                        BottomPaymentContent(data)
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
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBarSection(onBackPressed: () -> Unit = {}) {
        TopAppBar(
            title = {
                Text(
                    text = "确认订单",
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
    private fun HotelInfoSection() {
        confirmationData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Hotel Name
                    Text(
                        text = data.hotelName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Date Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "日期",
                            tint = ComposeColor(0xFF666666),
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = data.checkInInfo.dateRange,
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF333333),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = data.checkInInfo.checkInTime,
                            fontSize = 12.sp,
                            color = ComposeColor(0xFF666666),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = data.checkInInfo.checkOutTime,
                            fontSize = 12.sp,
                            color = ComposeColor(0xFF666666),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Room Type Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = data.roomType.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ComposeColor(0xFF333333)
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = data.roomType.bedInfo + " " + data.roomType.area,
                                fontSize = 12.sp,
                                color = ComposeColor(0xFF666666)
                            )
                        }
                        
                        Text(
                            text = "房型详情 >",
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF4A90E2),
                            modifier = Modifier.clickable { presenter.onRoomTypeDetailsClicked() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Cancel Policy
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "取消政策",
                            tint = ComposeColor(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        Text(
                            text = data.cancelPolicy,
                            fontSize = 12.sp,
                            color = ComposeColor(0xFF4CAF50)
                        )
                        
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        Text(
                            text = "立即确认",
                            fontSize = 12.sp,
                            color = ComposeColor(0xFF4A90E2),
                            modifier = Modifier
                                .background(
                                    ComposeColor(0xFF4A90E2).copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp, 2.dp)
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun BookingInfoSection() {
        confirmationData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "订房信息",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor(0xFF333333)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Room Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "间数",
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF333333)
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { presenter.onRoomCountDecrease() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "减少",
                                    tint = ComposeColor(0xFF666666)
                                )
                            }
                            
                            Text(
                                text = "${data.roomCount}间",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            
                            IconButton(
                                onClick = { presenter.onRoomCountIncrease() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "增加",
                                    tint = ComposeColor(0xFF666666)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Guest Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "住客姓名",
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF333333)
                        )
                        
                        Text(
                            text = data.guestName,
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF666666)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Contact Phone
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "联系手机",
                                fontSize = 14.sp,
                                color = ComposeColor(0xFF333333)
                            )
                            
                            Text(
                                text = data.contactPhone,
                                fontSize = 14.sp,
                                color = ComposeColor(0xFF666666)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "请注意是否用此号码接收订单信息",
                            fontSize = 12.sp,
                            color = ComposeColor(0xFF999999)
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun BenefitsSection() {
        confirmationData?.let { data ->
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
                        Text(
                            text = "本单可享",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ComposeColor(0xFF333333)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "已享最大优惠¥${data.priceBreakdown.discountAmount}",
                            fontSize = 12.sp,
                            color = ComposeColor(0xFFFF5722),
                            modifier = Modifier
                                .background(
                                    ComposeColor(0xFFFF5722).copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp, 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Promotions
                    data.promotions.forEach { promotion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = promotion.title,
                                fontSize = 14.sp,
                                color = ComposeColor(0xFF333333),
                                modifier = Modifier.weight(1f)
                            )
                            
                            Text(
                                text = "-¥${promotion.discountAmount}",
                                fontSize = 14.sp,
                                color = ComposeColor(0xFFFF5722)
                            )
                        }
                    }
                    
                    // Gifts
                    data.gifts.forEach { gift ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "礼品",
                                tint = ComposeColor(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = gift.description,
                                fontSize = 14.sp,
                                color = ComposeColor(0xFF333333),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    // Benefits
                    data.benefits.forEach { benefit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = benefit.title,
                                fontSize = 14.sp,
                                color = ComposeColor(0xFF333333),
                                modifier = Modifier.weight(1f)
                            )
                            
                            Text(
                                text = benefit.description,
                                fontSize = 12.sp,
                                color = ComposeColor(0xFF666666)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Points Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.pointsInfo.description,
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF333333),
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = "${data.pointsInfo.earnPoints}积分下次预订可抵¥${data.pointsInfo.value}",
                            fontSize = 12.sp,
                            color = ComposeColor(0xFF666666)
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun UrgencyMessageSection() {
        confirmationData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = ComposeColor(0xFFFFF3E0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "提示",
                        tint = ComposeColor(0xFFFF5722),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = data.urgencyMessage,
                        fontSize = 14.sp,
                        color = ComposeColor(0xFFFF5722),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    
    @Composable
    private fun BottomPaymentContent(data: InfoConfirmationData) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "¥${data.priceBreakdown.finalPrice}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ComposeColor(0xFFFF5722)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "查看明细",
                        fontSize = 12.sp,
                        color = ComposeColor(0xFF4A90E2),
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .clickable { presenter.onPriceDetailsClicked() }
                    )
                }
                
                Text(
                    text = data.priceBreakdown.description,
                    fontSize = 12.sp,
                    color = ComposeColor(0xFF666666)
                )
            }
            
            Button(
                onClick = { presenter.onPaymentClicked() },
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ComposeColor(0xFF4A90E2)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(
                    text = "立即支付",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = ComposeColor.White
                )
            }
        }
    }
    
    override fun showInfoConfirmationData(data: InfoConfirmationData) {
        confirmationData = data
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
    
    override fun onRoomCountChanged(count: Int) {
        // Update UI automatically through state change
    }
    
    override fun onGuestNameChanged(name: String) {
        // Update UI automatically through state change
    }
    
    override fun onContactPhoneChanged(phone: String) {
        // Update UI automatically through state change
    }
    
    override fun onPriceDetailsClicked() {
        Toast.makeText(context, "查看价格明细", Toast.LENGTH_SHORT).show()
    }
    
    override fun onRoomTypeDetailsClicked() {
        Toast.makeText(context, "查看房型详情", Toast.LENGTH_SHORT).show()
    }
    
    override fun onPaymentClicked() {
        // This method is kept for compatibility but actual navigation is handled by navigateToPayment
    }
    
    override fun navigateToPayment(confirmationData: InfoConfirmationData) {
        // Create and initialize PaymentTab
        val paymentTabView = PaymentTabView(context)
        paymentTabView.initialize(confirmationData)
        
        // Store reference and show payment screen
        this.paymentTabView = paymentTabView
        this.showPaymentScreen = true
    }
    
    override fun onBackPressed() {
        Toast.makeText(context, "返回房型详情", Toast.LENGTH_SHORT).show()
    }
}