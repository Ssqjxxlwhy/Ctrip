package com.example.Ctrip.home

import java.time.LocalDate

class FlightListTabPresenter(
    private val model: FlightListTabContract.Model
) : FlightListTabContract.Presenter {
    
    private var view: FlightListTabContract.View? = null
    private var currentData: FlightListData? = null
    private var activeFilters = mutableListOf<String>()
    private var currentSort = "sort_recommend"
    
    override fun attachView(view: FlightListTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadFlightListData(departureCity: String, arrivalCity: String, selectedDate: LocalDate) {
        view?.showLoading()
        
        try {
            val data = model.getFlightListData(departureCity, arrivalCity, selectedDate)
            if (data != null) {
                currentData = data
                view?.hideLoading()
                view?.showFlightListData(data)
            } else {
                view?.hideLoading()
                view?.showError("无法加载航班列表数据")
            }
        } catch (e: Exception) {
            view?.hideLoading()
            view?.showError("加载航班列表数据时出错: ${e.message}")
        }
    }
    
    override fun onFlightItemClicked(flightId: String) {
        try {
            model.saveFlightSelection(flightId)
            view?.onFlightSelected(flightId)
        } catch (e: Exception) {
            view?.showError("选择航班时出错: ${e.message}")
        }
    }
    
    override fun onDateOptionClicked(dateId: String) {
        try {
            model.saveDateSelection(dateId)
            
            currentData?.let { data ->
                val updatedDateOptions = data.dateOptions.map { option ->
                    option.copy(isSelected = option.id == dateId)
                }
                
                val selectedDate = updatedDateOptions.find { it.isSelected }?.date
                if (selectedDate != null) {
                    val updatedFlights = model.getFlightList(
                        data.routeInfo.departureCity,
                        data.routeInfo.arrivalCity,
                        selectedDate
                    )
                    
                    val updatedData = data.copy(
                        dateOptions = updatedDateOptions,
                        flightList = applyCurrentFiltersAndSort(updatedFlights)
                    )
                    
                    currentData = updatedData
                    view?.showFlightListData(updatedData)
                    view?.onDateChanged(dateId)
                }
            }
        } catch (e: Exception) {
            view?.showError("切换日期时出错: ${e.message}")
        }
    }
    
    override fun onFilterTagClicked(filterId: String) {
        try {
            if (activeFilters.contains(filterId)) {
                activeFilters.remove(filterId)
            } else {
                activeFilters.add(filterId)
            }
            
            model.saveFilterPreferences(activeFilters)
            
            currentData?.let { data ->
                val updatedFilterTags = data.filterTags.map { tag ->
                    tag.copy(isSelected = activeFilters.contains(tag.id))
                }
                
                val filteredFlights = model.applyFilters(data.flightList, activeFilters)
                val sortedFlights = model.sortFlights(filteredFlights, currentSort)
                
                val updatedData = data.copy(
                    filterTags = updatedFilterTags,
                    flightList = sortedFlights
                )
                
                currentData = updatedData
                view?.showFlightListData(updatedData)
                view?.onFilterChanged(filterId)
            }
        } catch (e: Exception) {
            view?.showError("应用筛选时出错: ${e.message}")
        }
    }
    
    override fun onSortOptionClicked(sortId: String) {
        try {
            currentSort = sortId
            model.saveSortPreference(sortId)
            
            currentData?.let { data ->
                val updatedSortOptions = data.sortOptions.map { option ->
                    option.copy(isSelected = option.id == sortId)
                }
                
                val sortedFlights = model.sortFlights(data.flightList, sortId)
                
                val updatedData = data.copy(
                    sortOptions = updatedSortOptions,
                    flightList = sortedFlights
                )
                
                currentData = updatedData
                view?.showFlightListData(updatedData)
                view?.onSortChanged(sortId)
            }
        } catch (e: Exception) {
            view?.showError("排序时出错: ${e.message}")
        }
    }
    
    override fun onBackClicked() {
        view?.onBack()
    }
    
    override fun onLowPriceAlertClicked() {
        view?.showLowPriceAlert()
    }
    
    override fun onMoreOptionsClicked() {
        view?.showMoreOptions()
    }
    
    override fun onCalendarClicked() {
        view?.showCalendar()
    }
    
    override fun onPromotionBannerClicked() {
        view?.showPromotionDetails()
    }
    
    override fun onMembershipInfoClicked() {
        view?.showMembershipUpgrade()
    }
    
    override fun refreshFlightList() {
        currentData?.let { data ->
            val selectedDate = data.dateOptions.find { it.isSelected }?.date ?: LocalDate.now().plusDays(1)
            loadFlightListData(data.routeInfo.departureCity, data.routeInfo.arrivalCity, selectedDate)
        }
    }
    
    private fun applyCurrentFiltersAndSort(flights: List<FlightItem>): List<FlightItem> {
        val filteredFlights = model.applyFilters(flights, activeFilters)
        return model.sortFlights(filteredFlights, currentSort)
    }
}