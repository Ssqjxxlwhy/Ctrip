package com.example.Ctrip.home

// Data classes for Room and Guest Selection
data class RoomAndGuestSelectData(
    val roomCount: Int,
    val adultCount: Int,
    val childCount: Int
)

data class RoomAndGuestSelection(
    val roomCount: Int,
    val adultCount: Int,
    val childCount: Int
)

// Contract interfaces
interface RoomAndGuestSelectTabContract {
    
    interface View {
        fun showRoomAndGuestData(data: RoomAndGuestSelectData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onRoomAndGuestSelectionCompleted(selection: RoomAndGuestSelection)
        fun updateSelection(roomCount: Int, adultCount: Int, childCount: Int)
        fun showInvalidSelection(message: String)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadRoomAndGuestData()
        fun onRoomCountChanged(count: Int)
        fun onAdultCountChanged(count: Int)
        fun onChildCountChanged(count: Int)
        fun onCompleteClicked()
        fun isValidSelection(roomCount: Int, adultCount: Int, childCount: Int): Boolean
    }
    
    interface Model {
        fun getRoomAndGuestSelectData(): RoomAndGuestSelectData
        fun updateRoomCount(count: Int)
        fun updateAdultCount(count: Int)
        fun updateChildCount(count: Int)
        fun saveRoomAndGuestSelection(selection: RoomAndGuestSelection)
        fun resetSelection()
    }
}