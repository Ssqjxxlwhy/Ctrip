package com.example.Ctrip.trip

class TripTabPresenter(
    private val model: TripTabContract.Model
) : TripTabContract.Presenter {
    
    private var view: TripTabContract.View? = null
    
    override fun attachView(view: TripTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        view = null
    }
    
    override fun loadTripData() {
        view?.showLoading()
        try {
            val data = model.getTripData()
            view?.showTripData(data)
        } catch (e: Exception) {
            view?.showError("加载行程数据失败")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onAddTripClicked() {
        view?.navigateToAddTrip()
    }
    
    override fun onMoreOptionsClicked() {
        view?.navigateToMoreOptions()
    }
    
    override fun onStartPlanningClicked() {
        view?.navigateToRoutePlanning()
    }
    
    override fun onTravelMapClicked() {
        view?.navigateToTravelMap()
    }
    
    override fun onAttractionClicked(attractionId: String) {
        view?.navigateToAttractionDetail(attractionId)
    }
    
    override fun onCityMoreClicked(cityId: String) {
        view?.navigateToCityDetail(cityId)
    }
    
    override fun onContentClicked(contentId: String) {
        view?.navigateToContentDetail(contentId)
    }
}