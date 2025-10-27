package com.example.Ctrip.home

import com.example.Ctrip.model.*

class InfoConfirmationTabPresenter(
    private val model: InfoConfirmationTabContract.Model
) : InfoConfirmationTabContract.Presenter {
    
    private var view: InfoConfirmationTabContract.View? = null
    private var currentData: InfoConfirmationData? = null
    
    override fun attachView(view: InfoConfirmationTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadInfoConfirmationData(roomType: RoomType, searchParams: HotelListSearchParams) {
        view?.showLoading()
        
        try {
            val data = model.getInfoConfirmationData(roomType, searchParams)
            if (data != null) {
                currentData = data
                view?.showInfoConfirmationData(data)
            } else {
                view?.showError("无法加载订房信息")
            }
        } catch (e: Exception) {
            view?.showError("加载失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onBackPressed() {
        // Navigate back to room type details
        view?.onBackPressed()
    }
    
    override fun onRoomCountIncrease() {
        currentData?.let { data ->
            val newCount = data.roomCount + 1
            if (newCount <= 5) { // Maximum room limit
                val updatedData = data.copy(roomCount = newCount)
                currentData = updatedData
                view?.onRoomCountChanged(newCount)
                model.updateRoomCount(data.roomType.id, newCount)
            }
        }
    }
    
    override fun onRoomCountDecrease() {
        currentData?.let { data ->
            val newCount = data.roomCount - 1
            if (newCount >= 1) { // Minimum room limit
                val updatedData = data.copy(roomCount = newCount)
                currentData = updatedData
                view?.onRoomCountChanged(newCount)
                model.updateRoomCount(data.roomType.id, newCount)
            }
        }
    }
    
    override fun onGuestNameChanged(name: String) {
        currentData?.let { data ->
            val updatedData = data.copy(guestName = name)
            currentData = updatedData
            view?.onGuestNameChanged(name)
            model.updateGuestInfo(data.roomType.id, name, data.contactPhone)
        }
    }
    
    override fun onContactPhoneChanged(phone: String) {
        currentData?.let { data ->
            val updatedData = data.copy(contactPhone = phone)
            currentData = updatedData
            view?.onContactPhoneChanged(phone)
            model.updateGuestInfo(data.roomType.id, data.guestName, phone)
        }
    }
    
    override fun onPriceDetailsClicked() {
        view?.onPriceDetailsClicked()
    }
    
    override fun onRoomTypeDetailsClicked() {
        view?.onRoomTypeDetailsClicked()
    }
    
    override fun onPaymentClicked() {
        currentData?.let { data ->
            // Save booking information before proceeding to payment
            val bookingInfo = mapOf(
                "roomTypeId" to data.roomType.id,
                "hotelName" to data.hotelName,
                "roomCount" to data.roomCount,
                "guestName" to data.guestName,
                "contactPhone" to data.contactPhone,
                "finalPrice" to data.priceBreakdown.finalPrice,
                "checkInDate" to data.searchParams.checkInDate,
                "checkOutDate" to data.searchParams.checkOutDate
            )
            model.saveBookingInfo(bookingInfo)
            
            view?.navigateToPayment(data)
        }
    }
}