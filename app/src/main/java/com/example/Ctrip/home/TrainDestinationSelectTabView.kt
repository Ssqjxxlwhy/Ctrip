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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.Ctrip.model.City
import com.example.Ctrip.model.CitySelectionData
import com.example.Ctrip.model.LocationRegion

class TrainDestinationSelectTabView(private val context: Context) : TrainDestinationSelectTabContract.View {

    private lateinit var presenter: TrainDestinationSelectTabContract.Presenter
    private var citySelectionData by mutableStateOf<CitySelectionData?>(null)
    private var isLoading by mutableStateOf(false)
    private var searchText by mutableStateOf("")
    private var currentRegion by mutableStateOf(LocationRegion.DOMESTIC)
    private var isMultiSelect by mutableStateOf(false)
    private var departureCity by mutableStateOf("上海") // 出发地，用于显示"XX出发的热门去处"

    fun initialize(fromCity: String = "上海") {
        departureCity = fromCity
        val model = TrainDestinationSelectTabModel(context)
        presenter = TrainDestinationSelectTabPresenter(model)
        presenter.attachView(this)
        presenter.loadCityData()
    }

    @Composable
    fun TrainDestinationSelectTabScreen(
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
                // 顶部搜索栏和返回按钮
                TopSearchSection(onClose = onClose)

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
                            // 附近站点板块
                            item {
                                NearbyStationsSection()
                            }

                            // XX出发的热门去处板块
                            item {
                                HotDestinationsSection(
                                    departureCity = departureCity,
                                    onCityClicked = { city ->
                                        presenter.onCityClicked(city)
                                        onCitySelected(city)
                                    }
                                )
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopSearchSection(onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 左侧返回按钮
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    modifier = Modifier
                        .clickable {
                            presenter.onCloseClicked()
                            onClose()
                        }
                        .size(24.dp),
                    tint = Color(0xFF333333)
                )

                // 搜索框
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { newValue ->
                        searchText = newValue
                        presenter.onSearchClicked(newValue)
                    },
                    placeholder = {
                        Text(
                            text = "请输入目的城市/车站名",
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
                        .weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF5F5F5),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
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
                        color = if (currentRegion == LocationRegion.DOMESTIC) Color(0xFF4A90E2) else Color(0xFF333333),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    if (currentRegion == LocationRegion.DOMESTIC) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Color(0xFF4A90E2))
                        )
                    }
                }

                // 国际标签
                Column(
                    modifier = Modifier.clickable {
                        currentRegion = LocationRegion.INTERNATIONAL
                        presenter.onRegionTabClicked(LocationRegion.INTERNATIONAL)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "国际",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (currentRegion == LocationRegion.INTERNATIONAL) Color(0xFF4A90E2) else Color(0xFF333333),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    if (currentRegion == LocationRegion.INTERNATIONAL) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(2.dp)
                                .background(Color(0xFF4A90E2))
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun NearbyStationsSection() {
        Column {
            Text(
                text = "附近站点",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "定位",
                        tint = Color(0xFF4A90E2),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "定位未开",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "附近",
                        fontSize = 14.sp,
                        color = Color(0xFF4A90E2),
                        modifier = Modifier.clickable { /* TODO: 打开定位 */ }
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun HotDestinationsSection(
        departureCity: String,
        onCityClicked: (City) -> Unit
    ) {
        Column {
            Text(
                text = "${departureCity}出发的热门去处",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 根据出发城市显示不同的热门目的地
            val hotDestinations = when (departureCity) {
                "上海" -> listOf(
                    "杭州", "苏州", "南京",
                    "无锡", "北京", "嘉兴",
                    "常州", "宁波", "合肥",
                    "南通", "金华", "盐城"
                )
                "北京" -> listOf(
                    "天津", "上海", "南京",
                    "杭州", "西安", "沈阳",
                    "郑州", "济南", "石家庄",
                    "太原", "哈尔滨", "长春"
                )
                else -> listOf(
                    "北京", "上海", "广州",
                    "深圳", "杭州", "南京",
                    "成都", "重庆", "武汉",
                    "西安", "郑州", "长沙"
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(160.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hotDestinations) { cityName ->
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = cityName,
                    fontSize = 14.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF999999),
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
                                .padding(vertical = 12.dp, horizontal = 0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = city.cityName,
                                fontSize = 16.sp,
                                color = Color(0xFF333333)
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
                verticalArrangement = Arrangement.spacedBy(1.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val letters = listOf("附近", departureCity) + ('A'..'Z').map { it.toString() }
                items(letters) { letter ->
                    Text(
                        text = letter,
                        fontSize = 10.sp,
                        color = Color(0xFF4A90E2),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { /* TODO: 滚动到对应字母区域 */ }
                            .padding(vertical = 2.dp)
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

    override fun onRegionChanged(region: LocationRegion) {
        currentRegion = region
    }

    override fun onMultiSelectToggled(isMultiSelect: Boolean) {
        this.isMultiSelect = isMultiSelect
    }
}
