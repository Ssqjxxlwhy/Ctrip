package com.example.Ctrip.home

import com.example.Ctrip.model.City
import com.example.Ctrip.model.CitySelectionData
import com.example.Ctrip.model.LocationRegion

class TrainDestinationSelectTabPresenter(private val model: TrainDestinationSelectTabContract.Model) : TrainDestinationSelectTabContract.Presenter {

    private var view: TrainDestinationSelectTabContract.View? = null
    private var currentCityData: CitySelectionData? = null
    private var currentRegion: LocationRegion = LocationRegion.DOMESTIC
    private var isMultiSelect: Boolean = false
    private var searchQuery: String = ""

    override fun attachView(view: TrainDestinationSelectTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadCityData() {
        view?.showLoading()

        try {
            val cityData = model.getCitySelectionData()
            if (cityData != null) {
                currentCityData = cityData
                view?.showCitySelectionData(cityData)
            } else {
                view?.showError("加载城市数据失败")
            }
        } catch (e: Exception) {
            view?.showError("加载城市数据失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }

    override fun onCityClicked(city: City) {
        model.updateSearchHistory(city)
        view?.onCitySelected(city)
    }

    override fun onCloseClicked() {
        view?.onClose()
    }

    override fun onSearchClicked(query: String) {
        searchQuery = query
        if (query.isNotBlank()) {
            try {
                val searchResults = model.searchCities(query)
                if (searchResults.isNotEmpty()) {
                    // Create search result data structure
                    val searchCityData = CitySelectionData(
                        history = searchResults,
                        hotCities = emptyList(),
                        domesticCities = mapOf("搜索结果" to searchResults),
                        internationalCities = emptyMap(),
                        isLocationEnabled = currentCityData?.isLocationEnabled ?: false
                    )
                    view?.showCitySelectionData(searchCityData)
                } else {
                    view?.showError("未找到相关城市")
                }
            } catch (e: Exception) {
                view?.showError("搜索失败: ${e.message}")
            }
        } else {
            // Return to normal view
            loadCityData()
        }
    }

    override fun onRegionTabClicked(region: LocationRegion) {
        currentRegion = region
        view?.onRegionChanged(region)

        try {
            val citiesByRegion = model.getCitiesByRegion(region)
            val cityData = CitySelectionData(
                history = currentCityData?.history ?: emptyList(),
                hotCities = if (region == LocationRegion.DOMESTIC) {
                    currentCityData?.hotCities ?: emptyList()
                } else {
                    emptyList()
                },
                domesticCities = if (region == LocationRegion.DOMESTIC) citiesByRegion else emptyMap(),
                internationalCities = if (region == LocationRegion.INTERNATIONAL) citiesByRegion else emptyMap(),
                isLocationEnabled = currentCityData?.isLocationEnabled ?: false
            )
            currentCityData = cityData
            view?.showCitySelectionData(cityData)
        } catch (e: Exception) {
            view?.showError("切换区域失败: ${e.message}")
        }
    }

    override fun onMultiSelectTabClicked(isMultiSelect: Boolean) {
        this.isMultiSelect = isMultiSelect
        view?.onMultiSelectToggled(isMultiSelect)
    }

    override fun onLocationEnabledChanged(enabled: Boolean) {
        view?.showLoading()

        try {
            val updatedData = currentCityData?.copy(isLocationEnabled = enabled)
            if (updatedData != null) {
                currentCityData = updatedData
                view?.showCitySelectionData(updatedData)
            }
        } catch (e: Exception) {
            view?.showError("更新定位状态失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
}
