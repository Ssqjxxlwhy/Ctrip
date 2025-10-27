package com.example.Ctrip.home

import java.time.LocalDate

class FlightDateSelectTabPresenter(
    private val model: FlightDateSelectTabContract.Model
) : FlightDateSelectTabContract.Presenter {
    
    private var view: FlightDateSelectTabContract.View? = null
    private var currentData: FlightDateSelectionData? = null
    
    override fun attachView(view: FlightDateSelectTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadFlightDateData() {
        view?.showLoading()
        
        try {
            val data = model.getFlightDateSelectionData()
            if (data != null) {
                currentData = data
                view?.hideLoading()
                view?.showFlightDateData(data)
            } else {
                view?.hideLoading()
                view?.showError("无法加载机票日期数据")
            }
        } catch (e: Exception) {
            view?.hideLoading()
            view?.showError("加载机票日期数据时出错: ${e.message}")
        }
    }
    
    override fun onDateClicked(date: LocalDate) {
        try {
            // Update the selected date in current data
            currentData?.let { data ->
                val updatedMonths = data.months.map { monthData ->
                    monthData.copy(
                        days = monthData.days.map { dayData ->
                            dayData.copy(isSelected = dayData.date == date)
                        }
                    )
                }
                
                val updatedData = data.copy(
                    months = updatedMonths,
                    selectedDate = date
                )
                
                currentData = updatedData
                view?.showFlightDateData(updatedData)
                view?.onDateSelected(date)
                
                // Save the selection
                model.saveDateSelection(date)
            }
        } catch (e: Exception) {
            view?.showError("选择日期时出错: ${e.message}")
        }
    }
    
    override fun onCloseClicked() {
        view?.onClose()
    }
    
    override fun onDirectFlightToggled(isDirectOnly: Boolean) {
        try {
            model.toggleDirectFlightFilter(isDirectOnly)
            
            // Update current data
            currentData?.let { data ->
                val updatedData = data.copy(isDirectFlightOnly = isDirectOnly)
                currentData = updatedData
                view?.updateDirectFlightToggle(isDirectOnly)
                view?.showFlightDateData(updatedData)
            }
        } catch (e: Exception) {
            view?.showError("切换直飞筛选时出错: ${e.message}")
        }
    }
    
    override fun onWeekendOfferClicked(offerId: String) {
        try {
            view?.navigateToWeekendOffers(offerId)
        } catch (e: Exception) {
            view?.showError("打开周末优惠时出错: ${e.message}")
        }
    }
    
    override fun onMonthChanged(year: Int, month: Int) {
        try {
            // Reload data if month is changed (for future enhancements)
            val prices = model.getFlightPricesForMonth(year, month)
            // Update the specific month data in currentData
            // For now, just reload all data
            loadFlightDateData()
        } catch (e: Exception) {
            view?.showError("切换月份时出错: ${e.message}")
        }
    }
}