package com.example.Ctrip.trip

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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.Ctrip.model.*

class TripTabView(private val context: Context) : TripTabContract.View {
    
    private lateinit var presenter: TripTabContract.Presenter
    private var tripData by mutableStateOf<TripData?>(null)
    private var isLoading by mutableStateOf(false)
    
    fun initialize() {
        val model = TripTabModelImpl(context)
        presenter = TripTabPresenter(model)
        presenter.attachView(this)
        presenter.loadTripData()
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TripTabScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeColor(0xFFF5F5F5))
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            TopBarSection()
            
            if (tripData != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        RoutePlanningSection()
                    }
                    
                    item {
                        TravelMapSection()
                    }
                    
                    tripData?.cityRecommendations?.let { cities ->
                        items(cities) { city ->
                            CityRecommendationSection(city)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("加载中...")
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
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBarSection() {
        TopAppBar(
            title = {
                Text(
                    text = if (tripData?.hasTrips == true) "我的行程" else "暂无行程",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = { presenter.onAddTripClicked() }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加行程",
                        tint = ComposeColor(0xFF666666)
                    )
                }
                IconButton(onClick = { presenter.onMoreOptionsClicked() }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = ComposeColor(0xFF666666)
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = ComposeColor(0xFFF0F8FF)
            )
        )
    }
    
    @Composable
    private fun RoutePlanningSection() {
        tripData?.let { data ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "线路规划",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "我的线路 >",
                            fontSize = 14.sp,
                            color = ComposeColor(0xFF666666),
                            modifier = Modifier.clickable { presenter.onStartPlanningClicked() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simplified attractions display
                    Text(
                        text = "景点数量: ${data.routePlanning.attractions.size}",
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = { presenter.onStartPlanningClicked() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ComposeColor(0xFF4A90E2)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = data.routePlanning.planningButtonText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = ComposeColor.White
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun AttractionItem(
        attraction: Attraction, 
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Column(
            modifier = modifier
                .clickable { onClick() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.size(56.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ComposeColor(android.graphics.Color.parseColor(attraction.color))
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = attraction.icon,
                        fontSize = 24.sp,
                        color = ComposeColor.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = attraction.name,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            if (attraction.description.isNotEmpty()) {
                Text(
                    text = attraction.description,
                    fontSize = 10.sp,
                    color = ComposeColor(0xFF666666),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
    
    @Composable
    private fun TravelMapSection() {
        tripData?.let { data ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { presenter.onTravelMapClicked() },
                colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.travelMap.icon,
                        fontSize = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.travelMap.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Text(
                        text = data.travelMap.subtitle + " >",
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF666666)
                    )
                }
            }
        }
    }
    
    @Composable
    private fun CityRecommendationSection(city: CityRecommendation) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ComposeColor.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = city.cityName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${city.moreText} >",
                        fontSize = 14.sp,
                        color = ComposeColor(0xFF666666),
                        modifier = Modifier.clickable { presenter.onCityMoreClicked(city.id) }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Simplified content display
                Text(
                    text = "内容数量: ${city.contents.size}",
                    fontSize = 14.sp
                )
            }
        }
    }
    
    @Composable
    private fun CityContentItem(
        content: CityContent, 
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Card(
            modifier = modifier
                .height(180.dp)
                .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFFF0F0F0)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Content image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            when (content.type) {
                                "guide" -> ComposeColor(0xFF4CAF50)
                                "note" -> ComposeColor(0xFF2196F3)
                                "planning" -> ComposeColor(0xFFFF5722)
                                else -> ComposeColor(0xFF666666)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (content.tag.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = ComposeColor.White.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = content.tag,
                                modifier = Modifier.padding(4.dp, 2.dp),
                                fontSize = 10.sp,
                                color = ComposeColor.Black
                            )
                        }
                    }
                    
                    Text(
                        text = when (content.type) {
                            "guide" -> "📋"
                            "note" -> "📝"
                            "planning" -> "🗺️"
                            else -> "📄"
                        },
                        fontSize = 32.sp
                    )
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = content.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (content.author.isNotEmpty()) {
                        Text(
                            text = content.author,
                            fontSize = 10.sp,
                            color = ComposeColor(0xFF666666),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
    
    override fun showTripData(data: TripData) {
        tripData = data
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
    
    override fun navigateToAddTrip() {
        Toast.makeText(context, "添加行程", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToMoreOptions() {
        Toast.makeText(context, "更多选项", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToRoutePlanning() {
        Toast.makeText(context, "开始规划路线", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToTravelMap() {
        Toast.makeText(context, "打开旅游地图", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToAttractionDetail(attractionId: String) {
        Toast.makeText(context, "查看景点详情: $attractionId", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToCityDetail(cityId: String) {
        Toast.makeText(context, "查看城市攻略: $cityId", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToContentDetail(contentId: String) {
        Toast.makeText(context, "查看内容详情: $contentId", Toast.LENGTH_SHORT).show()
    }
}