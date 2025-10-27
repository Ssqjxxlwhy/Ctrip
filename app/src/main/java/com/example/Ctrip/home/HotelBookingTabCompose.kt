package com.example.Ctrip.home

import android.widget.Toast
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.Ctrip.utils.DateUtils

@Composable
fun HotelBookingTabScreen(
    onHotelListRequested: (HotelListSearchParams) -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { HotelBookingTabModelImpl(context) }
    val presenter = remember { HotelBookingTabPresenter(model) }
    
    var hotelData by remember { mutableStateOf<HotelBookingData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showCitySelect by remember { mutableStateOf(false) }
    var showDateSelect by remember { mutableStateOf(false) }
    var showRoomGuestSelect by remember { mutableStateOf(false) }
    
    val view = object : HotelBookingTabContract.View {
        override fun showHotelData(data: HotelBookingData) {
            hotelData = data
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
        
        override fun navigateToHotelSearch(searchParams: HotelSearchParams) {
            // Convert to HotelListSearchParams and call the parent callback
            val listSearchParams = HotelListSearchParams(
                city = searchParams.city,
                checkInDate = searchParams.checkInDate,
                checkOutDate = searchParams.checkOutDate,
                roomCount = searchParams.roomCount,
                guestCount = searchParams.adultCount + searchParams.childCount
            )
            onHotelListRequested(listSearchParams)
        }
        
        override fun showDatePicker(currentDate: LocalDate) {
            showDateSelect = true
        }
        
        override fun showRoomGuestPicker(currentParams: HotelSearchParams) {
            showRoomGuestSelect = true
        }
        
        override fun showCitySelector(currentCity: String) {
            showCitySelect = true
        }
        
        override fun showImageSearch() {
            Toast.makeText(context, "图搜酒店", Toast.LENGTH_SHORT).show()
        }
        
        override fun showVoiceSearch() {
            Toast.makeText(context, "AI语音搜索", Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(view)
        presenter.loadHotelData()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }
    
    if (showCitySelect) {
        CitySelectTabScreen(
            onCitySelected = { city ->
                // Update hotel data with selected city
                hotelData?.let { data ->
                    val updatedParams = data.searchParams.copy(city = city.name)
                    presenter.updateSearchParams(updatedParams)
                    Toast.makeText(context, "已选择城市: ${city.name}", Toast.LENGTH_SHORT).show()
                }
                showCitySelect = false
            },
            onBackPressed = {
                showCitySelect = false
            }
        )
    } else if (showDateSelect) {
        HotelDateSelectTabScreen(
            onDateSelected = { dateSelection ->
                // Update hotel data with selected dates
                hotelData?.let { data ->
                    val updatedParams = data.searchParams.copy(
                        checkInDate = dateSelection.checkInDate,
                        checkOutDate = dateSelection.checkOutDate
                    )
                    presenter.updateSearchParams(updatedParams)
                    Toast.makeText(context, "已选择日期: ${dateSelection.checkInDate.format(DateTimeFormatter.ofPattern("M月d日"))} - ${dateSelection.checkOutDate.format(DateTimeFormatter.ofPattern("M月d日"))}", Toast.LENGTH_SHORT).show()
                }
                showDateSelect = false
            },
            onBackPressed = {
                showDateSelect = false
            }
        )
    } else {
        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            PromotionBannerSection(hotelData?.promotionBanner)
        }
        
        item {
            CategoryTabsSection(hotelData?.hotelCategories, presenter)
        }
        
        item {
            SearchParametersSection(hotelData?.searchParams, presenter)
        }
        
        item {
            SearchButtonSection(presenter)
        }
        
        item {
            SearchToolsSection(presenter)
        }
        
        item {
            BenefitSectionsRow(hotelData?.benefitSections, presenter)
        }
        
        item {
            QuickSearchTagsSection(hotelData?.quickSearchTags, presenter)
        }
        
        item {
            BottomSectionsRow(hotelData?.bottomSections, presenter)
        }
        
        item {
            BottomNavigationSection()
        }
    }
    }
    
    // Show City Select Screen
    if (showCitySelect) {
        CitySelectTabScreen(
            onCitySelected = { city ->
                hotelData?.let { data ->
                    val updatedParams = data.searchParams.copy(city = city.name)
                    presenter.updateSearchParams(updatedParams)
                }
                showCitySelect = false
            },
            onBackPressed = { showCitySelect = false }
        )
    }
    
    // Show Date Select Screen
    if (showDateSelect) {
        HotelDateSelectTabScreen(
            onDateSelected = { dateSelection ->
                hotelData?.let { data ->
                    val updatedParams = data.searchParams.copy(
                        checkInDate = dateSelection.checkInDate,
                        checkOutDate = dateSelection.checkOutDate
                    )
                    presenter.updateSearchParams(updatedParams)
                }
                showDateSelect = false
            },
            onBackPressed = { showDateSelect = false }
        )
    }
    
    // Show Room and Guest Select Screen
    if (showRoomGuestSelect) {
        RoomAndGuestSelectTabScreen(
            onRoomAndGuestSelected = { roomGuestSelection ->
                hotelData?.let { data ->
                    val updatedParams = data.searchParams.copy(
                        roomCount = roomGuestSelection.roomCount,
                        adultCount = roomGuestSelection.adultCount,
                        childCount = roomGuestSelection.childCount
                    )
                    presenter.updateSearchParams(updatedParams)
                }
                showRoomGuestSelect = false
            },
            onBackPressed = { showRoomGuestSelect = false }
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

@Composable
private fun PromotionBannerSection(banner: HotelPromotionBanner?) {
    banner?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(120.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90E2)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = banner.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = banner.subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTabsSection(categories: List<HotelCategory>?, presenter: HotelBookingTabPresenter) {
    categories?.let { cats ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(cats) { category ->
                    CategoryTab(
                        category = category,
                        onClick = { presenter.onCategorySelected(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTab(category: HotelCategory, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = category.title,
            fontSize = 16.sp,
            fontWeight = if (category.isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (category.isSelected) Color(0xFF4A90E2) else Color.Black
        )
        
        if (category.isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(Color(0xFF4A90E2))
            )
        }
    }
}

@Composable
private fun SearchParametersSection(searchParams: HotelSearchParams?, presenter: HotelBookingTabPresenter) {
    searchParams?.let { params ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // City Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { presenter.onCityClicked() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = params.city,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "选择城市",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "定位",
                        tint = Color(0xFF4A90E2)
                    )
                }
                
                Divider(color = Color(0xFFE0E0E0))
                
                // Search Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "位置/品牌/酒店",
                        color = Color(0xFF999999),
                        fontSize = 16.sp
                    )
                }
                
                Divider(color = Color(0xFFE0E0E0))
                
                // Date Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { presenter.onDateClicked() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatDateRange(params.checkInDate, params.checkOutDate),
                        fontSize = 16.sp
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "选择日期",
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Divider(color = Color(0xFFE0E0E0))
                
                // Room and Guest Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { presenter.onRoomGuestClicked() }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${params.roomCount}间房 ${params.adultCount}成人 ${params.childCount}儿童",
                        fontSize = 16.sp
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "选择房间和入住人数",
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Divider(color = Color(0xFFE0E0E0))
                
                // Price and Star Level
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "价格/星级",
                        color = Color(0xFF999999),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchButtonSection(presenter: HotelBookingTabPresenter) {
    Button(
        onClick = { presenter.onSearchClicked() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
        shape = RoundedCornerShape(25.dp)
    ) {
        Text(
            text = "查询",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SearchToolsSection(presenter: HotelBookingTabPresenter) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { presenter.onImageSearchClicked() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "📷",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "图搜酒店",
                    color = Color(0xFF4A90E2),
                    fontSize = 14.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { presenter.onVoiceSearchClicked() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎤",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI语音搜索",
                    color = Color(0xFF4A90E2),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun BenefitSectionsRow(benefitSections: List<BenefitSection>?, presenter: HotelBookingTabPresenter) {
    benefitSections?.let { sections ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "首住好礼",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sections) { section ->
                        BenefitCard(
                            section = section,
                            onClick = { presenter.onBenefitSectionClicked(section) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitCard(section: BenefitSection, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(AndroidColor.parseColor(section.backgroundColor))),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = section.discountText,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = section.benefitText,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun QuickSearchTagsSection(quickTags: List<String>?, presenter: HotelBookingTabPresenter) {
    quickTags?.let { tags ->
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tags) { tag ->
                Card(
                    modifier = Modifier.clickable { presenter.onQuickTagClicked(tag) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = tag,
                        modifier = Modifier.padding(12.dp, 6.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomSectionsRow(bottomSections: List<BottomSection>?, presenter: HotelBookingTabPresenter) {
    bottomSections?.let { sections ->
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sections) { section ->
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .clickable { presenter.onBottomSectionClicked(section.id) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = section.icon,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = section.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = section.subtitle,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem("推荐", "🔥")
            BottomNavItem("购物车/收藏", "🛒")
            BottomNavItem("我的权益", "💎")
            BottomNavItem("我的点评", "📝")
            BottomNavItem("我的订单", "📋")
        }
    }
}

@Composable
private fun BottomNavItem(title: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

private fun formatDateRange(checkIn: LocalDate, checkOut: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("M月d日")
    val today = DateUtils.getCurrentDate()
    val tomorrow = today.plusDays(1)
    
    val checkInText = when (checkIn) {
        today -> "${checkIn.format(formatter)} 今天"
        tomorrow -> "${checkIn.format(formatter)} 明天"
        else -> checkIn.format(formatter)
    }
    
    val checkOutText = when (checkOut) {
        today -> "${checkOut.format(formatter)} 今天"
        tomorrow -> "${checkOut.format(formatter)} 明天"
        else -> checkOut.format(formatter)
    }
    
    val nights = checkOut.toEpochDay() - checkIn.toEpochDay()
    
    return "$checkInText - $checkOutText 共${nights}晚"
}