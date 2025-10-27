package com.example.Ctrip.trip

import com.example.Ctrip.model.TripData

interface TripTabContract {
    
    interface View {
        fun showTripData(data: TripData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToAddTrip()
        fun navigateToMoreOptions()
        fun navigateToRoutePlanning()
        fun navigateToTravelMap()
        fun navigateToAttractionDetail(attractionId: String)
        fun navigateToCityDetail(cityId: String)
        fun navigateToContentDetail(contentId: String)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadTripData()
        fun onAddTripClicked()
        fun onMoreOptionsClicked()
        fun onStartPlanningClicked()
        fun onTravelMapClicked()
        fun onAttractionClicked(attractionId: String)
        fun onCityMoreClicked(cityId: String)
        fun onContentClicked(contentId: String)
    }
    
    interface Model {
        fun getTripData(): TripData
    }
}