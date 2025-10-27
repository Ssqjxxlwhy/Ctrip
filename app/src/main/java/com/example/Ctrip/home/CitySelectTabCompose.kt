package com.example.Ctrip.home

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CitySelectTabScreen(
    onCitySelected: (City) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { CitySelectTabModelImpl(context) }
    val presenter = remember { CitySelectTabPresenter(model) }
    
    var cityData by remember { mutableStateOf<CitySelectData?>(null) }
    var searchResults by remember { mutableStateOf<SearchResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDestinationTab by remember { mutableStateOf(true) }
    
    val view = object : CitySelectTabContract.View {
        override fun showCityData(data: CitySelectData) {
            cityData = data
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
        
        override fun showSearchResults(results: SearchResult) {
            searchResults = results
        }
        
        override fun clearSearchResults() {
            searchResults = null
        }
        
        override fun onCitySelected(city: City) {
            onCitySelected(city)
            onBackPressed()
        }
        
        override fun showLocationPermissionDialog() {
            Toast.makeText(context, "需要开启定位权限", Toast.LENGTH_SHORT).show()
        }
        
        override fun updateLocationStatus(isEnabled: Boolean) {
            cityData?.let { data ->
                cityData = data.copy(isLocationEnabled = isEnabled)
            }
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(view)
        presenter.loadCityData()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with tabs
        HeaderSection(
            selectedDestinationTab = selectedDestinationTab,
            onDestinationTabClicked = { 
                selectedDestinationTab = true
                presenter.onDestinationTabClicked() 
            },
            onExploreNearbyTabClicked = { 
                selectedDestinationTab = false
                presenter.onExploreNearbyTabClicked() 
            },
            onBackPressed = onBackPressed
        )
        
        // Search bar
        SearchSection(
            searchQuery = searchQuery,
            onSearchQueryChanged = { query ->
                searchQuery = query
                presenter.onSearchTextChanged(query)
            },
            onSearchSubmitted = { presenter.onSearchSubmitted(searchQuery) },
            searchHint = cityData?.searchHint ?: "全球城市/区域/位置/酒店"
        )
        
        // Location status
        LocationStatusSection(
            isLocationEnabled = cityData?.isLocationEnabled ?: false,
            onLocationClicked = { presenter.onLocationButtonClicked() }
        )
        
        // Main content
        Box(modifier = Modifier.weight(1f)) {
            if (searchResults != null) {
                // Show search results
                SearchResultsSection(
                    searchResults = searchResults!!,
                    onCityClicked = { city -> presenter.onCityClicked(city) }
                )
            } else {
                // Show normal city selection content
                CitySelectionContent(
                    cityData = cityData,
                    presenter = presenter
                )
            }
            
            // Alphabet index on the right side
            if (searchResults == null) {
                AlphabetIndexSection(
                    alphabetIndex = cityData?.alphabetIndex ?: emptyList(),
                    onLetterClicked = { letter -> presenter.onAlphabetIndexClicked(letter) },
                    modifier = Modifier.align(Alignment.CenterEnd)
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

@Composable
private fun HeaderSection(
    selectedDestinationTab: Boolean,
    onDestinationTabClicked: () -> Unit,
    onExploreNearbyTabClicked: () -> Unit,
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.ArrowBack,
            contentDescription = "返回",
            modifier = Modifier
                .clickable { onBackPressed() }
                .size(24.dp),
            tint = Color.Black
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Destination tab
        Column(
            modifier = Modifier.clickable { onDestinationTabClicked() },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "选择目的地",
                fontSize = 18.sp,
                fontWeight = if (selectedDestinationTab) FontWeight.Medium else FontWeight.Normal,
                color = if (selectedDestinationTab) Color.Black else Color.Gray
            )
            if (selectedDestinationTab) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(3.dp)
                        .background(Color(0xFF4A90E2), RoundedCornerShape(2.dp))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(32.dp))
        
        // Explore nearby tab
        Row(
            modifier = Modifier.clickable { onExploreNearbyTabClicked() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "探索周边",
                fontSize = 18.sp,
                fontWeight = if (!selectedDestinationTab) FontWeight.Medium else FontWeight.Normal,
                color = if (!selectedDestinationTab) Color.Black else Color.Gray
            )
            if (!selectedDestinationTab) {
                Spacer(modifier = Modifier.width(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90E2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "新玩法",
                        modifier = Modifier.padding(4.dp, 2.dp),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchSubmitted: () -> Unit,
    searchHint: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = "搜索",
            modifier = Modifier.size(20.dp),
            tint = Color.Gray
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.weight(1f),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = searchHint,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        )
        
        Text(
            text = "📷",
            fontSize = 16.sp
        )
    }
}

@Composable
private fun LocationStatusSection(
    isLocationEnabled: Boolean,
    onLocationClicked: () -> Unit
) {
    if (!isLocationEnabled) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLocationClicked() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "定位信息",
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF4A90E2)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "定位未开启",
                fontSize = 14.sp,
                color = Color(0xFF4A90E2)
            )
        }
    }
}

@Composable
private fun CitySelectionContent(
    cityData: CitySelectData?,
    presenter: CitySelectTabPresenter
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // History search section
        item {
            if (!cityData?.historySearches.isNullOrEmpty()) {
                HistorySearchSection(
                    historySearches = cityData!!.historySearches,
                    onHistoryCityClicked = { cityName -> presenter.onHistoryCityClicked(cityName) },
                    onClearHistoryClicked = { presenter.onClearHistoryClicked() }
                )
            }
        }
        
        // Region tabs
        item {
            if (!cityData?.regionTabs.isNullOrEmpty()) {
                RegionTabsSection(
                    regionTabs = cityData!!.regionTabs,
                    onRegionTabSelected = { regionId -> presenter.onRegionTabSelected(regionId) }
                )
            }
        }
        
        // Hot cities
        item {
            if (!cityData?.hotCities.isNullOrEmpty()) {
                HotCitiesSection(
                    hotCities = cityData!!.hotCities,
                    onCityClicked = { city -> presenter.onCityClicked(city) }
                )
            }
        }
        
        // Alphabetical cities
        if (!cityData?.alphabeticalCities.isNullOrEmpty()) {
            cityData!!.alphabetIndex.forEach { letter ->
                val cities = cityData.alphabeticalCities[letter]
                if (!cities.isNullOrEmpty()) {
                    item {
                        AlphabeticalCitiesSection(
                            letter = letter,
                            cities = cities,
                            onCityClicked = { city -> presenter.onCityClicked(city) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorySearchSection(
    historySearches: List<String>,
    onHistoryCityClicked: (String) -> Unit,
    onClearHistoryClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "历史搜索",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Icon(
                Icons.Default.Delete,
                contentDescription = "清除历史",
                modifier = Modifier
                    .clickable { onClearHistoryClicked() }
                    .size(20.dp),
                tint = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historySearches) { cityName ->
                Card(
                    modifier = Modifier.clickable { onHistoryCityClicked(cityName) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = cityName,
                        modifier = Modifier.padding(12.dp, 8.dp),
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionTabsSection(
    regionTabs: List<RegionTabCity>,
    onRegionTabSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            regionTabs.forEach { tab ->
                Column(
                    modifier = Modifier
                        .clickable { onRegionTabSelected(tab.id) }
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab.title,
                        fontSize = 14.sp,
                        fontWeight = if (tab.isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = if (tab.isSelected) Color(0xFF4A90E2) else Color.Black
                    )
                    if (tab.isSelected) {
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
        }
    }
}

@Composable
private fun HotCitiesSection(
    hotCities: List<City>,
    onCityClicked: (City) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "国内热门城市",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "热门",
                fontSize = 12.sp,
                color = Color(0xFF4A90E2)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(200.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(hotCities.take(24)) { city ->
                Text(
                    text = city.name,
                    modifier = Modifier
                        .clickable { onCityClicked(city) }
                        .padding(8.dp),
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun AlphabeticalCitiesSection(
    letter: String,
    cities: List<City>,
    onCityClicked: (City) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = letter,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        cities.forEach { city ->
            Text(
                text = city.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCityClicked(city) }
                    .padding(vertical = 12.dp),
                fontSize = 16.sp,
                color = Color.Black
            )
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
        }
    }
}

@Composable
private fun SearchResultsSection(
    searchResults: SearchResult,
    onCityClicked: (City) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(searchResults.cities) { city ->
            Text(
                text = city.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCityClicked(city) }
                    .padding(vertical = 16.dp),
                fontSize = 16.sp,
                color = Color.Black
            )
            Divider(color = Color(0xFFE0E0E0), thickness = 0.5.dp)
        }
    }
}

@Composable
private fun AlphabetIndexSection(
    alphabetIndex: List<String>,
    onLetterClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(end = 8.dp)
            .width(20.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(alphabetIndex) { letter ->
            Text(
                text = letter,
                modifier = Modifier
                    .clickable { onLetterClicked(letter) }
                    .padding(2.dp),
                fontSize = 12.sp,
                color = Color(0xFF4A90E2),
                textAlign = TextAlign.Center
            )
        }
    }
}