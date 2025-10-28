package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class OrderFormTabView(private val context: Context) : OrderFormTabContract.View {
    
    private lateinit var presenter: OrderFormTabContract.Presenter
    private var orderFormData by mutableStateOf<OrderFormData?>(null)
    private var isLoading by mutableStateOf(false)
    private var currentTotalPrice by mutableStateOf(0)
    
    fun initialize(ticketOptionId: String) {
        val model = OrderFormTabModel(context)
        presenter = OrderFormTabPresenter(model)
        presenter.attachView(this)
        presenter.loadOrderFormData(ticketOptionId)
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun OrderFormTabScreen(
        onNavigateToServiceSelect: (OrderFormData) -> Unit = {},
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
                // 顶部导航栏
                TopBarSection(onBack = onBack)
                
                // 主要内容区域
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    orderFormData?.let { data ->
                        // 出票安心提示
                        item {
                            TicketAssuranceSection()
                        }
                        
                        // 行程信息
                        item {
                            FlightInfoSection(
                                flightInfo = data.flightInfo,
                                priceBreakdown = data.priceBreakdown,
                                restrictions = data.restrictions
                            )
                        }
                        
                        // 选择乘机人
                        item {
                            PassengerSection(
                                passengers = data.passengers,
                                onAddPassenger = { presenter.onPassengerAddClicked() }
                            )
                        }
                        
                        // 联系电话
                        item {
                            ContactSection(
                                contactInfo = data.contactInfo,
                                onEditContact = { presenter.onContactEditClicked() },
                                onVerifyContact = { presenter.onContactVerifyClicked() }
                            )
                        }
                        
                        // 为行程添加保障
                        item {
                            InsuranceSection(
                                insuranceOptions = data.insuranceOptions,
                                onInsuranceOptionSelected = { type, optionId ->
                                    presenter.onInsuranceOptionSelected(type, optionId)
                                },
                                onInsuranceTermsClicked = { type ->
                                    presenter.onInsuranceTermsClicked(type)
                                },
                                onTravelInsuranceToggle = {
                                    presenter.onTravelInsuranceToggle()
                                }
                            )
                        }
                    }
                }
                
                // 底部下一步按钮
                BottomActionSection(
                    totalPrice = currentTotalPrice,
                    onNextStep = {
                        orderFormData?.let { data ->
                            presenter.onNextStepClicked()
                            onNavigateToServiceSelect(data)
                        }
                    }
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
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBarSection(onBack: () -> Unit) {
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
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // 流程导航
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "填订单",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4A90E2)
                    )
                    Text(
                        text = " → ",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = "选服务",
                        fontSize = 16.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = " → ",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                    Text(
                        text = "核对支付",
                        fontSize = 16.sp,
                        color = Color(0xFF999999)
                    )
                }
                
                // 携程客服
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { presenter.onCustomerServiceClicked() }
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "携程客服",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF666666)
                    )
                    Text(
                        text = "携程客服",
                        fontSize = 10.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TicketAssuranceSection() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF)),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "出票安心",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "出票安心",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "出票快，预计1小时内完成出票",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun FlightInfoSection(
        flightInfo: OrderFlightInfo,
        priceBreakdown: FlightPriceBreakdown,
        restrictions: OrderRestrictions
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 航班信息行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = flightInfo.tripType,
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(
                                    Color(0xFF4A90E2),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${flightInfo.route} ${flightInfo.date} ${flightInfo.time}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )
                    }
                    
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "详情",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 价格构成
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${priceBreakdown.adultPrice} + ${priceBreakdown.servicePrice} + ${priceBreakdown.taxPrice}",
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "价格说明",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 预订限制
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "🪪",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "限使用身份证，${restrictions.membershipRequired}，${restrictions.verificationRequired}",
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = restrictions.additionalInfo,
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PassengerSection(
        passengers: List<Passenger>,
        onAddPassenger: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 标题行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "选择乘机人",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onAddPassenger() }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "添加乘机人",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4A90E2)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "更多乘机人",
                            fontSize = 14.sp,
                            color = Color(0xFF4A90E2)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 乘机人列表
                if (passengers.isEmpty()) {
                    Text(
                        text = "当前价格仅可使用身份证预订",
                        fontSize = 14.sp,
                        color = Color(0xFF999999),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFFF5F5F5),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                } else {
                    passengers.forEach { passenger ->
                        PassengerItem(passenger)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
    
    @Composable
    private fun PassengerItem(passenger: Passenger) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFF5F5F5),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = passenger.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = passenger.passengerType,
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ContactSection(
        contactInfo: ContactInfo,
        onEditContact: () -> Unit,
        onVerifyContact: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 标题行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "联系电话",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "说明",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 电话号码行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${contactInfo.countryCode} ",
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "选择国家",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = contactInfo.phoneNumber,
                        fontSize = 16.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier.weight(1f)
                    )
                    
                    // 修改按钮
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "修改",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEditContact() },
                        tint = Color(0xFF999999)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // 识别按钮
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "识别",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onVerifyContact() },
                        tint = Color(0xFF4A90E2)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun InsuranceSection(
        insuranceOptions: InsuranceOptions,
        onInsuranceOptionSelected: (String, String) -> Unit,
        onInsuranceTermsClicked: (String) -> Unit,
        onTravelInsuranceToggle: () -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 标题
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🛡️",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "为行程添加保障",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "出行有保障，家人更放心",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 航意航延组合险
                FlightInsuranceGroup(
                    flightInsurance = insuranceOptions.flightInsurance,
                    onOptionSelected = { optionId ->
                        onInsuranceOptionSelected("flight", optionId)
                    },
                    onTermsClicked = {
                        onInsuranceTermsClicked("flight")
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 航空意外险
                AccidentInsuranceOption(
                    accidentInsurance = insuranceOptions.accidentInsurance,
                    onToggle = {
                        onInsuranceOptionSelected("accident", "toggle")
                    },
                    onTermsClicked = {
                        onInsuranceTermsClicked("accident")
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 国内旅行险
                TravelInsuranceOption(
                    travelInsurance = insuranceOptions.travelInsurance,
                    onToggle = onTravelInsuranceToggle
                )
            }
        }
    }
    
    @Composable
    private fun FlightInsuranceGroup(
        flightInsurance: FlightInsuranceGroup,
        onOptionSelected: (String) -> Unit,
        onTermsClicked: () -> Unit
    ) {
        Column {
            // 标题行
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = flightInsurance.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "投保须知",
                    fontSize = 12.sp,
                    color = Color(0xFF4A90E2),
                    modifier = Modifier.clickable { onTermsClicked() }
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 保险选项
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                flightInsurance.options.forEach { option ->
                    InsuranceOptionCard(
                        option = option,
                        modifier = Modifier.weight(1f),
                        onSelected = { onOptionSelected(option.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 多重保障保平安按钮
            Button(
                onClick = { onTermsClicked() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "多重保障保平安",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun InsuranceOptionCard(
        option: InsuranceOption,
        modifier: Modifier = Modifier,
        onSelected: () -> Unit
    ) {
        Card(
            modifier = modifier
                .clickable { onSelected() }
                .then(
                    if (option.isSelected) {
                        Modifier.border(2.dp, Color(0xFF4A90E2), RoundedCornerShape(8.dp))
                    } else {
                        Modifier.border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    }
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (option.isSelected) Color(0xFFF0F9FF) else Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = option.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )
                
                if (option.price.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = option.price,
                        fontSize = 12.sp,
                        color = Color(0xFF4A90E2),
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 保障内容
                if (option.benefits.isEmpty()) {
                    // 无保障显示红叉
                    repeat(3) {
                        Text(
                            text = "✗",
                            fontSize = 16.sp,
                            color = Color(0xFFFF4444)
                        )
                    }
                } else {
                    option.benefits.forEach { benefit ->
                        Text(
                            text = benefit,
                            fontSize = 10.sp,
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 选择圆圈
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(
                            2.dp,
                            if (option.isSelected) Color(0xFF4A90E2) else Color(0xFFCCCCCC),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (option.isSelected) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(Color(0xFF4A90E2), CircleShape)
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun AccidentInsuranceOption(
        accidentInsurance: AccidentInsurance,
        onToggle: () -> Unit,
        onTermsClicked: () -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 选择圆圈
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        2.dp,
                        if (accidentInsurance.isSelected) Color(0xFF4A90E2) else Color(0xFFCCCCCC),
                        CircleShape
                    )
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (accidentInsurance.isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF4A90E2), CircleShape)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = accidentInsurance.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "投保须知",
                        fontSize = 12.sp,
                        color = Color(0xFF4A90E2),
                        modifier = Modifier.clickable { onTermsClicked() }
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = accidentInsurance.description,
                    fontSize = 12.sp,
                    color = Color(0xFF666666),
                    lineHeight = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // 价格和下拉箭头
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = accidentInsurance.price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "展开",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF999999)
                )
            }
        }
    }
    
    @Composable
    private fun TravelInsuranceOption(
        travelInsurance: TravelInsurance,
        onToggle: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = travelInsurance.name,
                fontSize = 14.sp,
                color = Color(0xFF4A90E2),
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                if (travelInsurance.isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (travelInsurance.isExpanded) "收起" else "展开",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF4A90E2)
            )
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BottomActionSection(
        totalPrice: Int,
        onNextStep: () -> Unit
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
                // 总价显示
                Column {
                    Text(
                        text = "¥$totalPrice",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = "含税总价",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // 下一步按钮
                Button(
                    onClick = onNextStep,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "下一步",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // View interface implementations
    override fun showOrderFormData(data: OrderFormData) {
        orderFormData = data
        currentTotalPrice = data.priceBreakdown.totalPrice
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
    
    override fun navigateToServiceSelect(orderData: OrderFormData) {
        // 导航到服务选择页面
    }
    
    override fun onBack() {
        // 返回上一页
    }
    
    override fun showCustomerService() {
        Toast.makeText(context, "携程客服", Toast.LENGTH_SHORT).show()
    }
    
    override fun showPassengerSelector() {
        Toast.makeText(context, "选择乘机人", Toast.LENGTH_SHORT).show()
    }
    
    override fun showContactEditor() {
        Toast.makeText(context, "编辑联系方式", Toast.LENGTH_SHORT).show()
    }
    
    override fun showInsuranceTerms(insuranceType: String) {
        Toast.makeText(context, "查看保险条款: $insuranceType", Toast.LENGTH_SHORT).show()
    }
    
    override fun updateInsuranceSelection(insuranceType: String, optionId: String) {
        // UI状态已在showOrderFormData中更新
    }
    
    override fun updateTotalPrice(newPrice: Int) {
        currentTotalPrice = newPrice
    }
}