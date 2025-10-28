package com.example.Ctrip.home

import android.widget.Toast

class CabinTypeSelectTabPresenter(
    private val model: CabinTypeSelectTabContract.Model
) : CabinTypeSelectTabContract.Presenter {
    
    private var view: CabinTypeSelectTabContract.View? = null
    
    override fun attachView(view: CabinTypeSelectTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadCabinTypeData(flightId: String) {
        view?.showLoading()
        
        try {
            val data = model.getCabinTypeData(flightId)
            if (data != null) {
                view?.showCabinTypeData(data)
            } else {
                view?.showError("无法加载舱型信息")
            }
        } catch (e: Exception) {
            view?.showError("加载失败：${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onCabinTypeSelected(cabinTypeId: String) {
        model.saveCabinTypeSelection(cabinTypeId)
        // 重新加载对应舱型的票价选项
        loadCabinTypeData("current_flight")
    }
    
    override fun onTicketOptionSelected(ticketOptionId: String) {
        model.saveTicketOptionSelection(ticketOptionId)
        // 可以在这里添加选择反馈或导航逻辑
    }
    
    override fun onBookTicket(ticketOptionId: String) {
        model.saveBookingAction(ticketOptionId)
        view?.navigateToOrderForm(ticketOptionId)
    }
    
    override fun onBackClicked() {
        view?.onBack()
    }
    
    override fun onTravelTipClicked() {
        view?.showTravelTipDetails()
    }
    
    override fun onMembershipBenefitClicked() {
        view?.showMembershipUpgrade()
    }
    
    override fun onInsuranceClicked() {
        view?.showInsuranceDetails()
    }
    
    override fun onReminderClicked() {
        view?.showReminderSettings()
    }
    
    override fun onMoreOptionsClicked() {
        view?.showMoreOptions()
    }
    
    override fun onFavoriteClicked() {
        val newState = model.toggleFlightFavorite("current_flight")
        view?.toggleFavorite()
    }
}