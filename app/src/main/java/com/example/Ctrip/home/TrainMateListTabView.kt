package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrainMateListTabView(private val context: Context) : TrainMateListTabContract.View {

    private lateinit var presenter: TrainMateListTabContract.Presenter
    private var trainList by mutableStateOf<List<TrainTicket>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var dateOptions by mutableStateOf<List<TrainDateOption>>(emptyList())
    private var transportOptions by mutableStateOf<List<TransportOption>>(emptyList())
    private var filterOptions by mutableStateOf(TrainFilterOptions(null, null, false, false, false))

    private var departureCity by mutableStateOf("")
    private var arrivalCity by mutableStateOf("")
    private var selectedDate by mutableStateOf(LocalDate.now())
    private var isStudentTicket by mutableStateOf(false)

    private var showTrainDetail by mutableStateOf(false)
    private var selectedTrainId by mutableStateOf("")

    fun initialize(departureCity: String, arrivalCity: String, departureDate: LocalDate, isStudentTicket: Boolean = false) {
        android.util.Log.d("TrainMateListTabView", "initialize called with: departureCity=$departureCity, arrivalCity=$arrivalCity, departureDate=$departureDate, isStudentTicket=$isStudentTicket")
        this.departureCity = departureCity
        this.arrivalCity = arrivalCity
        this.selectedDate = departureDate
        this.isStudentTicket = isStudentTicket

        val model = TrainMateListTabModel(context)
        presenter = TrainMateListTabPresenter(model)
        presenter.attachView(this)
        presenter.loadTrainList(departureCity, arrivalCity, departureDate)
    }

    @Composable
    fun TrainMateListTabScreen(
        onClose: () -> Unit = {}
    ) {
        // 如果显示详情页，则渲染详情页
        if (showTrainDetail) {
            val detailView = remember { TrainDetailTabView(context) }
            LaunchedEffect(selectedTrainId) {
                detailView.initialize(selectedTrainId, isStudentTicket)
            }
            detailView.TrainDetailTabScreen(
                onClose = {
                    showTrainDetail = false
                    selectedTrainId = ""
                }
            )
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // 返回按钮栏
            BackButtonBar(onClose)

            // 日期选择栏
            DateSelectionRow()

            // 出行方式选择栏
            TransportTypeRow()

            // 新客礼包推广栏
            NewCustomerGiftBanner()

            // 筛选栏
            FilterRow()

            // 火车票列表和底部功能栏
            Box(modifier = Modifier.weight(1f)) {
                TrainListAndBottomBar(trainList)
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
    private fun BackButtonBar(onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按钮
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    modifier = Modifier
                        .clickable {
                            onClose()
                        }
                        .size(20.dp),
                    tint = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // 显示出发地和目的地信息
                Text(
                    text = "$departureCity → $arrivalCity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopNavigationBar(onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 返回按钮
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    modifier = Modifier
                        .clickable {
                            presenter.onBackClicked()
                            onClose()
                        }
                        .size(24.dp),
                    tint = Color(0xFF333333)
                )

                // 城市信息
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = departureCity,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "⇌",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable { presenter.onSwapCities() },
                        fontSize = 20.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = arrivalCity,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                }

                // 右侧功能按钮
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { presenter.onGrabTicketClicked() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "抢票",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF4A90E2)
                        )
                        Text(
                            text = "抢票",
                            fontSize = 10.sp,
                            color = Color(0xFF4A90E2)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { presenter.onShareClicked() }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "分享",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF666666)
                        )
                        Text(
                            text = "分享",
                            fontSize = 10.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun DateSelectionRow() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(dateOptions) { option ->
                        DateChip(
                            option = option,
                            onClick = { presenter.onDateSelected(option.date) }
                        )
                    }
                }

                Text(
                    text = "📅",
                    modifier = Modifier.padding(start = 6.dp),
                    fontSize = 18.sp
                )
            }
        }
    }

    @Composable
    private fun DateChip(option: TrainDateOption, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .background(
                    color = if (option.isSelected) Color(0xFF4A90E2) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = option.displayText,
                    fontSize = 11.sp,
                    color = if (option.isSelected) Color.White else Color(0xFF333333),
                    textAlign = TextAlign.Center
                )
                if (option.subText.isNotEmpty()) {
                    Text(
                        text = option.subText,
                        fontSize = 9.sp,
                        color = if (option.isSelected) Color.White else Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    private fun TransportTypeRow() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                transportOptions.forEach { option ->
                    TransportTypeTab(
                        option = option,
                        onClick = { presenter.onTransportSelected(option.id) }
                    )
                }
            }
        }
    }

    @Composable
    private fun TransportTypeTab(option: TransportOption, onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            val icon = when (option.id) {
                "train" -> "🚄"
                "flight" -> "✈️"
                "transfer" -> "🔄"
                else -> "🚄"
            }
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            val displayText = if (option.priceFrom.isNotEmpty()) {
                "${option.title} ${option.priceFrom}"
            } else {
                option.title
            }
            Text(
                text = displayText,
                fontSize = 12.sp,
                color = if (option.isSelected) Color(0xFF4A90E2) else Color(0xFF666666),
                fontWeight = if (option.isSelected) FontWeight.Medium else FontWeight.Normal
            )
            if (option.isSelected) {
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
    private fun NewCustomerGiftBanner() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .clickable { presenter.onNewCustomerGiftClicked() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎁", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "送您一份新客礼包 价值¥130",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF6600)
                        )
                        Text(
                            text = "优惠 ¥10立减  权益 ¥80VIP抢票、¥25安心退改、¥15免费换座",
                            fontSize = 9.sp,
                            color = Color(0xFF999999),
                            maxLines = 1
                        )
                    }
                }
                Text(
                    text = "查看 >",
                    fontSize = 11.sp,
                    color = Color(0xFF4A90E2)
                )
            }
        }
    }

    @Composable
    private fun FilterRow() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(text = "上海虹桥站")
                FilterChip(text = "北京南站")
                FilterChip(text = "积分兑换")
                FilterChip(text = "仅看有票")
                FilterChip(text = "仅看直达")
            }
        }
    }

    @Composable
    private fun FilterChip(text: String) {
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { }
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                color = Color(0xFF666666)
            )
        }
    }

    @Composable
    private fun TrainListAndBottomBar(trains: List<TrainTicket>) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(trains) { train ->
                    TrainItemCard(train)
                }
            }

            // 底部功能栏
            BottomFunctionBar()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TrainItemCard(train: TrainTicket) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clickable { presenter.onTrainClicked(train.trainId) },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // 时间和车次信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // 出发时间
                    Column {
                        Text(
                            text = train.departureTime,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = train.departureStation,
                                fontSize = 12.sp,
                                color = Color(0xFF999999)
                            )
                            if (train.departureStation.contains("虹桥")) {
                                Text(
                                    text = " 🚇",
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // 历时和车次
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = train.duration,
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = train.trainNumber,
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                        if (train.trainType.isNotEmpty()) {
                            Text(
                                text = train.trainType,
                                fontSize = 10.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }

                    // 到达时间和价格
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = train.arrivalTime,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (train.arrivalStation.contains("南")) {
                                Text(
                                    text = "🚇 ",
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = train.arrivalStation,
                                fontSize = 12.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 座位和价格信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 座位信息
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        train.availableSeats.forEach { (seatType, count) ->
                            SeatInfo(seatType, count)
                        }
                    }

                    // 价格信息
                    Column(horizontalAlignment = Alignment.End) {
                        val lowestPrice = train.prices.values.filter { it > 0 }.minOrNull() ?: 0.0
                        Text(
                            text = "¥${lowestPrice.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A90E2)
                        )
                        Text(
                            text = "起",
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                        if (train.hasDiscount) {
                            Text(
                                text = "平台已补 ¥${train.discountAmount.toInt()}",
                                fontSize = 10.sp,
                                color = Color(0xFFFF6600),
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFFFF5E6),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // 特性标签
                if (train.features.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        train.features.forEach { feature ->
                            Text(
                                text = feature,
                                fontSize = 10.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        if (train.features.any { it.contains("同车换乘") }) {
                            Text(
                                text = "详情 ▸",
                                fontSize = 10.sp,
                                color = Color(0xFF4A90E2)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun SeatInfo(seatType: String, count: Int) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = seatType,
                fontSize = 10.sp,
                color = if (count > 0) Color(0xFF4CAF50) else Color(0xFF999999)
            )
            Text(
                text = if (count > 0) "${count}张" else "无座",
                fontSize = 10.sp,
                color = if (count > 0) Color(0xFF4CAF50) else Color(0xFF999999)
            )
        }
    }

    @Composable
    private fun BottomFunctionBar() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BottomFunctionItem("⚙️", "高级筛选")
                BottomFunctionItem("⏰", "出发最早")
                BottomFunctionItem("⏱️", "耗时最短")
                BottomFunctionItem("💰", "价格最低")
            }
        }
    }

    @Composable
    private fun BottomFunctionItem(icon: String, text: String) {
        val sortType = when (text) {
            "高级筛选" -> null
            "出发最早" -> SortType.EARLIEST_DEPARTURE
            "耗时最短" -> SortType.SHORTEST_DURATION
            "价格最低" -> SortType.LOWEST_PRICE
            else -> SortType.DEFAULT
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                if (sortType != null) {
                    presenter.onSortChanged(sortType)
                } else {
                    presenter.onShowFilter()
                }
            }
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                color = Color(0xFF666666)
            )
        }
    }

    // View interface implementations
    override fun showTrainList(trains: List<TrainTicket>) {
        trainList = trains
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

    override fun updateDateOptions(options: List<TrainDateOption>) {
        dateOptions = options
    }

    override fun updateTransportOptions(options: List<TransportOption>) {
        transportOptions = options
    }

    override fun updateFilterInfo(filterOptions: TrainFilterOptions) {
        this.filterOptions = filterOptions
    }

    override fun navigateToTrainDetail(trainId: String) {
        selectedTrainId = trainId
        showTrainDetail = true
    }

    override fun showFilterDialog() {
        Toast.makeText(context, "显示筛选对话框", Toast.LENGTH_SHORT).show()
    }

    override fun navigateBack() {
        // Handle back navigation
    }

    override fun showShareDialog() {
        Toast.makeText(context, "分享行程", Toast.LENGTH_SHORT).show()
    }

    override fun showGrabTicketInfo() {
        Toast.makeText(context, "抢票功能", Toast.LENGTH_SHORT).show()
    }

    override fun showNewCustomerGift() {
        Toast.makeText(context, "查看新客礼包", Toast.LENGTH_SHORT).show()
    }
}
