package com.example.Ctrip.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import java.time.format.DateTimeFormatter

@Composable
fun HotelListTabScreen(
    searchParams: HotelListSearchParams,
    onBackPressed: () -> Unit = {},
    onHotelSelected: (HotelItem) -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { HotelListTabModelImpl(context) }
    val presenter = remember { HotelListTabPresenter(model) }
    
    var hotelListData by remember { mutableStateOf<HotelListData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
    val view = object : HotelListTabContract.View {
        override fun showHotelListData(data: HotelListData) {
            hotelListData = data
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
        
        override fun showNoResults(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
        
        override fun updateSearchResults(hotels: List<HotelItem>) {
            hotelListData = hotelListData?.copy(hotels = hotels)
        }
        
        override fun onHotelSelected(hotel: HotelItem) {
            onHotelSelected(hotel)
        }
        
        override fun showMapView() {
            Toast.makeText(context, "地图功能", Toast.LENGTH_SHORT).show()
        }
        
        override fun showMoreOptions() {
            Toast.makeText(context, "更多选项", Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(searchParams) {
        presenter.attachView(view)
        presenter.loadHotelList(searchParams)
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
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Header Section
        HeaderSection(
            searchParams = searchParams,
            searchQuery = searchQuery,
            onSearchQueryChanged = { query ->
                searchQuery = query
                presenter.onSearchQueryChanged(query)
            },
            onBackPressed = onBackPressed,
            onMapClicked = { presenter.onMapButtonClicked() },
            onMoreClicked = { presenter.onMoreButtonClicked() }
        )
        
        hotelListData?.let { data ->
            // Sort Options Section
            SortOptionsSection(
                sortOptions = data.sortOptions,
                onSortOptionSelected = { sortId -> presenter.onSortOptionSelected(sortId) }
            )
            
            // Filter Tags Section
            FilterTagsSection(
                filterTags = data.filterTags,
                onTagToggled = { tagId -> presenter.onFilterTagToggled(tagId) }
            )
            
            // First Stay Benefit Section
            FirstStayBenefitSection(
                benefit = data.firstStayBenefit,
                onBenefitClicked = { presenter.onFirstStayBenefitClicked() }
            )
            
            // Hotel List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(data.hotels) { hotel ->
                    HotelItemCard(
                        hotel = hotel,
                        onHotelClicked = { presenter.onHotelClicked(hotel) }
                    )
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HeaderSection(
    searchParams: HotelListSearchParams,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onBackPressed: () -> Unit,
    onMapClicked: () -> Unit,
    onMoreClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Top row with back button and location info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Location and date info
                Text(
                    text = "${searchParams.city} ${searchParams.checkInDate.format(DateTimeFormatter.ofPattern("MM-dd"))} ${searchParams.roomCount}间 ${searchParams.checkOutDate.format(DateTimeFormatter.ofPattern("MM-dd"))} ${searchParams.guestCount}人",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                
                // Map and More buttons
                IconButton(onClick = onMapClicked) {
                    Icon(
                        Icons.Default.Place,
                        contentDescription = "地图",
                        tint = Color.Black
                    )
                }
                
                IconButton(onClick = onMoreClicked) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Search bar
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("位置/品牌/酒店") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
private fun SortOptionsSection(
    sortOptions: List<HotelSortOption>,
    onSortOptionSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sortOptions) { option ->
            Row(
                modifier = Modifier
                    .clickable { onSortOptionSelected(option.id) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.title,
                    fontSize = 14.sp,
                    color = if (option.isSelected) Color(0xFF4A90E2) else Color.Black,
                    fontWeight = if (option.isSelected) FontWeight.Medium else FontWeight.Normal
                )
                
                if (option.hasDropdown) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "下拉",
                        modifier = Modifier.size(16.dp),
                        tint = if (option.isSelected) Color(0xFF4A90E2) else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterTagsSection(
    filterTags: List<FilterTag>,
    onTagToggled: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filterTags) { tag ->
            Card(
                modifier = Modifier.clickable { onTagToggled(tag.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (tag.isSelected) Color(0xFF4A90E2) else Color(0xFFF5F5F5)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = tag.title,
                    modifier = Modifier.padding(12.dp, 6.dp),
                    fontSize = 12.sp,
                    color = if (tag.isSelected) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
private fun FirstStayBenefitSection(
    benefit: FirstStayBenefit,
    onBenefitClicked: () -> Unit
) {
    if (!benefit.isVisible) return
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onBenefitClicked() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = "信息",
                tint = Color(0xFFFF69B4),
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = benefit.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4444))
            ) {
                Text(
                    text = benefit.description,
                    modifier = Modifier.padding(6.dp, 2.dp),
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onBenefitClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF69B4)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("查看", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

@Composable
private fun HotelItemCard(
    hotel: HotelItem,
    onHotelClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHotelClicked() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            // Hotel image placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏨",
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Hotel details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Hotel name
                Text(
                    text = hotel.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rating and stats
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4A90E2))
                    ) {
                        Text(
                            text = hotel.rating.toString(),
                            modifier = Modifier.padding(4.dp, 2.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "超棒",
                        fontSize = 12.sp,
                        color = Color(0xFF4A90E2)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "${hotel.reviewCount}点评",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "${hotel.favoriteCount}收藏",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Location
                Text(
                    text = hotel.location,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Description
                Text(
                    text = hotel.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Amenities
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(hotel.amenities.take(4)) { amenity ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Text(
                                text = amenity,
                                modifier = Modifier.padding(6.dp, 2.dp),
                                fontSize = 10.sp,
                                color = Color(0xFF4A90E2)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Discount info
                hotel.discountInfo?.let { discount ->
                    Text(
                        text = discount,
                        fontSize = 11.sp,
                        color = Color(0xFFFF4444)
                    )
                }
            }
            
            // Price section
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Original price
                hotel.originalPrice?.let { originalPrice ->
                    Text(
                        text = "¥$originalPrice",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
                
                // Current price
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "¥",
                        fontSize = 14.sp,
                        color = Color(0xFFFF4444)
                    )
                    Text(
                        text = hotel.price.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4444)
                    )
                    Text(
                        text = "起",
                        fontSize = 12.sp,
                        color = Color(0xFFFF4444)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Benefit info
                hotel.benefitInfo?.let { benefit ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = benefit,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        if (hotel.promotionCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = "详情",
                                modifier = Modifier.size(12.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}