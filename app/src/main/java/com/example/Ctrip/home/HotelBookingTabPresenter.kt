package com.example.Ctrip.home

class HotelBookingTabPresenter(private val model: HotelBookingTabModel) : HotelBookingTabContract.Presenter {
    
    private var view: HotelBookingTabContract.View? = null
    
    override fun attachView(view: HotelBookingTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadHotelData() {
        view?.showLoading()
        
        val data = model.getHotelBookingData()
        if (data != null) {
            view?.showHotelData(data)
        } else {
            view?.showError("加载数据失败")
        }
        
        view?.hideLoading()
    }
    
    override fun onCategorySelected(categoryId: String) {
        // Update category selection and reload data
        loadHotelData()
    }
    
    override fun onCityClicked() {
        val data = model.getHotelBookingData()
        data?.let {
            view?.showCitySelector(it.searchParams.city)
        }
    }
    
    override fun onDateClicked() {
        val data = model.getHotelBookingData()
        data?.let {
            view?.showDatePicker(it.searchParams.checkInDate)
        }
    }
    
    override fun onRoomGuestClicked() {
        val data = model.getHotelBookingData()
        data?.let {
            view?.showRoomGuestPicker(it.searchParams)
        }
    }
    
    override fun onSearchClicked() {
        val data = model.getHotelBookingData()
        data?.let {
            model.saveSearchAction(it.searchParams)
            view?.navigateToHotelSearch(it.searchParams)
        }
    }
    
    override fun onImageSearchClicked() {
        view?.showImageSearch()
    }
    
    override fun onVoiceSearchClicked() {
        view?.showVoiceSearch()
    }
    
    override fun onQuickTagClicked(tag: String) {
        // Handle quick tag selection
        val data = model.getHotelBookingData()
        data?.let { currentData ->
            val updatedParams = currentData.searchParams.copy(
                priceRange = if (tag.contains("折") || tag.contains("积分")) tag else currentData.searchParams.priceRange
            )
            model.updateSearchParams(updatedParams)
            loadHotelData()
        }
    }
    
    override fun onBenefitSectionClicked(section: BenefitSection) {
        // Handle benefit section click - could filter hotels or show details
    }
    
    override fun onBottomSectionClicked(sectionId: String) {
        // Handle bottom section navigation
        when (sectionId) {
            "reputation" -> {
                // Navigate to reputation list
            }
            "special" -> {
                // Navigate to special packages
            }
            "lowPrice" -> {
                // Navigate to low price hotels
            }
        }
    }
    
    override fun updateSearchParams(params: HotelSearchParams) {
        model.updateSearchParams(params)
        loadHotelData()
    }
}