package com.example.Ctrip.home

import android.widget.Toast
import android.graphics.Color as AndroidColor
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.Ctrip.utils.DateUtils
import com.example.Ctrip.model.City

@Composable
fun TrainTicketBookingTabScreen(onClose: () -> Unit = {}) {
    val context = LocalContext.current
    val model = remember { TrainTicketBookingTabModelImpl(context) }
    val presenter = remember { TrainTicketBookingTabPresenter(model) }

    var trainData by remember { mutableStateOf<TrainTicketBookingData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showDepartureSelect by remember { mutableStateOf(false) }
    var showDestinationSelect by remember { mutableStateOf(false) }
    var showDateSelect by remember { mutableStateOf(false) }
    var showTrainList by remember { mutableStateOf(false) }
    var departureCity by remember { mutableStateOf("上海") }
    var arrivalCity by remember { mutableStateOf("北京") }
    var departureDate by remember { mutableStateOf(LocalDate.of(2025, 10, 20)) }
    
    val view = object : TrainTicketBookingTabContract.View {
        override fun showTrainData(data: TrainTicketBookingData) {
            trainData = data
            // 同步更新UI状态变量
            departureCity = data.searchParams.departureCity
            arrivalCity = data.searchParams.arrivalCity
            departureDate = data.searchParams.departureDate
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
        
        override fun navigateToTrainSearch(searchParams: TrainSearchParams) {
            android.util.Log.d("TrainTicketBooking", "navigateToTrainSearch called: ${searchParams.departureCity} -> ${searchParams.arrivalCity} on ${searchParams.departureDate}")
            departureCity = searchParams.departureCity
            arrivalCity = searchParams.arrivalCity
            departureDate = searchParams.departureDate
            android.util.Log.d("TrainTicketBooking", "State variables updated: $departureCity -> $arrivalCity on $departureDate")
            showTrainList = true
        }
        
        override fun showDatePicker() {
            showDateSelect = true
        }
        
        override fun showCitySelector(isDeparture: Boolean) {
            if (isDeparture) {
                showDepartureSelect = true
            } else {
                showDestinationSelect = true
            }
        }
        
        override fun swapCities() {
            Toast.makeText(context, "交换出发地和目的地", Toast.LENGTH_SHORT).show()
        }
        
        override fun showNewCustomerGift() {
            Toast.makeText(context, "查看新客礼包", Toast.LENGTH_SHORT).show()
        }
        
        override fun showTravelTools() {
            Toast.makeText(context, "出行工具", Toast.LENGTH_SHORT).show()
        }
        
        override fun showTrainDeals(dealId: String) {
            Toast.makeText(context, "查看优惠: $dealId", Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(view)
        presenter.loadTrainData()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }

    if (showDepartureSelect) {
        // 显示出发地选择页面
        val departureView = remember { TrainDepartureSelectTabView(context) }

        LaunchedEffect(Unit) {
            departureView.initialize()
        }

        departureView.TrainDepartureSelectTabScreen(
            onCitySelected = { city ->
                android.util.Log.d("TrainTicketBooking", "Departure city selected: ${city.cityName}")
                departureCity = city.cityName
                // 更新trainData中的出发城市
                trainData?.let { data ->
                    val updatedParams = data.searchParams.copy(
                        departureCity = city.cityName
                    )
                    trainData = data.copy(searchParams = updatedParams)
                    // 同步更新 model 中的搜索参数
                    presenter.updateSearchParams(updatedParams)
                }
                showDepartureSelect = false
            },
            onClose = {
                showDepartureSelect = false
            }
        )
        return
    }

    if (showDestinationSelect) {
        // 显示目的地选择页面
        val destinationView = remember { TrainDestinationSelectTabView(context) }

        LaunchedEffect(Unit) {
            destinationView.initialize(departureCity)
        }

        destinationView.TrainDestinationSelectTabScreen(
            onCitySelected = { city ->
                android.util.Log.d("TrainTicketBooking", "Arrival city selected: ${city.cityName}")
                arrivalCity = city.cityName
                // 更新trainData中的到达城市
                trainData?.let { data ->
                    val updatedParams = data.searchParams.copy(
                        arrivalCity = city.cityName
                    )
                    trainData = data.copy(searchParams = updatedParams)
                    // 同步更新 model 中的搜索参数
                    presenter.updateSearchParams(updatedParams)
                }
                showDestinationSelect = false
            },
            onClose = {
                showDestinationSelect = false
            }
        )
        return
    }

    if (showDateSelect) {
        // 显示日期选择页面
        val dateView = remember { TrainDateSelectTabView(context) }

        LaunchedEffect(Unit) {
            dateView.initialize()
        }

        dateView.TrainDateSelectTabScreen(
            onDateSelected = { date ->
                android.util.Log.d("TrainTicketBooking", "Date selected: $date")
                departureDate = date
                // 更新trainData中的出发日期
                trainData?.let { data ->
                    val updatedParams = data.searchParams.copy(
                        departureDate = date
                    )
                    trainData = data.copy(searchParams = updatedParams)
                    // 同步更新 model 中的搜索参数
                    presenter.updateSearchParams(updatedParams)
                }
                showDateSelect = false
            },
            onClose = {
                showDateSelect = false
            }
        )
        return
    }

    if (showTrainList) {
        // 显示火车票列表页面
        // 基于查询参数来remember，确保参数变化时重新创建view
        val trainListView = remember(departureCity, arrivalCity, departureDate) {
            TrainMateListTabView(context)
        }

        val isStudentTicket = trainData?.searchParams?.isStudentTicket ?: false

        LaunchedEffect(departureCity, arrivalCity, departureDate, isStudentTicket) {
            trainListView.initialize(departureCity, arrivalCity, departureDate, isStudentTicket)
        }

        trainListView.TrainMateListTabScreen(
            onClose = {
                showTrainList = false
            }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 返回按钮栏
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable { onClose() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "返回",
                        color = Color(0xFF4A90E2),
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

        LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            TrainPromotionBannerSection(trainData?.promotionBanner)
        }
        
        item {
            TransportTabsSection(trainData?.transportTabs, presenter)
        }
        
        item {
            RegionTabsSection(trainData?.regionTabs, presenter)
        }
        
        item {
            TripTypeTabsSection(trainData?.tripTypeTabs, presenter)
        }
        
        item {
            TrainSearchSection(trainData?.searchParams, presenter)
        }
        
        item {
            SearchButtonSection(presenter)
        }
        
        item {
            SearchHistorySection()
        }
        
        item {
            NewCustomerGiftSection(trainData?.newCustomerGift, presenter)
        }
        
        item {
            TravelToolboxSection(trainData?.travelToolbox, presenter)
        }
        
        item {
            LowPriceDealsSection(trainData?.lowPriceDeals, presenter)
        }
        
        item {
            BottomNavigationSection(presenter)
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
private fun TrainPromotionBannerSection(banner: TrainPromotionBanner?) {
    banner?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(140.dp),
            colors = CardDefaults.cardColors(containerColor = Color(AndroidColor.parseColor(banner.backgroundColor))),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = banner.subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TransportTabsSection(tabs: List<TransportTabTrain>?, presenter: TrainTicketBookingTabPresenter) {
    tabs?.let { tabList ->
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
                tabList.forEach { tab ->
                    TransportTabItem(
                        tab = tab,
                        onClick = { presenter.onTransportTabSelected(tab.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransportTabItem(tab: TransportTabTrain, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tab.icon,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tab.title,
            fontSize = 14.sp,
            fontWeight = if (tab.isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (tab.isSelected) Color(0xFF4A90E2) else Color.Black
        )
        
        if (tab.isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(Color(0xFF4A90E2))
            )
        }
    }
}

@Composable
private fun RegionTabsSection(tabs: List<RegionTab>?, presenter: TrainTicketBookingTabPresenter) {
    tabs?.let { tabList ->
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
                tabList.forEach { tab ->
                    RegionTabItem(
                        tab = tab,
                        onClick = { presenter.onRegionTabSelected(tab.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionTabItem(tab: RegionTab, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tab.title,
            fontSize = 16.sp,
            fontWeight = if (tab.isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (tab.isSelected) Color.Black else Color(0xFF666666)
        )
        
        if (tab.isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .height(2.dp)
                    .background(Color(0xFF4A90E2))
            )
        }
    }
}

@Composable
private fun TripTypeTabsSection(tabs: List<TripTypeTabTrain>?, presenter: TrainTicketBookingTabPresenter) {
    tabs?.let { tabList ->
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
                horizontalArrangement = Arrangement.Start
            ) {
                tabList.forEach { tab ->
                    TripTypeTabItem(
                        tab = tab,
                        onClick = { presenter.onTripTypeSelected(tab.id) }
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TripTypeTabItem(tab: TripTypeTabTrain, onClick: () -> Unit) {
    Text(
        text = tab.title,
        fontSize = 16.sp,
        fontWeight = if (tab.isSelected) FontWeight.Medium else FontWeight.Normal,
        color = if (tab.isSelected) Color.Black else Color(0xFF666666),
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    )
}

@Composable
private fun TrainSearchSection(searchParams: TrainSearchParams?, presenter: TrainTicketBookingTabPresenter) {
    searchParams?.let { params ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Subscription offer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF8DC), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎯",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "订火车票，免费领酒店立减券！",
                        fontSize = 14.sp,
                        color = Color(0xFFFF6600)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "查看详情",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFF6600)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cities Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Departure City
                    Text(
                        text = params.departureCity,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { presenter.onDepartureCityClicked() }
                            .weight(1f)
                    )
                    
                    // Swap Button
                    IconButton(
                        onClick = { presenter.onSwapCitiesClicked() },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Text(
                            text = "🔄",
                            fontSize = 20.sp
                        )
                    }
                    
                    // Arrival City
                    Text(
                        text = params.arrivalCity,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { presenter.onArrivalCityClicked() }
                            .weight(1f),
                        textAlign = TextAlign.End
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { presenter.onDateClicked() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTrainDate(params.departureDate),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Multi-day commute toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "多日通勤",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = params.isMultiDayCommute,
                            onCheckedChange = { presenter.onMultiDayCommuteToggled(it) },
                            modifier = Modifier.scale(0.8f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Ticket options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = params.isStudentTicket,
                            onClick = { presenter.onStudentTicketToggled(!params.isStudentTicket) }
                        )
                        Text(
                            text = "学生票",
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { 
                                presenter.onStudentTicketToggled(!params.isStudentTicket) 
                            }
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = params.isHighSpeedTrain,
                            onClick = null,  // 禁用点击
                            enabled = false  // 设置为不可用
                        )
                        Text(
                            text = "高铁动车",
                            fontSize = 14.sp,
                            color = Color(0xFF999999)  // 灰色显示表示禁用
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchButtonSection(presenter: TrainTicketBookingTabPresenter) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { presenter.onSearchClicked() },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "查询",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E1)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "立减¥10",
                modifier = Modifier.padding(8.dp, 4.dp),
                fontSize = 12.sp,
                color = Color(0xFFFF6600),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SearchHistorySection() {
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
            Text(
                text = "上海 - 北京",
                fontSize = 14.sp,
                color = Color(0xFF4A90E2),
                modifier = Modifier.clickable { }
            )
            Text(
                text = "北京 - 上海",
                fontSize = 14.sp,
                color = Color(0xFF4A90E2),
                modifier = Modifier.clickable { }
            )
            Text(
                text = "清除历史",
                fontSize = 14.sp,
                color = Color(0xFF666666),
                modifier = Modifier.clickable { }
            )
        }
    }
}

@Composable
private fun NewCustomerGiftSection(gift: NewCustomerGift?, presenter: TrainTicketBookingTabPresenter) {
    gift?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(AndroidColor.parseColor(gift.backgroundColor))),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🎁",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = gift.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = gift.value,
                            fontSize = 14.sp,
                            color = Color(0xFFFF6600),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(gift.coupons) { coupon ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = coupon.type,
                                        fontSize = 10.sp,
                                        color = Color(0xFF666666)
                                    )
                                    Text(
                                        text = coupon.amount,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFFF6600)
                                    )
                                    if (coupon.description.isNotEmpty()) {
                                        Text(
                                            text = coupon.description,
                                            fontSize = 8.sp,
                                            color = Color(0xFF999999)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Button(
                    onClick = { presenter.onNewCustomerGiftClicked() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(16.dp, 8.dp)
                ) {
                    Text(
                        text = "去看看",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TravelToolboxSection(toolbox: TravelToolbox?, presenter: TrainTicketBookingTabPresenter) {
    toolbox?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = toolbox.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "全部工具 >",
                        fontSize = 14.sp,
                        color = Color(0xFF4A90E2),
                        modifier = Modifier.clickable { }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Main tools
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(toolbox.mainTools) { tool ->
                        ToolboxMainItem(
                            tool = tool,
                            onClick = { presenter.onTravelToolClicked(tool.id) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Additional tools
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(toolbox.additionalTools) { tool ->
                        ToolboxAdditionalItem(
                            tool = tool,
                            onClick = { presenter.onTravelToolClicked(tool.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolboxMainItem(tool: ToolboxItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(AndroidColor.parseColor(tool.backgroundColor))),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = tool.icon,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                if (tool.id == "ticket_prediction") {
                    Text(
                        text = "去看看",
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0xFF4CAF50), RoundedCornerShape(10.dp))
                            .padding(6.dp, 2.dp)
                    )
                }
            }
            
            Column {
                Text(
                    text = tool.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (tool.subtitle.isNotEmpty()) {
                    Text(
                        text = tool.subtitle,
                        fontSize = 10.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolboxAdditionalItem(tool: ToolboxItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tool.icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tool.title,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LowPriceDealsSection(deals: List<TrainDeal>?, presenter: TrainTicketBookingTabPresenter) {
    deals?.let { dealList ->
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dealList) { deal ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(80.dp)
                        .clickable { presenter.onTrainDealClicked(deal.id) },
                    colors = CardDefaults.cardColors(containerColor = Color(AndroidColor.parseColor(deal.backgroundColor))),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = deal.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationSection(presenter: TrainTicketBookingTabPresenter) {
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
            val bottomItems = listOf(
                BottomNavigationItem("grab_ticket", "抢票", "🎯", false),
                BottomNavigationItem("seat_change", "在线换座", "💺", false),
                BottomNavigationItem("money_center", "省钱中心", "💰", false),
                BottomNavigationItem("trip_orders", "行程/订单", "📋", false),
                BottomNavigationItem("personal_center", "个人中心", "👤", false)
            )
            
            bottomItems.forEach { item ->
                Column(
                    modifier = Modifier
                        .clickable { presenter.onBottomNavigationClicked(item.id) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = item.icon,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

private fun formatTrainDate(date: LocalDate): String {
    val today = DateUtils.getCurrentDate()
    val tomorrow = today.plusDays(1)
    
    return when (date) {
        today -> "${date.format(DateTimeFormatter.ofPattern("M月d日"))} 今天"
        tomorrow -> "${date.format(DateTimeFormatter.ofPattern("M月d日"))} 明天"
        else -> date.format(DateTimeFormatter.ofPattern("M月d日"))
    }
}