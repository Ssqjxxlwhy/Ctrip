package com.example.Ctrip.home

class RoomAndGuestSelectTabPresenter(private val model: RoomAndGuestSelectTabModel) : RoomAndGuestSelectTabContract.Presenter {
    
    private var view: RoomAndGuestSelectTabContract.View? = null
    
    override fun attachView(view: RoomAndGuestSelectTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadRoomAndGuestData() {
        view?.showLoading()
        
        val data = model.getRoomAndGuestSelectData()
        view?.showRoomAndGuestData(data)
        
        view?.hideLoading()
    }
    
    override fun onRoomCountChanged(count: Int) {
        if (count < 1) {
            view?.showInvalidSelection("房间数量不能少于1间")
            return
        }
        if (count > 10) {
            view?.showInvalidSelection("房间数量不能超过10间")
            return
        }
        
        model.updateRoomCount(count)
        val data = model.getRoomAndGuestSelectData()
        view?.updateSelection(data.roomCount, data.adultCount, data.childCount)
        loadRoomAndGuestData()
    }
    
    override fun onAdultCountChanged(count: Int) {
        if (count < 1) {
            view?.showInvalidSelection("成人数量不能少于1人")
            return
        }
        if (count > 20) {
            view?.showInvalidSelection("成人数量不能超过20人")
            return
        }
        
        model.updateAdultCount(count)
        val data = model.getRoomAndGuestSelectData()
        view?.updateSelection(data.roomCount, data.adultCount, data.childCount)
        loadRoomAndGuestData()
    }
    
    override fun onChildCountChanged(count: Int) {
        if (count < 0) {
            view?.showInvalidSelection("儿童数量不能少于0人")
            return
        }
        if (count > 10) {
            view?.showInvalidSelection("儿童数量不能超过10人")
            return
        }
        
        model.updateChildCount(count)
        val data = model.getRoomAndGuestSelectData()
        view?.updateSelection(data.roomCount, data.adultCount, data.childCount)
        loadRoomAndGuestData()
    }
    
    override fun onCompleteClicked() {
        val data = model.getRoomAndGuestSelectData()
        
        if (!isValidSelection(data.roomCount, data.adultCount, data.childCount)) {
            view?.showInvalidSelection("请选择有效的房间和入住人数")
            return
        }
        
        val selection = RoomAndGuestSelection(
            roomCount = data.roomCount,
            adultCount = data.adultCount,
            childCount = data.childCount
        )
        
        model.saveRoomAndGuestSelection(selection)
        view?.onRoomAndGuestSelectionCompleted(selection)
    }
    
    override fun isValidSelection(roomCount: Int, adultCount: Int, childCount: Int): Boolean {
        return roomCount >= 1 && 
               adultCount >= 1 && 
               childCount >= 0
    }
    
    // Helper method to reset selection state
    fun resetSelectionState() {
        model.resetSelection()
    }
}