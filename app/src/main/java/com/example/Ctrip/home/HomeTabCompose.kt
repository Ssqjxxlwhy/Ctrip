package com.example.Ctrip.home

import android.graphics.Color
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun HomeTabScreen(
    showHotelBooking: Boolean = false,
    onHotelBookingChanged: (Boolean) -> Unit = {},
    showFlightBooking: Boolean = false,
    onFlightBookingChanged: (Boolean) -> Unit = {},
    showTrainBooking: Boolean = false,
    onTrainBookingChanged: (Boolean) -> Unit = {},
    onHotelListRequested: (HotelListSearchParams) -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { HomeTabModelImpl(context) }
    val presenter = remember { HomeTabPresenter(model) }
    
    var homeData by remember { mutableStateOf<HomeTabData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val view = object : HomeTabContract.View {
        override fun showHomeData(data: HomeTabData) {
            homeData = data
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
        
        override fun navigateToHotel() {
            onHotelBookingChanged(true)
        }
        
        override fun navigateToFlight() {
            onFlightBookingChanged(true)
        }
        
        override fun navigateToTrain() {
            onTrainBookingChanged(true)
        }
        
        override fun navigateToGuideAttraction() {
            Toast.makeText(context, "导航到攻略/景点页面", Toast.LENGTH_SHORT).show()
        }
        
        override fun navigateToTravelGroup() {
            Toast.makeText(context, "导航到旅游/跟团页面", Toast.LENGTH_SHORT).show()
        }
        
        override fun onQuickActionClick(actionId: String) {
            when (actionId) {
                "hotel" -> onHotelBookingChanged(true)
                "flight" -> onFlightBookingChanged(true)
                "train" -> onTrainBookingChanged(true)
                else -> Toast.makeText(context, "点击了: $actionId", Toast.LENGTH_SHORT).show()
            }
        }
        
        override fun onSearchClick(query: String) {
            Toast.makeText(context, "搜索: $query", Toast.LENGTH_SHORT).show()
        }
        
        override fun onPromotionClick(promotionId: String) {
            Toast.makeText(context, "点击促销: $promotionId", Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(view)
        presenter.loadHomeData()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }
    
    if (showHotelBooking) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back button - Fixed header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ComposeColor.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { 
                                println("返回按钮被点击了")  // 调试日志
                                onHotelBookingChanged(false) 
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "返回",
                            tint = ComposeColor(0xFF4A90E2),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "返回",
                            color = ComposeColor(0xFF4A90E2),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "酒店预订",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Hotel booking content - Modified to not fill entire size
            Box(modifier = Modifier.weight(1f)) {
                HotelBookingTabScreen(
                    onHotelListRequested = onHotelListRequested
                )
            }
        }
    } else if (showFlightBooking) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back button - Fixed header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ComposeColor.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { 
                                println("返回按钮被点击了")  // 调试日志
                                onFlightBookingChanged(false) 
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "返回",
                            tint = ComposeColor(0xFF4A90E2),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "返回",
                            color = ComposeColor(0xFF4A90E2),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "机票预订",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Flight booking content
            Box(modifier = Modifier.weight(1f)) {
                FlightBookingTabScreen()
            }
        }
    } else if (showTrainBooking) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back button - Fixed header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = ComposeColor.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable { 
                                println("返回按钮被点击了")  // 调试日志
                                onTrainBookingChanged(false) 
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "返回",
                            tint = ComposeColor(0xFF4A90E2),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "返回",
                            color = ComposeColor(0xFF4A90E2),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "火车票预订",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Train booking content
            Box(modifier = Modifier.weight(1f)) {
                TrainTicketBookingTabScreen()
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeColor(0xFFF5F5F5)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                HeaderSection(homeData)
            }
            
            item {
                MainActionsSection(homeData, presenter)
            }
            
            item {
                SecondaryActionsSection(homeData, presenter)
            }
            
            item {
                MoreProductsSection()
            }
            
            item {
                SearchSection(presenter)
            }
            
            item {
                QuickSearchSection(homeData, presenter)
            }
            
            item {
                ContentSectionsRow(homeData)
            }
            
            item {
                PromotionSection(homeData, presenter)
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

@Composable
private fun HeaderSection(homeData: HomeTabData?) {
    homeData?.let { data ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF4A90E2))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Logo",
                        tint = ComposeColor.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "携程旅行",
                        color = ComposeColor.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row {
                    Card(
                        modifier = Modifier.clickable { },
                        colors = CardDefaults.cardColors(containerColor = ComposeColor.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = data.userInfo.memberLevel,
                            modifier = Modifier.padding(8.dp, 4.dp),
                            color = ComposeColor.White,
                            fontSize = 12.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Card(
                        modifier = Modifier.clickable { },
                        colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFFFFB74D)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp, 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💰",
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${data.userInfo.points}积分",
                                color = ComposeColor.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainActionsSection(homeData: HomeTabData?, presenter: HomeTabPresenter) {
    homeData?.let { data ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ComposeColor.White)
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(data.mainActions) { action ->
                    QuickActionItem(action) {
                        presenter.onQuickActionClicked(action.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun SecondaryActionsSection(homeData: HomeTabData?, presenter: HomeTabPresenter) {
    homeData?.let { data ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ComposeColor.White)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .padding(16.dp)
                    .height(120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(data.secondaryActions) { action ->
                    SecondaryActionItem(action) {
                        presenter.onQuickActionClicked(action.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(action: QuickAction, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(56.dp),
            colors = CardDefaults.cardColors(
                containerColor = ComposeColor(Color.parseColor(action.color))
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon,
                    fontSize = 24.sp,
                    color = ComposeColor.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = action.title,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SecondaryActionItem(action: QuickAction, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = action.icon,
            fontSize = 20.sp
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = action.title,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MoreProductsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = ComposeColor.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🚌", fontSize = 16.sp)
            Text(text = "🏖️", fontSize = 16.sp)
            Text(text = "⛰️", fontSize = 16.sp)
            Text(text = "🎭", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "+12个更多产品",
                fontSize = 14.sp,
                color = ComposeColor(0xFF666666)
            )
        }
    }
}

@Composable
private fun SearchSection(presenter: HomeTabPresenter) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = ComposeColor.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ComposeColor(0xFFF0F8FF))
                    .clickable { presenter.onSearchSubmitted("北京本地游·景点·酒店") }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ComposeColor(0xFF4A90E2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "北京本地游·景点·酒店",
                    color = ComposeColor(0xFF4A90E2),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = ComposeColor(0xFF666666)
                )
            }
        }
    }
}

@Composable
private fun QuickSearchSection(homeData: HomeTabData?, presenter: HomeTabPresenter) {
    homeData?.let { data ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ComposeColor.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "搜一搜：",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.searchSuggestions) { suggestion ->
                        Card(
                            modifier = Modifier.clickable { 
                                presenter.onSearchSubmitted(suggestion.text) 
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = ComposeColor(0xFFF0F0F0)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = suggestion.text,
                                modifier = Modifier.padding(12.dp, 6.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentSectionsRow(homeData: HomeTabData?) {
    homeData?.let { data ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = ComposeColor.White)
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(data.contentSections) { section ->
                    Column(
                        modifier = Modifier.clickable { },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = section.icon,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = section.title,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromotionSection(homeData: HomeTabData?, presenter: HomeTabPresenter) {
    homeData?.let { data ->
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(data.promotionBanners) { banner ->
                Card(
                    modifier = Modifier
                        .width(200.dp)
                        .height(120.dp)
                        .clickable { presenter.onPromotionClicked(banner.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = ComposeColor(Color.parseColor(banner.backgroundColor))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = banner.title,
                                color = ComposeColor.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = banner.subtitle,
                                color = ComposeColor.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                        
                        Text(
                            text = banner.price,
                            color = ComposeColor.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}