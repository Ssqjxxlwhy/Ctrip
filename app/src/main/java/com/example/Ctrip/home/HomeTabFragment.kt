package com.example.Ctrip.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class HomeTabFragment : Fragment(), HomeTabContract.View {
    
    private lateinit var presenter: HomeTabContract.Presenter
    private var homeData by mutableStateOf<HomeTabData?>(null)
    private var isLoading by mutableStateOf(false)
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val model = HomeTabModelImpl(requireContext())
        presenter = HomeTabPresenter(model)
        presenter.attachView(this)
        
        return ComposeView(requireContext()).apply {
            setContent {
                HomeTabScreen()
            }
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadHomeData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }
    
    @Composable
    private fun HomeTabScreen() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ComposeColor(0xFFF5F5F5)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                HeaderSection()
            }
            
            item {
                MainActionsSection()
            }
            
            item {
                SecondaryActionsSection()
            }
            
            item {
                MoreProductsSection()
            }
            
            item {
                SearchSection()
            }
            
            item {
                QuickSearchSection()
            }
            
            item {
                ContentSectionsRow()
            }
            
            item {
                PromotionSection()
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
    private fun HeaderSection() {
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
    private fun MainActionsSection() {
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
    private fun SecondaryActionsSection() {
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
    private fun SearchSection() {
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
    private fun QuickSearchSection() {
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
    private fun ContentSectionsRow() {
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
    private fun PromotionSection() {
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
    
    // HomeTabContract.View implementation
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
        Toast.makeText(context, "导航到酒店页面", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToFlight() {
        Toast.makeText(context, "导航到机票页面", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToTrain() {
        Toast.makeText(context, "导航到火车票页面", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToGuideAttraction() {
        Toast.makeText(context, "导航到攻略/景点页面", Toast.LENGTH_SHORT).show()
    }
    
    override fun navigateToTravelGroup() {
        Toast.makeText(context, "导航到旅游/跟团页面", Toast.LENGTH_SHORT).show()
    }
    
    override fun onQuickActionClick(actionId: String) {
        Toast.makeText(context, "点击了: $actionId", Toast.LENGTH_SHORT).show()
    }
    
    override fun onSearchClick(query: String) {
        Toast.makeText(context, "搜索: $query", Toast.LENGTH_SHORT).show()
    }
    
    override fun onPromotionClick(promotionId: String) {
        Toast.makeText(context, "点击促销: $promotionId", Toast.LENGTH_SHORT).show()
    }
}