package com.example.Ctrip.home

import android.content.Context
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.example.Ctrip.model.City
import com.example.Ctrip.model.LocationRegion

class DestinationSelectTabView(private val context: Context) : DestinationSelectTabContract.View {
    
    private lateinit var presenter: DestinationSelectTabContract.Presenter
    private var destinationData by mutableStateOf<DestinationSelectionData?>(null)
    private var isLoading by mutableStateOf(false)
    private var searchText by mutableStateOf("")
    private var currentRegion by mutableStateOf(LocationRegion.DOMESTIC)
    private var isMultiSelect by mutableStateOf(false)
    
    fun initialize() {
        val model = DestinationSelectTabModel(context)
        presenter = DestinationSelectTabPresenter(model)
        presenter.attachView(this)
        presenter.loadDestinationData()
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DestinationSelectTabScreen(
        onDestinationSelected: (City) -> Unit = {},
        onClose: () -> Unit = {}
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
                TopBarSection(onClose = onClose)
                
                // 搜索栏
                SearchSection()
                
                // 国内/国际标签栏
                RegionTabsSection()
                
                // 主内容区域，包含右侧分类索引
                Box(modifier = Modifier.weight(1f)) {
                    // 左侧内容
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (destinationData != null) {
                            // 历史板块
                            item { 
                                HistorySection()
                            }
                            
                            // 热门推荐板块
                            item { 
                                HotRecommendationSection(
                                    specialOffers = destinationData!!.specialOffers,
                                    hotCities = destinationData!!.hotCities,
                                    onDestinationClicked = { city ->
                                        presenter.onDestinationClicked(city)
                                        onDestinationSelected(city)
                                    },
                                    onSpecialOfferClicked = { offerId ->
                                        presenter.onSpecialOfferClicked(offerId)
                                    }
                                )
                            }
                            
                            // 目的地口碑榜
                            item { 
                                ReputationRankingSection(
                                    rankings = destinationData!!.reputationRankings,
                                    onRankingClicked = { rankingId ->
                                        presenter.onReputationRankingClicked(rankingId)
                                    },
                                    onDestinationClicked = { city ->
                                        presenter.onDestinationClicked(city)
                                        onDestinationSelected(city)
                                    }
                                )
                            }
                            
                            // 热门地区
                            item { 
                                HotRegionSection(
                                    regions = destinationData!!.hotRegions,
                                    onRegionClicked = { regionId ->
                                        presenter.onHotRegionClicked(regionId)
                                    }
                                )
                            }
                        }
                    }
                    
                    // 右侧分类栏
                    RightCategoryIndex()
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
    private fun TopBarSection(onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧关闭按钮
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    modifier = Modifier
                        .clickable { 
                            presenter.onCloseClicked()
                            onClose()
                        }
                        .size(24.dp),
                    tint = Color(0xFF333333)
                )
                
                // 中间标题
                Text(
                    text = "选择目的地",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                
                // 右侧单选多选选项
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 单选按钮
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!isMultiSelect) Color(0xFF007AFF) else Color.Transparent
                        ),
                        modifier = Modifier.clickable { 
                            isMultiSelect = false
                            presenter.onMultiSelectTabClicked(false)
                        }
                    ) {
                        Text(
                            text = "单选",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            color = if (!isMultiSelect) Color.White else Color(0xFF666666)
                        )
                    }
                    
