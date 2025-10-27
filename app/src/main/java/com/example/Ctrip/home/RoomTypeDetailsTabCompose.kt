package com.example.Ctrip.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun RoomTypeDetailsTabScreen(
    roomType: RoomType,
    searchParams: HotelListSearchParams,
    onBackPressed: () -> Unit = {},
    onNavigateToInfoConfirmation: (RoomType, HotelListSearchParams) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val model = remember { RoomTypeDetailsTabModelImpl(context) }
    val presenter = remember { RoomTypeDetailsTabPresenter(model) }
    
    var roomDetailsData by remember { mutableStateOf<RoomTypeDetailsData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val view = object : RoomTypeDetailsTabContract.View {
        override fun showRoomTypeDetailsData(data: RoomTypeDetailsData) {
            roomDetailsData = data
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
        
        override fun onImageChanged(index: Int) {
            roomDetailsData?.let { data ->
                roomDetailsData = data.copy(currentImageIndex = index)
            }
        }
        
        override fun onFacilitiesExpanded() {
            Toast.makeText(context, "查看全部设施", Toast.LENGTH_SHORT).show()
        }
        
        override fun onGiftDetailsShown() {
            Toast.makeText(context, "查看详情", Toast.LENGTH_SHORT).show()
        }
        
        override fun onSpecialOfferClicked(offer: SpecialOffer) {
            Toast.makeText(context, "特惠：${offer.title}", Toast.LENGTH_SHORT).show()
        }
        
        override fun onContactHotel() {
            Toast.makeText(context, "问酒店", Toast.LENGTH_SHORT).show()
        }
        
        override fun onAddToCart() {
            Toast.makeText(context, "已加入购物车", Toast.LENGTH_SHORT).show()
        }
        
        override fun onBookingClicked() {
            onNavigateToInfoConfirmation(roomType, searchParams)
        }
        
        override fun navigateToInfoConfirmation(roomType: RoomType, searchParams: HotelListSearchParams) {
            onNavigateToInfoConfirmation(roomType, searchParams)
        }
    }
    
    LaunchedEffect(roomType.id) {
        presenter.attachView(view)
        presenter.loadRoomTypeDetails(roomType, searchParams)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        roomDetailsData?.let { data ->
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    // 图片展示区域
                    ImageDisplaySection(
                        images = data.imageGallery,
                        currentIndex = data.currentImageIndex,
                        onBackPressed = {
                            presenter.onBackPressed()
                            onBackPressed()
                        },
                        onChatClicked = { presenter.onChatClicked() },
                        onImageClicked = { index -> presenter.onImageClicked(index) }
                    )
                }
                
                item {
                    // 房型名称
                    RoomTitleSection(roomType = data.roomType)
                }
                
                item {
                    // 房型基础信息
                    RoomFeaturesSection(
                        features = data.roomFeatures
                    )
                }
                
                item {
                    // 床型信息和早餐
                    BedAndBreakfastSection(
                        bedInfo = data.bedInfo,
                        breakfastInfo = data.breakfastInfo
                    )
                }
                
                item {
                    // 查看全部设施按钮
                    ViewFacilitiesSection(
                        facilities = data.roomFacilities,
                        onViewAllClicked = { presenter.onViewAllFacilitiesClicked() }
                    )
                }
                
                item {
                    // 礼赠服务
                    GiftServicesSection(
                        giftServices = data.giftServices,
                        onDetailsClicked = { presenter.onGiftDetailsClicked() }
                    )
                }
                
                item {
                    // 首住好礼
                    SpecialOffersSection(
                        offers = data.specialOffers,
                        onOfferClicked = { offer -> presenter.onSpecialOfferClicked(offer) }
                    )
                }
            }
            
            // 底部操作栏
            BottomActionSection(
                priceInfo = data.priceInfo,
                onContactHotelClicked = { presenter.onContactHotelClicked() },
                onAddToCartClicked = { presenter.onAddToCartClicked() },
                onBookingClicked = { presenter.onBookingClicked() }
            )
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
private fun ImageDisplaySection(
    images: List<RoomImage>,
    currentIndex: Int,
    onBackPressed: () -> Unit,
    onChatClicked: () -> Unit,
    onImageClicked: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        // 房间图片区域（模拟）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🛏️",
                fontSize = 80.sp,
                color = Color.Gray
            )
        }
        
        // 顶部工具栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // 关闭按钮
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .clickable { onBackPressed() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // 聊天图标
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .clickable { onChatClicked() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💬",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
        
        // 图片计数器
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${currentIndex + 1}/${images.size}",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun RoomTitleSection(roomType: RoomType) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = roomType.name,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun RoomFeaturesSection(features: List<RoomFeature>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        LazyRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(features) { feature ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = feature.icon,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = feature.title,
                        fontSize = 10.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun BedAndBreakfastSection(
    bedInfo: BedInfo,
    breakfastInfo: BreakfastInfo
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 床型信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛏️",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${bedInfo.bedType}${bedInfo.bedSize}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = bedInfo.extraBedPolicy,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
            
            // 早餐信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🍽️",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = breakfastInfo.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ViewFacilitiesSection(
    facilities: List<RoomFacility>,
    onViewAllClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            onClick = onViewAllClicked,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF666666)
            )
        ) {
            Text(
                text = "查看全部设施",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "展开",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun GiftServicesSection(
    giftServices: List<GiftService>,
    onDetailsClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    text = "礼赠 共${giftServices.size}项",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                TextButton(
                    onClick = onDetailsClicked
                ) {
                    Text(
                        text = "查看详情",
                        fontSize = 12.sp,
                        color = Color(0xFF4A90E2)
                    )
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "查看详情",
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            giftServices.forEach { service ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = service.title,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = service.quantity,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialOffersSection(
    offers: List<SpecialOffer>,
    onOfferClicked: (SpecialOffer) -> Unit
) {
    offers.forEach { offer ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onOfferClicked(offer) },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = offer.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B6B)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = offer.subtitle,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = offer.description,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Icon(
                    Icons.Default.Info,
                    contentDescription = "详情",
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomActionSection(
    priceInfo: PriceInfo,
    onContactHotelClicked: () -> Unit,
    onAddToCartClicked: () -> Unit,
    onBookingClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 问酒店
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onContactHotelClicked() }
            ) {
                Text(
                    text = "💬",
                    fontSize = 24.sp,
                    color = Color(0xFF4A90E2)
                )
                Text(
                    text = "问酒店",
                    fontSize = 10.sp,
                    color = Color(0xFF4A90E2)
                )
            }
            
            // 价格信息
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                priceInfo.originalPrice?.let { originalPrice ->
                    Text(
                        text = "¥$originalPrice",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "¥",
                        fontSize = 16.sp,
                        color = Color(0xFFFF4444)
                    )
                    Text(
                        text = priceInfo.currentPrice.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4444)
                    )
                    Text(
                        text = "起",
                        fontSize = 12.sp,
                        color = Color(0xFFFF4444)
                    )
                }
                
                Text(
                    text = priceInfo.priceDescription,
                    fontSize = 10.sp,
                    color = Color(0xFF4A90E2)
                )
            }
            
            // 操作按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 加购物车
                OutlinedButton(
                    onClick = onAddToCartClicked,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF4A90E2)
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        "加购物车",
                        fontSize = 12.sp
                    )
                }
                
                // 在线付
                Button(
                    onClick = onBookingClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        "在线付",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}