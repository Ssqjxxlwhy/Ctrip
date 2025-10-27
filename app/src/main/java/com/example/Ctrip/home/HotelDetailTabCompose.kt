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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.LocalDate

@Composable
fun HotelDetailTabScreen(
    hotelId: String,
    searchParams: HotelListSearchParams,
    onBackPressed: () -> Unit = {},
    onRoomTypeSelected: (RoomType) -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { HotelDetailTabModelImpl(context) }
    val presenter = remember { HotelDetailTabPresenter(model) }
    
    var hotelDetailData by remember { mutableStateOf<HotelDetailData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    
    val view = object : HotelDetailTabContract.View {
        override fun showHotelDetailData(data: HotelDetailData) {
            hotelDetailData = data
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
        
        override fun onRoomTypeSelected(roomType: RoomType) {
            onRoomTypeSelected(roomType)
        }
        
        override fun onImageTabChanged(tab: String) {
            // 图片标签切换，UI已在showHotelDetailData中更新
        }
        
        override fun onDateChanged(checkInDate: LocalDate, checkOutDate: LocalDate) {
            Toast.makeText(context, "日期选择功能", Toast.LENGTH_SHORT).show()
        }
        
        override fun onGuestCountChanged(roomCount: Int, guestCount: Int) {
            Toast.makeText(context, "人数选择功能", Toast.LENGTH_SHORT).show()
        }
        
        override fun updateFavoriteStatus(isFav: Boolean) {
            isFavorite = isFav
            Toast.makeText(context, if (isFav) "已收藏" else "已取消收藏", Toast.LENGTH_SHORT).show()
        }
        
        override fun showShareOptions() {
            Toast.makeText(context, "分享功能", Toast.LENGTH_SHORT).show()
        }
        
        override fun showCartUpdated() {
            Toast.makeText(context, "购物车功能", Toast.LENGTH_SHORT).show()
        }
        
        override fun showMoreOptions() {
            Toast.makeText(context, "更多功能", Toast.LENGTH_SHORT).show()
        }
        
        override fun showContactHotel() {
            Toast.makeText(context, "问酒店", Toast.LENGTH_SHORT).show()
        }
        
        override fun navigateToRoomTypeDetails(roomType: RoomType) {
            onRoomTypeSelected(roomType)
        }
    }
    
    LaunchedEffect(hotelId) {
        presenter.attachView(view)
        presenter.loadHotelDetail(hotelId, searchParams)
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
        hotelDetailData?.let { data ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    // 图片展示区域
                    ImageGallerySection(
                        images = data.imageGallery,
                        currentTab = data.currentImageTab,
                        isFavorite = isFavorite,
                        onBackPressed = onBackPressed,
                        onTabChanged = { tab -> presenter.onImageTabClicked(tab) },
                        onFavoriteClicked = { presenter.onFavoriteClicked() },
                        onShareClicked = { presenter.onShareClicked() },
                        onCartClicked = { presenter.onCartClicked() },
                        onMoreClicked = { presenter.onMoreClicked() }
                    )
                }
                
                item {
                    // 酒店信息区域
                    HotelInfoSection(
                        hotelInfo = data.hotelInfo,
                        amenities = data.amenities
                    )
                }
                
                item {
                    // 评分和位置区域
                    ReviewAndLocationSection(
                        reviewSummary = data.reviewSummary,
                        locationInfo = data.locationInfo,
                        onLocationClicked = { presenter.onLocationClicked() }
                    )
                }
                
                item {
                    // 订房优惠区域
                    PromotionSection(
                        promotions = data.promotions,
                        onPromotionClicked = { promotion -> presenter.onPromotionClicked(promotion) }
                    )
                }
                
                item {
                    // 日期和人数选择区域
                    DateAndGuestSection(
                        searchParams = data.searchParams,
                        onDateClicked = { presenter.onDateClicked() },
                        onGuestCountClicked = { presenter.onGuestCountClicked() }
                    )
                }
                
                item {
                    // 房型筛选标签
                    RoomFilterSection(
                        filterTags = data.filterTags,
                        onTagToggled = { tagId -> presenter.onRoomFilterToggled(tagId) }
                    )
                }
                
                items(data.roomTypes) { roomType ->
                    // 房型推荐区域
                    RoomTypeCard(
                        roomType = roomType,
                        onRoomTypeClicked = { presenter.onRoomTypeClicked(roomType) },
                        onContactHotelClicked = { presenter.onContactHotelClicked() }
                    )
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
private fun ImageGallerySection(
    images: List<HotelImage>,
    currentTab: String,
    isFavorite: Boolean,
    onBackPressed: () -> Unit,
    onTabChanged: (String) -> Unit,
    onFavoriteClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onCartClicked: () -> Unit,
    onMoreClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        // 背景图片区域（模拟）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🏨",
                fontSize = 60.sp,
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
            // 返回按钮
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    .clickable { onBackPressed() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // 右侧图标栏
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 收藏
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable { onFavoriteClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "收藏",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // 分享
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable { onShareClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "分享",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // 购物车
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable { onCartClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "购物车",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // 更多
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .clickable { onMoreClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        // 底部图片标签栏
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val tabs = listOf(
                "cover" to "封面",
                "featured" to "精选",
                "location" to "位置",
                "album" to "相册"
            )
            
            tabs.forEach { (tabId, tabName) ->
                Box(
                    modifier = Modifier
                        .background(
                            if (currentTab == tabId) Color.White else Color.Black.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onTabChanged(tabId) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = tabName,
                        color = if (currentTab == tabId) Color.Black else Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HotelInfoSection(
    hotelInfo: HotelDetailInfo,
    amenities: List<HotelAmenity>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 酒店名称和标识
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hotelInfo.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                // 标识
                hotelInfo.badges.forEach { badge ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (badge.type) {
                                "vip" -> Color(0xFFFFD700)
                                "year" -> Color(0xFF4A90E2)
                                else -> Color(0xFFF0F0F0)
                            }
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = badge.title,
                            modifier = Modifier.padding(6.dp, 3.dp),
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 设施和政策图标栏
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(amenities) { amenity ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            text = amenity.icon,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = amenity.title,
                            fontSize = 10.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewAndLocationSection(
    reviewSummary: ReviewSummary,
    locationInfo: LocationInfo,
    onLocationClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 评分区域
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        Text(
                            text = reviewSummary.rating.toString(),
                            modifier = Modifier.padding(8.dp, 4.dp),
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column {
                        Text(
                            text = "超棒",
                            fontSize = 14.sp,
                            color = Color(0xFF4A90E2),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${reviewSummary.reviewCount}条",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // 位置区域
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.clickable { onLocationClicked() }
                ) {
                    Text(
                        text = locationInfo.distanceInfo,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = locationInfo.address,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, false)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "地图",
                            tint = Color(0xFF4A90E2),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Text(
                            text = "地图",
                            fontSize = 12.sp,
                            color = Color(0xFF4A90E2)
                        )
                    }
                }
            }
            
            if (reviewSummary.summary.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${reviewSummary.summary}\"",
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun PromotionSection(
    promotions: List<PromotionItem>,
    onPromotionClicked: (PromotionItem) -> Unit
) {
    if (promotions.isEmpty()) return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "订房优惠",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(promotions) { promotion ->
                    Card(
                        modifier = Modifier.clickable { onPromotionClicked(promotion) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(android.graphics.Color.parseColor(promotion.backgroundColor))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp, 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = promotion.title,
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            
                            if (promotion.discount.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = promotion.discount,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateAndGuestSection(
    searchParams: HotelListSearchParams,
    onDateClicked: () -> Unit,
    onGuestCountClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 日期选择
            Column(
                modifier = Modifier.clickable { onDateClicked() }
            ) {
                Text(
                    text = "今天",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDateRange(searchParams.checkInDate, searchParams.checkOutDate),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "选择日期",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            // 人数选择
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.clickable { onGuestCountClicked() }
            ) {
                Text(
                    text = "间数人数",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${searchParams.roomCount}间 ${searchParams.guestCount}成人 0儿童",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "选择人数",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RoomFilterSection(
    filterTags: List<RoomFilterTag>,
    onTagToggled: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filterTags) { tag ->
            Card(
                modifier = Modifier.clickable { onTagToggled(tag.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (tag.isSelected) Color(0xFF4A90E2) else Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = tag.title,
                    modifier = Modifier.padding(12.dp, 6.dp),
                    fontSize = 12.sp,
                    color = if (tag.isSelected) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
private fun RoomTypeCard(
    roomType: RoomType,
    onRoomTypeClicked: () -> Unit,
    onContactHotelClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 销量徽章
            if (roomType.badge != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF8E1))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = roomType.badge,
                        fontSize = 12.sp,
                        color = Color(0xFFFF9800)
                    )
                }
            }
            
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                // 房间图片
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛏️",
                        fontSize = 32.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 房间详情
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 房间名称
                    Text(
                        text = roomType.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (roomType.description.isNotEmpty()) {
                        Text(
                            text = roomType.description,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 房间特征
                    Text(
                        text = roomType.features.joinToString(" "),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                    
                    Text(
                        text = roomType.breakfast,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                    
                    Text(
                        text = roomType.cancellationPolicy,
                        fontSize = 11.sp,
                        color = Color(0xFF4A90E2)
                    )
                }
            }
            
            // 底部操作区域
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
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "问酒店",
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "问酒店",
                        fontSize = 10.sp,
                        color = Color(0xFF4A90E2)
                    )
                }
                
                // 价格和查看房型按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        // 原价
                        roomType.originalPrice?.let { originalPrice ->
                            Text(
                                text = "¥$originalPrice",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        
                        // 现价
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "¥",
                                fontSize = 14.sp,
                                color = Color(0xFFFF4444)
                            )
                            Text(
                                text = roomType.price.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF4444)
                            )
                            Text(
                                text = "起",
                                fontSize = 12.sp,
                                color = Color(0xFFFF4444)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // 查看房型按钮
                    Button(
                        onClick = onRoomTypeClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            "查看房型",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private fun formatDateRange(checkIn: LocalDate, checkOut: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("M月d日")
    val nights = ChronoUnit.DAYS.between(checkIn, checkOut)
    return "${checkIn.format(formatter)} - ${checkOut.format(formatter)} 共${nights}晚"
}