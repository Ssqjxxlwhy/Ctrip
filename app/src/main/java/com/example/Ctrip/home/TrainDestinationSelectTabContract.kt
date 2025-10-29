package com.example.Ctrip.home

import com.example.Ctrip.model.City
import com.example.Ctrip.model.CitySelectionData
import com.example.Ctrip.model.LocationRegion

interface TrainDestinationSelectTabContract {

    interface View {
        fun showCitySelectionData(data: CitySelectionData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onCitySelected(city: City)
        fun onClose()
        fun onRegionChanged(region: LocationRegion)
        fun onMultiSelectToggled(enabled: Boolean)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadCityData()
        fun onCityClicked(city: City)
        fun onCloseClicked()
        fun onSearchClicked(query: String)
        fun onRegionTabClicked(region: LocationRegion)
        fun onMultiSelectTabClicked(isMultiSelect: Boolean)
        fun onLocationEnabledChanged(enabled: Boolean)
    }

    interface Model {
        fun getCitySelectionData(): CitySelectionData?
        fun searchCities(query: String): List<City>
        fun updateSearchHistory(city: City)
        fun getCitiesByRegion(region: LocationRegion): Map<String, List<City>>
    }
}
