package com.example.Ctrip.home

import android.content.Context
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class InfoConfirmTabView(private val context: Context) : InfoConfirmTabContract.View {

    private lateinit var presenter: InfoConfirmTabContract.Presenter
    private var confirmData by mutableStateOf<InfoConfirmData?>(null)
    private var isLoading by mutableStateOf(false)
    private var showDetailExpanded by mutableStateOf(false)
    private var navigationCallback: ((InfoConfirmData) -> Unit)? = null

    fun initialize(
        trainId: String,
        ticketOption: TrainTicketOption,
        selectedSeatType: String
    ) {
        val model = InfoConfirmTabModel(context)
        presenter = InfoConfirmTabPresenter(model)
        presenter.attachView(this)
        presenter.loadConfirmData(trainId, ticketOption, selectedSeatType)
    }

    @Composable
    fun InfoConfirmTabScreen(onClose: () -> Unit = {}, onNavigateToPayment: (InfoConfirmData) -> Unit = {}) {
        // 保存回调到成员变量
        LaunchedEffect(Unit) {
            navigationCallback = onNavigateToPayment
        }

        Box(modifier = Modifier.fillMaxSize()) {
            confirmData?.let { data ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF5F5F5))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        item { TopBar(data, onClose) }
                        item { TripInfoSection(data) }
                        item { Login12306Section() }
                        item { PassengerSection(data) }
                        item { PhoneSection(data) }
                        item { CarriageHintSection() }
                        item { CarriageSelectionSection(data) }
                        item { SeatSelectionSection(data) }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }

                    BottomBar(data)
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
    private fun TopBar(data: InfoConfirmData, onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    modifier = Modifier
                        .clickable { onClose() }
                        .size(24.dp),
                    tint = Color(0xFF333333)
                )

                Text(
                    text = "🍁 ${data.insuranceName} 🍁",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFFF6600)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { }
                    ) {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "客服",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF333333)
                        )
                        Text(
                            text = "客服",
                            fontSize = 10.sp,
                            color = Color(0xFF666666)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { presenter.onShowDetailClicked() }
                    ) {
                        Text(text = "≡", fontSize = 18.sp, color = Color(0xFF333333))
                        Text(
                            text = "须知",
                            fontSize = 10.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TripInfoSection(data: InfoConfirmData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // 行程信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF4A90E2), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "单程",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${data.departureStation}→${data.arrivalStation} ${data.departureDate} ${data.departureTime}",
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showDetailExpanded = !showDetailExpanded }
                    ) {
                        Text(
                            text = "详情",
                            fontSize = 13.sp,
                            color = Color(0xFF4A90E2)
                        )
                        Icon(
                            if (showDetailExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 票价信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${data.seatType}¥${data.ticketPrice}  ${data.insuranceName} ¥${data.insurancePrice}x1",
                        fontSize = 13.sp,
                        color = Color(0xFF666666)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { }
                    ) {
                        Text(
                            text = "更换",
                            fontSize = 13.sp,
                            color = Color(0xFF4A90E2)
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Login12306Section() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎫", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "登录12306账号，",
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "积分兑换车票",
                            fontSize = 14.sp,
                            color = Color(0xFFFF6600),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "激活会员可得2880积分，100积分抵扣1元 >",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }

                Button(
                    onClick = { presenter.onLoginClicked() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A90E2)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = "去登录",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    private fun PassengerSection(data: InfoConfirmData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // 一键添加提示
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "使用以下乘客信息一键添加",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4A90E2)
                        )
                        Text(
                            text = "更多",
                            fontSize = 13.sp,
                            color = Color(0xFF4A90E2)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 乘客信息卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4A90E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = data.currentUser.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${getPassengerTypeText(data.currentUser.passengerType)}  ${getIDTypeText(data.currentUser.idType)} ${data.currentUser.idNumber}",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 删除按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "删除",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { },
                        tint = Color(0xFF999999)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 添加乘客按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { presenter.onAddPassengerClicked() }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "添加/修改乘客（成人/学生/儿童）",
                        fontSize = 14.sp,
                        color = Color(0xFF4A90E2)
                    )
                }
            }
        }
    }

    @Composable
    private fun PhoneSection(data: InfoConfirmData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "手机号码",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "+86",
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatPhone(data.currentUser.phone),
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                }

                Icon(
                    Icons.Default.Edit,
                    contentDescription = "修改",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { presenter.onPhoneEditClicked() },
                    tint = Color(0xFF4A90E2)
                )
            }
        }
    }

    @Composable
    private fun CarriageHintSection() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF0)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "长途出行 选靠餐车车厢更方便",
                        fontSize = 13.sp,
                        color = Color(0xFF666666)
                    )
                }

                TextButton(
                    onClick = { presenter.onSeatDiagramClicked() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "座席图示",
                        fontSize = 12.sp,
                        color = Color(0xFF4A90E2)
                    )
                }
            }
        }
    }

    @Composable
    private fun CarriageSelectionSection(data: InfoConfirmData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "选车厢(可选3个)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    Text(
                        text = "¥12/人",
                        fontSize = 13.sp,
                        color = Color(0xFFFF6600)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.availableCarriages) { carriage ->
                        CarriageItem(carriage)
                    }
                }
            }
        }
    }

    @Composable
    private fun CarriageItem(carriage: CarriageInfo) {
        val backgroundColor = when {
            carriage.isSelected -> Color(0xFF4A90E2)
            carriage.tag == "餐车" -> Color(0xFFFFE4B5)
            else -> Color(0xFFE0E0E0)
        }

        val textColor = if (carriage.isSelected) Color.White else Color(0xFF333333)

        Box(
            modifier = Modifier
                .size(60.dp, 50.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(backgroundColor)
                .clickable { presenter.onCarriageSelected(carriage.number) },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = carriage.number,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                if (carriage.tag.isNotEmpty()) {
                    Text(
                        text = carriage.tag,
                        fontSize = 9.sp,
                        color = if (carriage.tag == "餐车") Color(0xFFFF6600) else Color(0xFF4CAF50),
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }

    @Composable
    private fun SeatSelectionSection(data: InfoConfirmData) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "在线选座",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )

                    Text(
                        text = "0/1",
                        fontSize = 13.sp,
                        color = Color(0xFF999999)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 座位位置选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    data.availableSeats.filter {
                        it.position == SeatPosition.WINDOW || it.position == SeatPosition.AISLE
                    }.forEach { seat ->
                        SeatPositionItem(seat)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 车门位置选择
                data.availableSeats.filter {
                    it.position == SeatPosition.NEAR_DOOR || it.position == SeatPosition.FAR_DOOR
                }.forEach { seat ->
                    SeatOptionItem(seat)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    @Composable
    private fun SeatPositionItem(seat: SeatSelectionInfo) {
        val backgroundColor = if (seat.isSelected) Color(0xFF4A90E2) else Color(0xFFE8E8E8)
        val textColor = if (seat.isSelected) Color.White else Color(0xFF666666)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { presenter.onSeatSelected(seat.position) }
        ) {
            Text(
                text = if (seat.position == SeatPosition.WINDOW) "靠\n窗" else "过\n道",
                fontSize = 11.sp,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = seat.label,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }

    @Composable
    private fun SeatOptionItem(seat: SeatSelectionInfo) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF8F8F8))
                .clickable { presenter.onSeatSelected(seat.position) }
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = seat.isSelected,
                    onClick = { presenter.onSeatSelected(seat.position) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF4A90E2)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = seat.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = seat.description,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }

            Text(
                text = "¥${seat.price}/人",
                fontSize = 13.sp,
                color = Color(0xFFFF6600)
            )
        }
    }

    @Composable
    private fun BottomBar(data: InfoConfirmData) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            // 优惠提示
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFFFE4E1),
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "新客专享",
                            fontSize = 11.sp,
                            color = Color(0xFFFF6600),
                            modifier = Modifier
                                .background(
                                    Color.White,
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${data.newCustomerDiscount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6600)
                        )
                        Text(
                            text = "元 优惠已立减",
                            fontSize = 12.sp,
                            color = Color(0xFFFF6600)
                        )
                    }

                    Text(
                        text = "2天11:14:01后失效",
                        fontSize = 11.sp,
                        color = Color(0xFF999999)
                    )
                }
            }

            // 底部支付栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "¥",
                            fontSize = 14.sp,
                            color = Color(0xFFFF6600)
                        )
                        val totalPrice = data.ticketPrice + data.insurancePrice - data.newCustomerDiscount
                        Text(
                            text = "$totalPrice",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF6600)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    Color(0xFFFFE4E1),
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "新客专享",
                                fontSize = 10.sp,
                                color = Color(0xFFFF6600)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "已优惠¥${data.newCustomerDiscount}",
                            fontSize = 11.sp,
                            color = Color(0xFF999999)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "得${data.points}积分，价值¥${data.pointsValue}",
                            fontSize = 11.sp,
                            color = Color(0xFF999999)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "明细",
                            fontSize = 11.sp,
                            color = Color(0xFF4A90E2)
                        )
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF4A90E2)
                        )
                    }
                }

                Button(
                    onClick = { presenter.onConfirmOrderClicked() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6600)
                    ),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .widthIn(min = 150.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "立即预订",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = "享7大全能保障",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Helper functions
    private fun getPassengerTypeText(type: PassengerType): String {
        return when (type) {
            PassengerType.ADULT -> "成人票"
            PassengerType.STUDENT -> "学生票"
            PassengerType.CHILD -> "儿童票"
        }
    }

    private fun getIDTypeText(type: IDType): String {
        return when (type) {
            IDType.ID_CARD -> "二代身份证"
            IDType.PASSPORT -> "护照"
            IDType.OTHER -> "其他"
        }
    }

    private fun formatPhone(phone: String): String {
        // 格式化电话号码显示
        return phone.replace("*", "")
            .chunked(4)
            .joinToString(" ")
            .let {
                if (phone.contains("*")) {
                    phone.replace(Regex("\\d{4}\\d{4}"), "****")
                        .chunked(4)
                        .joinToString(" ")
                } else {
                    it
                }
            }
    }

    // View interface implementations
    override fun showConfirmData(data: InfoConfirmData) {
        confirmData = data
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

    override fun navigateBack() {
        // Handle by parent
    }

    override fun navigateToLogin() {
        Toast.makeText(context, "跳转到12306登录", Toast.LENGTH_SHORT).show()
    }

    override fun navigateToPayment(confirmData: InfoConfirmData) {
        // 传递完整的订单确认数据到支付页面
        navigationCallback?.invoke(confirmData) ?: run {
            Toast.makeText(context, "跳转到支付页面", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showPassengerPicker() {
        Toast.makeText(context, "选择乘客", Toast.LENGTH_SHORT).show()
    }

    override fun showPhoneEditor() {
        Toast.makeText(context, "修改手机号", Toast.LENGTH_SHORT).show()
    }

    override fun showSeatDiagram() {
        Toast.makeText(context, "查看座席图示", Toast.LENGTH_SHORT).show()
    }
}