                    // 多选按钮
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMultiSelect) Color(0xFF007AFF) else Color.Transparent
                        ),
                        modifier = Modifier.clickable { 
                            isMultiSelect = true
                            presenter.onMultiSelectTabClicked(true)
                        }
                    ) {
                        Text(
                            text = "多选",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp,
                            color = if (isMultiSelect) Color.White else Color(0xFF666666)
                        )
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SearchSection() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newValue ->
                    searchText = newValue
                    presenter.onSearchClicked(newValue)
                },
                placeholder = {
                    Text(
                        text = "试试搜索'亲子、避暑、海滨'",
                        color = Color(0xFF999999),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color(0xFF999999)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun RegionTabsSection() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // 国内标签
                Column(
                    modifier = Modifier.clickable { 
                        currentRegion = LocationRegion.DOMESTIC
                        presenter.onRegionTabClicked(LocationRegion.DOMESTIC)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "国内",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (currentRegion == LocationRegion.DOMESTIC) Color(0xFF007AFF) else Color(0xFF333333),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    if (currentRegion == LocationRegion.DOMESTIC) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Color(0xFF007AFF))
                        )
                    }
                }
                
                // 国际/中国港澳台标签
                Column(
                    modifier = Modifier.clickable { 
                        currentRegion = LocationRegion.INTERNATIONAL
                        presenter.onRegionTabClicked(LocationRegion.INTERNATIONAL)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "国际/中国港澳台",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (currentRegion == LocationRegion.INTERNATIONAL) Color(0xFF007AFF) else Color(0xFF333333),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    if (currentRegion == LocationRegion.INTERNATIONAL) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Color(0xFF007AFF))
                        )
                    }
                }
            }
        }
    }
    
    @Composable
    private fun HistorySection() {
        Column {
            Text(
                text = "历史（含国内、国际/中国港澳台）",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "📍",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "定位未开启",
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HotRecommendationSection(
        specialOffers: List<DestinationSpecialOffer>,
        hotCities: List<City>,
        onDestinationClicked: (City) -> Unit,
        onSpecialOfferClicked: (String) -> Unit
    ) {
        Column {
            Text(
                text = "热门推荐",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // 特殊优惠区域
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                specialOffers.forEach { offer ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .clickable { onSpecialOfferClicked(offer.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(android.graphics.Color.parseColor(offer.backgroundColor))
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = offer.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                if (offer.subtitle.isNotEmpty()) {
                                    Text(
                                        text = offer.subtitle,
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 热门城市网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hotCities.take(8)) { city ->
                    CityChip(
                        cityName = city.cityName,
                        onClick = { onDestinationClicked(city) }
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CityChip(
        cityName: String,
        onClick: () -> Unit
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Text(
                text = cityName,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ReputationRankingSection(
        rankings: List<ReputationRanking>,
        onRankingClicked: (String) -> Unit,
        onDestinationClicked: (City) -> Unit
    ) {
        Column {
            Text(
                text = "🏆目的地口碑榜",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rankings) { ranking ->
                    RankingCard(
                        ranking = ranking,
                        onRankingClicked = onRankingClicked,
                        onDestinationClicked = onDestinationClicked
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun RankingCard(
        ranking: ReputationRanking,
        onRankingClicked: (String) -> Unit,
        onDestinationClicked: (City) -> Unit
    ) {
        Card(
            modifier = Modifier
                .width(150.dp)
                .height(180.dp)
                .clickable { onRankingClicked(ranking.id) },
            colors = CardDefaults.cardColors(
                containerColor = Color(android.graphics.Color.parseColor(ranking.backgroundColor))
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = ranking.icon,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = ranking.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                
                ranking.cities.take(3).forEachIndexed { index, rankingCity ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDestinationClicked(rankingCity.city) }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "${rankingCity.rank}",
                            fontSize = 12.sp,
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = rankingCity.city.cityName,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HotRegionSection(
        regions: List<HotRegion>,
        onRegionClicked: (String) -> Unit
    ) {
        Column {
            Text(
                text = "热门地区",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(regions) { region ->
                    Card(
                        modifier = Modifier
                            .width(120.dp)
                            .height(80.dp)
                            .clickable { onRegionClicked(region.id) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90E2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = region.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun RightCategoryIndex() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(60.dp)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val categories = listOf("历史", "热门", "主题", "地区")
                items(categories) { category ->
                    Text(
                        text = category,
                        fontSize = 12.sp,
                        color = Color(0xFF007AFF),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { presenter.onCategoryClicked(category) }
                            .padding(4.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                // 字母索引
                val alphabet = ('A'..'Z').toList()
                items(alphabet) { letter ->
                    Text(
                        text = letter.toString(),
                        fontSize = 10.sp,
                        color = Color(0xFF007AFF),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { /* TODO: 滚动到对应字母区域 */ }
                            .padding(2.dp)
                    )
                }
            }
        }
    }
    
    // View interface implementations
    override fun showDestinationData(data: DestinationSelectionData) {
        destinationData = data
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
    
    override fun onDestinationSelected(city: City) {
        // Handle destination selection
    }
    
    override fun onClose() {
        // Handle close action
    }
    
    override fun onSearchRequested(query: String) {
        searchText = query
    }
    
    override fun onRegionChanged(region: LocationRegion) {
        currentRegion = region
    }
    
    override fun onMultiSelectToggled(isMultiSelect: Boolean) {
        this.isMultiSelect = isMultiSelect
    }
}