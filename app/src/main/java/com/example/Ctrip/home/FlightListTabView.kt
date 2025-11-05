package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import com.example.Ctrip.utils.SearchParamsManager
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

class FlightListTabView(private val context: Context) : FlightListTabContract.View {
    
    private lateinit var presenter: FlightListTabContract.Presenter
    private var flightListData by mutableStateOf<FlightListData?>(null)
    private var isLoading by mutableStateOf(false)
    private var departureCity by mutableStateOf("")
    private var arrivalCity by mutableStateOf("")
    private var selectedDate by mutableStateOf<LocalDate?>(null)
    private var cabinClass by mutableStateOf("")

    fun initialize(departureCity: String, arrivalCity: String, selectedDate: LocalDate, cabinClass: String = "economy") {
        this.departureCity = departureCity
        this.arrivalCity = arrivalCity
        this.selectedDate = selectedDate
        this.cabinClass = cabinClass
        val model = FlightListTabModel(context)
        presenter = FlightListTabPresenter(model)
        presenter.attachView(this)
        presenter.loadFlightListData(departureCity, arrivalCity, selectedDate, cabinClass)
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FlightListTabScreen(
        onFlightSelected: (String) -> Unit = {},
        onBack: () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 顶部标题栏
                TopBarSection(
                    departureCity = departureCity,
                    arrivalCity = arrivalCity,
                    onBack = onBack
                )
                
                // 主要内容区域
                Box(modifier = Modifier.weight(1f)) {
                    flightListData?.let { data ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp), // Leave space for bottom bar
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            // 日期选择栏
                            item {
                                DateOptionsSection(
                                    dateOptions = data.dateOptions,
                                    onDateClicked = { dateId ->
                                        presenter.onDateOptionClicked(dateId)
                                    }
                                )
                            }
                            
                            // 机票次卡推广栏
                            item {
                                PromotionBannerSection(
                                    banner = data.promotionBanner,
                                    onClick = { presenter.onPromotionBannerClicked() }
                                )
                            }
                            
                            // 普通会员权益栏
                            item {
                                MembershipInfoSection(
                                    membershipInfo = data.membershipInfo,
                                    onClick = { presenter.onMembershipInfoClicked() }
                                )
                            }
                            
                            // 筛选标签栏
                            item {
                                FilterTagsSection(
                                    filterTags = data.filterTags,
                                    onFilterClicked = { filterId ->
                                        presenter.onFilterTagClicked(filterId)
                                    }
                                )
                            }
                            
                            // 航班列表
                            items(data.flightList) { flight ->
                                FlightItemCard(
                                    flight = flight,
                                    onClick = {
                                        presenter.onFlightItemClicked(flight.id)
                                        onFlightSelected(flight.id)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // 底部排序栏
                flightListData?.let { data ->
                    BottomSortSection(
                        sortOptions = data.sortOptions,
                        onSortClicked = { sortId ->
                            presenter.onSortOptionClicked(sortId)
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
    private fun TopBarSection(
        departureCity: String,
        arrivalCity: String,
        onBack: () -> Unit
    ) {
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
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // 标题
                Text(
                    text = "$departureCity — $arrivalCity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    modifier = Modifier.weight(1f)
                )
                
                // 右侧更多按钮和低价提醒
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "低价提醒",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF666666)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DateOptionsSection(
        dateOptions: List<DateOption>,
        onDateClicked: (String) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dateOptions) { dateOption ->
                    DateOptionItem(
                        dateOption = dateOption,
                        onClick = { onDateClicked(dateOption.id) }
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DateOptionItem(
        dateOption: DateOption,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .clickable { onClick() }
                .width(70.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (dateOption.isSelected) Color(0xFF007AFF) else Color(0xFFF0F0F0)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dateOption.label,
                    fontSize = 12.sp,
                    color = if (dateOption.isSelected) Color.White else Color(0xFF333333)
                )
                Text(
                    text = dateOption.displayDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (dateOption.isSelected) Color.White else Color(0xFF333333)
                )
                Text(
                    text = dateOption.price,
                    fontSize = 12.sp,
                    color = if (dateOption.isSelected) Color.White else Color(0xFF007AFF)
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PromotionBannerSection(
        banner: FlightListPromotionBanner,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = banner.icon,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = banner.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = banner.subtitle,
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
                
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = banner.actionText,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
    
    @Composable
    private fun MembershipInfoSection(
        membershipInfo: MembershipInfo,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "💎",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = membershipInfo.level,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = membershipInfo.upgradeText,
                                fontSize = 14.sp,
                                color = Color(0xFF007AFF)
                            )
                        }
                        Text(
                            text = membershipInfo.benefits,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
                
                if (membershipInfo.showArrow) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "查看详情",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF999999)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FilterTagsSection(
        filterTags: List<FlightFilterTag>,
        onFilterClicked: (String) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterTags) { filterTag ->
                    FilterTagItem(
                        filterTag = filterTag,
                        onClick = { onFilterClicked(filterTag.id) }
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FilterTagItem(
        filterTag: FlightFilterTag,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier.clickable { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (filterTag.isSelected) Color(0xFF007AFF) else Color(0xFFF0F0F0)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = filterTag.text,
                    fontSize = 12.sp,
                    color = if (filterTag.isSelected) Color.White else Color(0xFF333333)
                )
                if (filterTag.hasDropdown) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "展开",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(14.dp),
                        tint = if (filterTag.isSelected) Color.White else Color(0xFF666666)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FlightItemCard(
        flight: FlightItem,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 左侧时间和机场信息
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = flight.departureTime,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "━━━━━━",
                                color = Color(0xFFCCCCCC),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = flight.arrivalTime,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row {
                            Text(
                                text = flight.departureAirport,
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = flight.arrivalAirport,
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                    
                    // 右侧价格和收藏
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = flight.price,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF007AFF)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                if (flight.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "收藏",
                                modifier = Modifier.size(16.dp),
                                tint = if (flight.isFavorite) Color(0xFFFF4081) else Color(0xFF999999)
                            )
                        }
                        
                        flight.originalPrice?.let { originalPrice ->
                            Text(
                                text = "普通价$originalPrice",
                                fontSize = 10.sp,
                                color = Color(0xFF999999)
                            )
                        }
                        
                        flight.discount?.let { discount ->
                            Text(
                                text = discount,
                                fontSize = 10.sp,
                                color = Color(0xFFFF6600)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 航空公司和机型信息
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = flight.airline,
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = flight.flightNumber,
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = flight.aircraftType,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                    if (flight.hasWifi) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "📶",
                            fontSize = 12.sp
                        )
                    }
                }
                
                // 标签
                if (flight.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(flight.tags) { tag ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        tag.contains("共享") -> Color(0xFFE3F2FD)
                                        tag.contains("送机") -> Color(0xFFE8F5E8)
                                        else -> Color(0xFFF0F0F0)
                                    }
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 10.sp,
                                    color = Color(0xFF007AFF),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun BottomSortSection(
        sortOptions: List<SortOption>,
        onSortClicked: (String) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                sortOptions.forEach { sortOption ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onSortClicked(sortOption.id) }
                    ) {
                        Text(
                            text = sortOption.icon,
                            fontSize = 20.sp
                        )
                        Text(
                            text = sortOption.title,
                            fontSize = 12.sp,
                            color = if (sortOption.isSelected) Color(0xFF007AFF) else Color(0xFF333333),
                            fontWeight = if (sortOption.isSelected) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
    
    // View interface implementations
    override fun showFlightListData(data: FlightListData) {
        flightListData = data
        // 记录搜索参数并标记列表已显示
        // 将英文舱位转换为中文
        val cabinChinese = when(cabinClass) {
            "economy" -> "经济舱"
            "business" -> "公务/头等舱"
            else -> cabinClass
        }
        SearchParamsManager.recordFlightSearch(
            context = context,
            from = departureCity,
            to = arrivalCity,
            date = selectedDate?.toString(),
            cabin = cabinChinese,
            listShown = true
        )
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
    
    override fun onFlightSelected(flightId: String) {
        // Handle flight selection
    }
    
    override fun onDateChanged(dateId: String) {
        // Handle date change
    }
    
    override fun onFilterChanged(filterId: String) {
        // Handle filter change
    }
    
    override fun onSortChanged(sortId: String) {
        // Handle sort change
    }
    
    override fun onBack() {
        // Handle back navigation
    }
    
    override fun showLowPriceAlert() {
        Toast.makeText(context, "低价提醒功能", Toast.LENGTH_SHORT).show()
    }
    
    override fun showMoreOptions() {
        Toast.makeText(context, "更多选项", Toast.LENGTH_SHORT).show()
    }
    
    override fun showCalendar() {
        Toast.makeText(context, "日历选择", Toast.LENGTH_SHORT).show()
    }
    
    override fun showPromotionDetails() {
        Toast.makeText(context, "机票次卡详情", Toast.LENGTH_SHORT).show()
    }
    
    override fun showMembershipUpgrade() {
        Toast.makeText(context, "会员升级", Toast.LENGTH_SHORT).show()
    }
}