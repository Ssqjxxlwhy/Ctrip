package com.example.Ctrip.home

// Data classes for City Selection
data class CitySelectData(
    val searchHint: String,
    val isLocationEnabled: Boolean,
    val historySearches: List<String>,
    val regionTabs: List<RegionTabCity>,
    val hotCities: List<City>,
    val alphabeticalCities: Map<String, List<City>>,
    val alphabetIndex: List<String>
)

data class RegionTabCity(
    val id: String,
    val title: String,
    val isSelected: Boolean
)

data class City(
    val id: String,
    val name: String,
    val pinyin: String,
    val isHot: Boolean
)

data class SearchResult(
    val cities: List<City>,
    val searchTerm: String
)

// Contract interfaces
interface CitySelectTabContract {
    
    interface View {
        fun showCityData(data: CitySelectData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showSearchResults(results: SearchResult)
        fun clearSearchResults()
        fun onCitySelected(city: City)
        fun showLocationPermissionDialog()
        fun updateLocationStatus(isEnabled: Boolean)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadCityData()
        fun onSearchTextChanged(query: String)
        fun onSearchSubmitted(query: String)
        fun onRegionTabSelected(regionId: String)
        fun onCityClicked(city: City)
        fun onHistoryCityClicked(cityName: String)
        fun onLocationButtonClicked()
        fun onClearHistoryClicked()
        fun onAlphabetIndexClicked(letter: String)
        fun onDestinationTabClicked()
        fun onExploreNearbyTabClicked()
    }
    
    interface Model {
        fun getCitySelectData(): CitySelectData?
        fun searchCities(query: String): SearchResult
        fun addToHistory(cityName: String)
        fun clearHistory()
        fun getHotCitiesByRegion(regionId: String): List<City>
        fun getCitiesByAlphabet(letter: String): List<City>
        fun getAllCities(): List<City>
        fun saveCitySelection(city: City)
    }
}