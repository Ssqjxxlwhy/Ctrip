package com.example.Ctrip.home

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import com.example.Ctrip.utils.DateUtils

class HotelDateSelectTabPresenter(private val model: HotelDateSelectTabModel) : HotelDateSelectTabContract.Presenter {
    
    private var view: HotelDateSelectTabContract.View? = null
    private var isSelectingCheckIn = true // true for check-in, false for check-out
    
    override fun attachView(view: HotelDateSelectTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadDateData() {
        view?.showLoading()
        
        val data = model.getDateSelectData()
        view?.showDateData(data)
        
        view?.hideLoading()
    }
    
    override fun onDateModeSelected(modeId: String) {
        model.updateDateMode(modeId)
        loadDateData()
    }
    
    override fun onDateClicked(date: LocalDate) {
        // Don't allow selection of past dates
        if (date.isBefore(DateUtils.getCurrentDate())) {
            view?.showInvalidDateSelection("不能选择过去的日期")
            return
        }
        
        val currentData = model.getDateSelectData()
        
        // New logic: Allow flexible date selection
        when {
            // If no dates are selected yet, or user clicks on check-in date, set as check-in
            currentData.checkInDate == currentData.checkOutDate.minusDays(1) && 
            currentData.checkInDate == DateUtils.getCurrentDate() -> {
                // First selection - set as check-in date
                model.updateCheckInDate(date)
                model.updateCheckOutDate(date.plusDays(1))
                isSelectingCheckIn = false
            }
            
            // If user clicks on current check-in date, allow changing it
            date == currentData.checkInDate -> {
                model.updateCheckInDate(date)
                // Ensure check-out is still valid
                if (currentData.checkOutDate <= date) {
                    model.updateCheckOutDate(date.plusDays(1))
                }
                isSelectingCheckIn = false
            }
            
            // If user clicks on current check-out date, allow changing it
            date == currentData.checkOutDate -> {
                if (date > currentData.checkInDate) {
                    model.updateCheckOutDate(date)
                    isSelectingCheckIn = true
                } else {
                    view?.showInvalidDateSelection("离店日期必须晚于入住日期")
                    return
                }
            }
            
            // If date is before current check-in, set as new check-in
            date < currentData.checkInDate -> {
                model.updateCheckInDate(date)
                // Keep existing check-out if it's still valid, otherwise set to check-in + 1
                if (currentData.checkOutDate <= date) {
                    model.updateCheckOutDate(date.plusDays(1))
                }
                isSelectingCheckIn = false
            }
            
            // If date is after current check-out, set as new check-out
            date > currentData.checkOutDate -> {
                model.updateCheckOutDate(date)
                isSelectingCheckIn = true
            }
            
            // If date is between check-in and check-out
            date > currentData.checkInDate && date < currentData.checkOutDate -> {
                if (isSelectingCheckIn) {
                    // Set as new check-in date
                    model.updateCheckInDate(date)
                    isSelectingCheckIn = false
                } else {
                    // Set as new check-out date
                    model.updateCheckOutDate(date)
                    isSelectingCheckIn = true
                }
            }
            
            // Default case: follow the original toggle logic
            else -> {
                if (isSelectingCheckIn) {
                    model.updateCheckInDate(date)
                    if (currentData.checkOutDate <= date) {
                        model.updateCheckOutDate(date.plusDays(1))
                    }
                    isSelectingCheckIn = false
                } else {
                    if (date <= currentData.checkInDate) {
                        view?.showInvalidDateSelection("离店日期必须晚于入住日期")
                        return
                    }
                    model.updateCheckOutDate(date)
                    isSelectingCheckIn = true
                }
            }
        }
        
        // Reload data to update UI
        val updatedData = model.getDateSelectData()
        view?.updateDateSelection(updatedData.checkInDate, updatedData.checkOutDate)
        view?.updateSelectionStatus(isSelectingCheckIn)
        loadDateData()
    }
    
    override fun onCompleteClicked() {
        val data = model.getDateSelectData()
        
        if (!isValidDateSelection(data.checkInDate, data.checkOutDate)) {
            view?.showInvalidDateSelection("请选择有效的入住和离店日期")
            return
        }
        
        val selection = DateSelection(
            checkInDate = data.checkInDate,
            checkOutDate = data.checkOutDate,
            nights = data.totalNights
        )
        
        model.saveDateSelection(selection)
        view?.onDateSelectionCompleted(selection)
    }
    
    override fun calculateNights(checkIn: LocalDate, checkOut: LocalDate): Int {
        return if (checkOut.isAfter(checkIn)) {
            ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
        } else {
            0
        }
    }
    
    override fun isValidDateSelection(checkIn: LocalDate, checkOut: LocalDate): Boolean {
        return checkIn >= DateUtils.getCurrentDate() && 
               checkOut.isAfter(checkIn) && 
               calculateNights(checkIn, checkOut) > 0
    }
    
    // Helper method to reset date selection state
    fun resetSelectionState() {
        isSelectingCheckIn = true
        model.resetSelection()
        view?.updateSelectionStatus(isSelectingCheckIn)
    }
}