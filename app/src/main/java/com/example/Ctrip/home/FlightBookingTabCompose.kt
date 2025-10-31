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
fun FlightBookingTabScreen(
    onNavigationStateChanged: (isInSubPage: Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { FlightBookingTabModelImpl(context) }
    val presenter = remember { FlightBookingTabPresenter(model) }

    var flightData by remember { mutableStateOf<FlightBookingData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showDepartureSelect by remember { mutableStateOf(false) }
    var showDestinationSelect by remember { mutableStateOf(false) }
    var showFlightDateSelect by remember { mutableStateOf(false) }
    var showFlightList by remember { mutableStateOf(false) }
    var showCabinTypeSelect by remember { mutableStateOf(false) }
    var showOrderForm by remember { mutableStateOf(false) }
    var showServiceSelect by remember { mutableStateOf(false) }
    var showPaymentSuccess by remember { mutableStateOf(false) }
    var selectedFlightId by remember { mutableStateOf<String?>(null) }
    var selectedTicketOptionId by remember { mutableStateOf<String?>(null) }
    var currentOrderFormData by remember { mutableStateOf<OrderFormData?>(null) }
    var currentServiceData by remember { mutableStateOf<ServiceSelectData?>(null) }
    var selectedDepartureCity by remember { mutableStateOf<com.example.Ctrip.model.City?>(null) }
    var selectedDestinationCity by remember { mutableStateOf<com.example.Ctrip.model.City?>(null) }
    var selectedFlightDate by remember { mutableStateOf<LocalDate?>(null) }
    
    val view = object : FlightBookingTabContract.View {
        override fun showFlightData(data: FlightBookingData) {
            flightData = data
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
        
        override fun navigateToFlightSearch(searchParams: FlightSearchParams) {
            // Navigate to flight list with search parameters
            showFlightList = true
        }
        
        override fun showDatePicker(isReturn: Boolean) {
            if (!isReturn) {
                showFlightDateSelect = true
            } else {
                Toast.makeText(context, "选择返程日期", Toast.LENGTH_SHORT).show()
            }
        }
        
        override fun showPassengerPicker(currentInfo: PassengerInfo) {
            Toast.makeText(context, "选择乘客信息", Toast.LENGTH_SHORT).show()
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
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(view)
        presenter.loadFlightData()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }

    // 通知父组件当前是否在子页面
    LaunchedEffect(showFlightList, showCabinTypeSelect, showOrderForm, showServiceSelect, showPaymentSuccess) {
        val isInSubPage = showFlightList || showCabinTypeSelect || showOrderForm || showServiceSelect || showPaymentSuccess
        onNavigationStateChanged(isInSubPage)
    }

    if (showFlightList) {
        // Flight List Screen - 完全替换机票预订页面
        val flightListView = remember { FlightListTabView(context) }
        LaunchedEffect(Unit) {
            val departureCity = selectedDepartureCity?.cityName ?: "上海"
            val arrivalCity = selectedDestinationCity?.cityName ?: "北京"
            val searchDate = selectedFlightDate ?: LocalDate.now().plusDays(1)
            // 获取选中的舱位类型
            val selectedCabin = flightData?.cabinTypes?.find { it.isSelected }?.id ?: "economy"
            flightListView.initialize(departureCity, arrivalCity, searchDate, selectedCabin)
        }
        flightListView.FlightListTabScreen(
            onFlightSelected = { flightId ->
                selectedFlightId = flightId
                showCabinTypeSelect = true
                showFlightList = false
            },
            onBack = {
                showFlightList = false
            }
        )
    } else {
        // Flight Booking Screen - 机票预订页面
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    PromotionBannerSection(flightData?.promotionBanner)
                }
                
                item {
                    TransportTabsSection(flightData?.transportTabs, presenter)
                }
                
                item {
                    TripTypeTabsSection(flightData?.tripTypeTabs, presenter)
                }
                
                item {
                    FlightSearchSection(flightData?.searchParams, flightData?.cabinTypes, selectedDepartureCity, selectedDestinationCity, selectedFlightDate, presenter)
                }
                
                item {
                    SearchButtonSection(presenter)
                }
                
                item {
                    ServiceProviderSection()
                }
                
                item {
                    ServiceFeaturesSection(flightData?.serviceFeatures, presenter)
                }
                
                item {
                    MembershipSectionsRow(flightData?.membershipSections, presenter)
                }
                
                item {
                    InspirationSection(flightData?.inspirationSections, presenter)
                }
                
                item {
                    BottomFeaturesSection(flightData?.bottomFeatures, presenter)
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
            
            // Departure City Selection Screen
            if (showDepartureSelect) {
                val departureView = remember { FlightDepartureSelectTabView(context) }
                LaunchedEffect(Unit) {
                    departureView.initialize()
                }
                departureView.FlightDepartureSelectTabScreen(
                    onCitySelected = { city ->
                        selectedDepartureCity = city
                        // 通知presenter更新出发地城市
                        presenter.updateDepartureCity(city.cityName)
                        Toast.makeText(context, "选择出发地: ${city.cityName}", Toast.LENGTH_SHORT).show()
                        showDepartureSelect = false
                    },
                    onClose = {
                        showDepartureSelect = false
                    }
                )
            }
            
            // Destination City Selection Screen
            if (showDestinationSelect) {
                val destinationView = remember { FlightDestinationSelectTabView(context) }
                LaunchedEffect(Unit) {
                    destinationView.initialize()
                }
                destinationView.FlightDestinationSelectTabScreen(
                    onDestinationSelected = { city ->
                        selectedDestinationCity = city
                        // 通知presenter更新目的地城市
                        presenter.updateDestinationCity(city.cityName)
                        Toast.makeText(context, "选择目的地: ${city.cityName}", Toast.LENGTH_SHORT).show()
                        showDestinationSelect = false
                    },
                    onClose = {
                        showDestinationSelect = false
                    }
                )
            }
            
            // Flight Date Selection Screen
            if (showFlightDateSelect) {
                val flightDateView = remember { FlightDateSelectTabView(context) }
                LaunchedEffect(Unit) {
                    flightDateView.initialize()
                }
                flightDateView.FlightDateSelectTabScreen(
                    onDateSelected = { date ->
                        selectedFlightDate = date
                        Toast.makeText(context, "选择出发日期: ${date.format(DateTimeFormatter.ofPattern("M月d日"))}", Toast.LENGTH_SHORT).show()
                        showFlightDateSelect = false
                    },
                    onClose = {
                        showFlightDateSelect = false
                    }
                )
            }
        }
    }
    
    if (showCabinTypeSelect && selectedFlightId != null) {
        // Cabin Type Selection Screen
        val cabinTypeSelectView = remember { CabinTypeSelectTabView(context) }
        LaunchedEffect(selectedFlightId) {
            cabinTypeSelectView.initialize(selectedFlightId!!)
        }
        cabinTypeSelectView.CabinTypeSelectTabScreen(
            onNavigateToOrderForm = { ticketOptionId ->
                selectedTicketOptionId = ticketOptionId
                showCabinTypeSelect = false
                showOrderForm = true
            },
            onBack = {
                showCabinTypeSelect = false
                showFlightList = true
            }
        )
    }
    
    if (showOrderForm && selectedTicketOptionId != null) {
        // Order Form Screen
        val orderFormView = remember { OrderFormTabView(context) }
        LaunchedEffect(selectedTicketOptionId) {
            orderFormView.initialize(selectedTicketOptionId!!)
        }
        orderFormView.OrderFormTabScreen(
            onNavigateToServiceSelect = { orderData ->
                currentOrderFormData = orderData
                showOrderForm = false
                showServiceSelect = true
            },
            onBack = {
                showOrderForm = false
                showCabinTypeSelect = true
            }
        )
    }

    if (showServiceSelect && currentOrderFormData != null) {
        // Service Select Screen
        val serviceSelectView = remember { ServiceSelectTabView(context) }
        LaunchedEffect(currentOrderFormData) {
            serviceSelectView.initialize(currentOrderFormData!!)
        }
        serviceSelectView.ServiceSelectTabScreen(
            onNavigateToPayment = { serviceData ->
                currentServiceData = serviceData
                showServiceSelect = false
                showPaymentSuccess = true
            },
            onBack = {
                showServiceSelect = false
                showOrderForm = true
            }
        )
    }

    if (showPaymentSuccess && currentServiceData != null) {
        // Flight Payment Success Screen
        val paymentSuccessView = remember { FlightPaymentSuccessTabView(context) }
        LaunchedEffect(currentServiceData) {
            paymentSuccessView.initialize(currentServiceData!!)
        }
        paymentSuccessView.FlightPaymentSuccessTabScreen(
            onNavigateToOrderList = {
                Toast.makeText(context, "导航到订单列表页面", Toast.LENGTH_SHORT).show()
            },
            onNavigateToHome = {
                // 返回首页，重置所有状态
                showPaymentSuccess = false
                showServiceSelect = false
                showOrderForm = false
                showCabinTypeSelect = false
                showFlightList = false
            },
            onBack = {
                // 返回到服务选择页面
                showPaymentSuccess = false
                showServiceSelect = true
            }
        )
    }
}

@Composable
private fun PromotionBannerSection(banner: FlightPromotionBanner?) {
    banner?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(120.dp),
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
                    Spacer(modifier = Modifier.height(4.dp))
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
private fun TransportTabsSection(tabs: List<TransportTab>?, presenter: FlightBookingTabPresenter) {
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
private fun TransportTabItem(tab: TransportTab, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = tab.title,
                fontSize = 16.sp,
                fontWeight = if (tab.isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (tab.isSelected) Color(0xFF4A90E2) else Color.Black
            )
            
            if (tab.hasDiscount) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "特价",
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            Color(0xFFFF6600),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
        
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
private fun TripTypeTabsSection(tabs: List<TripTypeTab>?, presenter: FlightBookingTabPresenter) {
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
private fun TripTypeTabItem(tab: TripTypeTab, onClick: () -> Unit) {
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
private fun FlightSearchSection(
    searchParams: FlightSearchParams?, 
    cabinTypes: List<CabinType>?, 
    selectedDepartureCity: com.example.Ctrip.model.City?,
    selectedDestinationCity: com.example.Ctrip.model.City?,
    selectedFlightDate: LocalDate?,
    presenter: FlightBookingTabPresenter
) {
    searchParams?.let { params ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Cities Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Departure City
                    Text(
                        text = selectedDepartureCity?.cityName ?: params.departureCity,
                        fontSize = 28.sp,
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
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "交换城市",
                            tint = Color(0xFF4A90E2)
                        )
                    }
                    
                    // Arrival City
                    Text(
                        text = selectedDestinationCity?.cityName ?: params.arrivalCity,
                        fontSize = 28.sp,
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
                        text = selectedFlightDate?.let { formatFlightDate(it) } ?: formatFlightDate(params.departureDate),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "选择日期",
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Cabin and Passenger Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Cabin Selection
                    cabinTypes?.let { cabins ->
                        Row {
                            cabins.forEach { cabin ->
                                Text(
                                    text = cabin.title,
                                    fontSize = 16.sp,
                                    fontWeight = if (cabin.isSelected) FontWeight.Medium else FontWeight.Normal,
                                    color = if (cabin.isSelected) Color.Black else Color(0xFF666666),
                                    modifier = Modifier
                                        .clickable { presenter.onCabinTypeSelected(cabin.id) }
                                        .padding(end = 16.dp)
                                )
                            }
                        }
                    }
                    
                    // Passenger Info
                    Row(
                        modifier = Modifier.clickable { presenter.onPassengerInfoClicked() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatPassengerInfo(params.passengerInfo),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = "选择乘客",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchButtonSection(presenter: FlightBookingTabPresenter) {
    Button(
        onClick = { presenter.onSearchClicked() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
}

@Composable
private fun ServiceProviderSection() {
    Text(
        text = "机票预订服务由上海华程西南国际旅行社有限公司提供",
        fontSize = 12.sp,
        color = Color(0xFF999999),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ServiceFeaturesSection(features: List<ServiceFeature>?, presenter: FlightBookingTabPresenter) {
    features?.let { featureList ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            LazyRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(featureList) { feature ->
                    ServiceFeatureItem(
                        feature = feature,
                        onClick = { presenter.onServiceFeatureClicked(feature.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceFeatureItem(feature: ServiceFeature, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = feature.icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = feature.title,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MembershipSectionsRow(sections: List<MembershipSection>?, presenter: FlightBookingTabPresenter) {
    sections?.let { sectionList ->
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sectionList) { section ->
                MembershipCard(
                    section = section,
                    onClick = { presenter.onMembershipSectionClicked(section.id) }
                )
            }
        }
    }
}

@Composable
private fun MembershipCard(section: MembershipSection, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(AndroidColor.parseColor(section.backgroundColor))),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = section.title,
                color = Color(AndroidColor.parseColor(section.textColor)),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = section.subtitle,
                color = Color(AndroidColor.parseColor(section.textColor)).copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun InspirationSection(inspirations: List<InspirationSection>?, presenter: FlightBookingTabPresenter) {
    inspirations?.let { inspirationList ->
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "上海出发的灵感",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📍",
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(inspirationList) { inspiration ->
                    InspirationCard(
                        inspiration = inspiration,
                        onClick = { presenter.onInspirationSectionClicked(inspiration.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InspirationCard(inspiration: InspirationSection, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = inspiration.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = inspiration.subtitle,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun BottomFeaturesSection(features: List<BottomFeature>?, presenter: FlightBookingTabPresenter) {
    features?.let { featureList ->
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
                featureList.forEach { feature ->
                    BottomFeatureItem(
                        feature = feature,
                        onClick = { presenter.onBottomFeatureClicked(feature.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomFeatureItem(feature: BottomFeature, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = feature.icon,
            fontSize = 16.sp
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

private fun formatFlightDate(date: LocalDate): String {
    val today = DateUtils.getCurrentDate()
    val tomorrow = today.plusDays(1)
    
    return when (date) {
        today -> "${date.format(DateTimeFormatter.ofPattern("M月d日"))} 今天"
        tomorrow -> "${date.format(DateTimeFormatter.ofPattern("M月d日"))} 明天"
        else -> date.format(DateTimeFormatter.ofPattern("M月d日"))
    }
}

private fun formatPassengerInfo(info: PassengerInfo): String {
    return "${info.adultCount}成人 ${info.childCount}儿童 ${info.infantCount}婴儿"
}