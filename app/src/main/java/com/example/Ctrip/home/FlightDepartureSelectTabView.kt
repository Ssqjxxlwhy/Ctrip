package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.Ctrip.model.CitySelectionData
import com.example.Ctrip.model.LocationRegion
import com.example.Ctrip.home.FlightDepartureSelectTabContract

class FlightDepartureSelectTabView(private val context: Context) : FlightDepartureSelectTabContract.View {

    private lateinit var presenter: FlightDepartureSelectTabContract.Presenter
    private var citySelectionData by mutableStateOf<CitySelectionData?>(null)
    private var isLoading by mutableStateOf(false)
    private var searchText by mutableStateOf("")
    private var currentRegion by mutableStateOf(LocationRegion.DOMESTIC)
    private var isMultiSelect by mutableStateOf(false)
    
    fun initialize() {
        val model = FlightDepartureSelectTabModel(context)
        presenter = FlightDepartureSelectTabPresenter(model)
        presenter.attachView(this)
        presenter.loadCityData()
    }

    @Composable
    fun FlightDepartureSelectTabScreen(
        onCitySelected: (City) -> Unit = {},
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
                
                // 主内容区域，包含右侧字母索引
                Box(modifier = Modifier.weight(1f)) {
                    // 左侧内容
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (citySelectionData != null) {
                            // 历史板块
                            item { 
                                HistorySection()
                            }
                            
                            // 热门城市板块
                            item { 
                                HotCitiesSection(
                                    onCityClicked = { city ->
                                        presenter.onCityClicked(city)
                                        onCitySelected(city)
                                    }
                                )
                            }
                            
                            // 字母索引
                            item { 
                                AlphabetIndexSection()
                            }
                            
                            // 按字母分类的城市列表
                            val citiesByLetter = if (currentRegion == LocationRegion.DOMESTIC) {
                                citySelectionData?.domesticCities ?: emptyMap()
                            } else {
                                citySelectionData?.internationalCities ?: emptyMap()
                            }
                            
                            items(citiesByLetter.toList()) { (letter, cities) ->
                                CityGroupSection(
                                    letter = letter,
                                    cities = cities,
                                    onCityClicked = { city ->
                                        presenter.onCityClicked(city)
                                        onCitySelected(city)
                                    }
                                )
                            }
                        }
                    }
                    
                    // 右侧字母索引栏
                    RightAlphabetIndex()
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
                    text = "选择出发地",
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
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isMultiSelect) Color(0xFF007AFF) else Color.Transparent,
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
    private fun HotCitiesSection(onCityClicked: (City) -> Unit) {
        Column {
            Text(
                text = "热门城市",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val hotCities = listOf(
                "上海", "北京", "成都", "广州",
                "乌鲁木齐", "昆明", "深圳", "重庆",
                "西安", "杭州", "青岛", "三亚",
                "南京", "哈尔滨", "贵阳", "大连"
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.height(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hotCities) { cityName ->
                    CityChip(
                        cityName = cityName,
                        onClick = { 
                            val city = City(
                                cityId = cityName,
                                cityName = cityName,
                                cityCode = cityName,
                                province = "未知",
                                country = "中国",
                                isHot = true,
                                pinyin = cityName,
                                firstLetter = cityName.first().toString(),
                                isInternational = false
                            )
                            onCityClicked(city)
                        }
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
    private fun AlphabetIndexSection() {
        Column {
            Text(
                text = "字母索引",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val alphabet = ('A'..'Z').toList()
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(150.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(alphabet) { letter ->
                    Card(
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { /* TODO: 滚动到对应字母区域 */ }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = letter.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CityGroupSection(
        letter: String,
        cities: List<City>,
        onCityClicked: (City) -> Unit
    ) {
        Column {
            // 字母标题
            Text(
                text = letter,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // 城市列表
            Column {
                cities.forEach { city ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCityClicked(city) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = city.cityName,
                                fontSize = 16.sp,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = city.cityCode,
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                    Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
                }
            }
        }
    }
    
    @Composable
    private fun RightAlphabetIndex() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(20.dp)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
    override fun showCitySelectionData(data: CitySelectionData) {
        citySelectionData = data
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
    
    override fun onCitySelected(city: City) {
        // Handle city selection
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