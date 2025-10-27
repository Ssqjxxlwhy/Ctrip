package com.example.Ctrip.home

class HomeTabPresenter(private val model: HomeTabContract.Model) : HomeTabContract.Presenter {
    
    private var view: HomeTabContract.View? = null
    
    override fun attachView(view: HomeTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadHomeData() {
        view?.showLoading()
        
        try {
            val homeData = model.getHomeData()
            if (homeData != null) {
                view?.showHomeData(homeData)
            } else {
                view?.showError("Failed to load home data")
            }
        } catch (e: Exception) {
            view?.showError("Error: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onQuickActionClicked(actionId: String) {
        model.saveActionClick(actionId)
        
        when (actionId) {
            "hotel" -> view?.navigateToHotel()
            "flight" -> view?.navigateToFlight()
            "train" -> view?.navigateToTrain()
            "guide_attraction" -> view?.navigateToGuideAttraction()
            "travel_group" -> view?.navigateToTravelGroup()
            else -> view?.onQuickActionClick(actionId)
        }
    }
    
    override fun onSearchSubmitted(query: String) {
        model.saveSearchQuery(query)
        view?.onSearchClick(query)
    }
    
    override fun onPromotionClicked(promotionId: String) {
        view?.onPromotionClick(promotionId)
    }
}