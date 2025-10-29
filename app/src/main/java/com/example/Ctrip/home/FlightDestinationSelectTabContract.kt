package com.example.Ctrip.home

import com.example.Ctrip.model.City
import com.example.Ctrip.model.LocationRegion

// Data classes for Destination Selection
data class DestinationSelectionData(
    val history: List<City>,
    val specialOffers: List<DestinationSpecialOffer>,
    val hotCities: List<City>,
    val reputationRankings: List<ReputationRanking>,
    val hotRegions: List<HotRegion>,
    val isLocationEnabled: Boolean = false
)

data class DestinationSpecialOffer(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: String, // "low_price", "visa_free", etc.
    val backgroundColor: String,
    val textColor: String = "#FFFFFF"
)

data class ReputationRanking(
    val id: String,
    val title: String, // "必打卡榜", "亲子榜", "赏秋榜"
    val icon: String,
    val cities: List<RankingCity>,
    val backgroundColor: String
)

data class RankingCity(
    val rank: Int,
    val city: City
)

data class HotRegion(
    val id: String,
    val name: String,
    val imageUrl: String,
    val cities: List<City>
)

// Contract interfaces
interface FlightDestinationSelectTabContract {
    
    interface View {
        fun showDestinationData(data: DestinationSelectionData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onDestinationSelected(city: City)
        fun onClose()
        fun onSearchRequested(query: String)
        fun onRegionChanged(region: LocationRegion)
        fun onMultiSelectToggled(isMultiSelect: Boolean)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadDestinationData()
        fun onDestinationClicked(city: City)
        fun onSpecialOfferClicked(offerId: String)
        fun onReputationRankingClicked(rankingId: String)
        fun onHotRegionClicked(regionId: String)
        fun onCloseClicked()
        fun onSearchClicked(query: String)
        fun onRegionTabClicked(region: LocationRegion)
        fun onMultiSelectTabClicked(isMultiSelect: Boolean)
        fun onCategoryClicked(category: String) // "历史", "热门", "主题", "地区"
    }
    
    interface Model {
        fun getDestinationSelectionData(): DestinationSelectionData?
        fun searchDestinations(query: String): List<City>
        fun getDestinationsByRegion(region: LocationRegion): List<City>
        fun getDestinationsByCategory(category: String): List<City>
        fun saveDestinationSelection(city: City)
        fun updateSearchHistory(city: City)
    }
}