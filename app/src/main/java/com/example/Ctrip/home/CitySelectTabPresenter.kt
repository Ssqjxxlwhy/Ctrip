package com.example.Ctrip.home

class CitySelectTabPresenter(private val model: CitySelectTabModel) : CitySelectTabContract.Presenter {
    
    private var view: CitySelectTabContract.View? = null
    private var currentRegionId = "domestic"
    
    override fun attachView(view: CitySelectTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadCityData() {
        view?.showLoading()
        
        val data = model.getCitySelectData()
        if (data != null) {
            view?.showCityData(data)
        } else {
            view?.showError("加载城市数据失败")
        }
        
        view?.hideLoading()
    }
    
    override fun onSearchTextChanged(query: String) {
        if (query.isBlank()) {
            view?.clearSearchResults()
            return
        }
        
        val results = model.searchCities(query)
        view?.showSearchResults(results)
    }
    
    override fun onSearchSubmitted(query: String) {
        if (query.isNotBlank()) {
            val results = model.searchCities(query)
            if (results.cities.isNotEmpty()) {
                // If there's an exact match, select it automatically
                val exactMatch = results.cities.find { 
                    it.name.equals(query, ignoreCase = true) 
                }
                if (exactMatch != null) {
                    onCityClicked(exactMatch)
                } else {
                    view?.showSearchResults(results)
                }
            }
        }
    }
    
    override fun onRegionTabSelected(regionId: String) {
        currentRegionId = regionId
        
        // Reload hot cities for the selected region
        val hotCities = model.getHotCitiesByRegion(regionId)
        
        // Get current data and update with new hot cities
        val currentData = model.getCitySelectData()
        currentData?.let { data ->
            val updatedRegionTabs = data.regionTabs.map { tab ->
                tab.copy(isSelected = tab.id == regionId)
            }
            
            val updatedData = data.copy(
                regionTabs = updatedRegionTabs,
                hotCities = hotCities
            )
            
            view?.showCityData(updatedData)
        }
    }
    
    override fun onCityClicked(city: City) {
        model.saveCitySelection(city)
        view?.onCitySelected(city)
    }
    
    override fun onHistoryCityClicked(cityName: String) {
        val allCities = model.getAllCities()
        val city = allCities.find { it.name == cityName }
        
        if (city != null) {
            onCityClicked(city)
        } else {
            // Create a temporary city object for the history search
            val tempCity = City(
                id = cityName.lowercase(),
                name = cityName,
                pinyin = cityName,
                isHot = false
            )
            onCityClicked(tempCity)
        }
    }
    
    override fun onLocationButtonClicked() {
        view?.showLocationPermissionDialog()
    }
    
    override fun onClearHistoryClicked() {
        model.clearHistory()
        loadCityData() // Reload to update the UI
    }
    
    override fun onAlphabetIndexClicked(letter: String) {
        // This would typically scroll to the section with that letter
        // For now, we'll just load cities starting with that letter
        val cities = model.getCitiesByAlphabet(letter)
        if (cities.isNotEmpty()) {
            // In a real implementation, you would scroll to that section
            // For now, we can show a toast or update the view in some way
        }
    }
    
    override fun onDestinationTabClicked() {
        // Already on destination tab, so just reload data
        loadCityData()
    }
    
    override fun onExploreNearbyTabClicked() {
        // Switch to explore nearby mode
        // This would typically change the data source or filters
        // For now, we'll just show a different set of data
        view?.showError("探索周边功能暂未实现")
    }
}