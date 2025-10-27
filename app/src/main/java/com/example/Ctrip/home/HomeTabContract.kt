package com.example.Ctrip.home

interface HomeTabContract {
    
    interface View {
        fun showHomeData(data: HomeTabData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToHotel()
        fun navigateToFlight()
        fun navigateToTrain()
        fun navigateToGuideAttraction()
        fun navigateToTravelGroup()
        fun onQuickActionClick(actionId: String)
        fun onSearchClick(query: String)
        fun onPromotionClick(promotionId: String)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadHomeData()
        fun onQuickActionClicked(actionId: String)
        fun onSearchSubmitted(query: String)
        fun onPromotionClicked(promotionId: String)
    }
    
    interface Model {
        fun getHomeData(): HomeTabData?
        fun saveActionClick(actionId: String)
        fun saveSearchQuery(query: String)
    }
}