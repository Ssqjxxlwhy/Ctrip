package com.example.Ctrip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.Ctrip.home.HomeTabScreen
import com.example.Ctrip.message.MessageTabView
import com.example.Ctrip.trip.TripTabScreen
import com.example.Ctrip.my.MyTabScreen
import com.example.Ctrip.ui.theme.CtripTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CtripTheme {
                MainScreen()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 确保应用关闭时清理所有状态，下次启动时从主页开始
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainScreen() {
        var selectedTab by remember { mutableStateOf(0) }
        var showHotelBooking by remember { mutableStateOf(false) }
        var showFlightBooking by remember { mutableStateOf(false) }
        var showTrainBooking by remember { mutableStateOf(false) }
        var showHotelList by remember { mutableStateOf(false) }
        var hotelSearchParams by remember { mutableStateOf<com.example.Ctrip.home.HotelListSearchParams?>(null) }
        var showHotelDetail by remember { mutableStateOf(false) }
        var selectedHotelId by remember { mutableStateOf<String?>(null) }
        var showRoomTypeDetails by remember { mutableStateOf(false) }
        var selectedRoomType by remember { mutableStateOf<com.example.Ctrip.home.RoomType?>(null) }
        var showInfoConfirmation by remember { mutableStateOf(false) }
        val context = LocalContext.current
        
        val messageTabView = remember {
            MessageTabView(context).apply { initialize() }
        }
        
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                // Only show bottom navigation when not in any booking page
                if (!showHotelBooking && !showFlightBooking && !showTrainBooking && !showHotelList && !showHotelDetail && !showRoomTypeDetails && !showInfoConfirmation) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                            label = { Text("首页") },
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Email, contentDescription = "消息") },
                            label = { Text("消息") },
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Place, contentDescription = "行程") },
                            label = { Text("行程") },
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "我的") },
                            label = { Text("我的") },
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                when (selectedTab) {
                    0 -> {
                        // HomeTab content with booking navigation
                        HomeTabScreen(
                            showHotelBooking = showHotelBooking,
                            onHotelBookingChanged = { newValue ->
                                println("状态更新: showHotelBooking = $newValue")  // 调试日志
                                showHotelBooking = newValue
                            },
                            showFlightBooking = showFlightBooking,
                            onFlightBookingChanged = { newValue ->
                                println("状态更新: showFlightBooking = $newValue")  // 调试日志
                                showFlightBooking = newValue
                            },
                            showTrainBooking = showTrainBooking,
                            onTrainBookingChanged = { newValue ->
                                println("状态更新: showTrainBooking = $newValue")  // 调试日志
                                showTrainBooking = newValue
                            },
                            onHotelListRequested = { searchParams ->
                                hotelSearchParams = searchParams
                                showHotelList = true
                                showHotelBooking = false  // 关闭酒店预订页面
                            }
                        )
                    }
                    1 -> {
                        // MessageTab content
                        messageTabView.MessageTabScreen()
                    }
                    2 -> {
                        // TripTab content
                        TripTabScreen()
                    }
                    3 -> {
                        // MyTab content
                        MyTabScreen()
                    }
                }
            }
        }
        
        // Show Hotel List as full screen overlay
        if (showHotelList && hotelSearchParams != null) {
            com.example.Ctrip.home.HotelListTabScreen(
                searchParams = hotelSearchParams!!,
                onBackPressed = { 
                    showHotelList = false 
                    showHotelBooking = true  // 返回到酒店预订页面
                    hotelSearchParams = null
                },
                onHotelSelected = { hotel ->
                    selectedHotelId = hotel.id
                    showHotelDetail = true
                    showHotelList = false
                }
            )
        }
        
        // Show Hotel Detail as full screen overlay
        if (showHotelDetail && selectedHotelId != null && hotelSearchParams != null) {
            com.example.Ctrip.home.HotelDetailTabScreen(
                hotelId = selectedHotelId!!,
                searchParams = hotelSearchParams!!,
                onBackPressed = { 
                    showHotelDetail = false
                    showHotelList = true  // 返回到酒店列表页面
                    selectedHotelId = null
                },
                onRoomTypeSelected = { roomType ->
                    selectedRoomType = roomType
                    showRoomTypeDetails = true
                    showHotelDetail = false
                }
            )
        }
        
        // Show Room Type Details as full screen overlay
        if (showRoomTypeDetails && selectedRoomType != null && hotelSearchParams != null) {
            com.example.Ctrip.home.RoomTypeDetailsTabScreen(
                roomType = selectedRoomType!!,
                searchParams = hotelSearchParams!!,
                onBackPressed = { 
                    showRoomTypeDetails = false
                    showHotelDetail = true  // 返回到酒店详情页面
                    selectedRoomType = null
                },
                onNavigateToInfoConfirmation = { roomType, searchParams ->
                    showInfoConfirmation = true
                    showRoomTypeDetails = false
                }
            )
        }
        
        // Show Info Confirmation as full screen overlay
        if (showInfoConfirmation && selectedRoomType != null && hotelSearchParams != null) {
            val infoConfirmationView = remember {
                com.example.Ctrip.home.InfoConfirmationTabView(context).apply { 
                    initialize(selectedRoomType!!, hotelSearchParams!!) 
                }
            }
            
            infoConfirmationView.InfoConfirmationTabScreen(
                onBackPressed = {
                    showInfoConfirmation = false
                    showRoomTypeDetails = true
                }
            )
        }
    }
}