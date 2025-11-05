package com.example.Ctrip.home

class HotelListTabPresenter(private val model: HotelListTabModel) : HotelListTabContract.Presenter {
    
    private var view: HotelListTabContract.View? = null
    private var currentData: HotelListData? = null
    private var currentSortId: String = "welcome"
    
    override fun attachView(view: HotelListTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadHotelList(searchParams: HotelListSearchParams) {
        view?.showLoading()
        
        val data = model.getHotelListData(searchParams)
        if (data != null) {
            currentData = data
            
            if (data.hotels.isEmpty()) {
                view?.showNoResults("抱歉，没有找到符合条件的酒店")
            } else {
                view?.showHotelListData(data)
            }
        } else {
            view?.showError("加载酒店列表失败")
        }
        
        view?.hideLoading()
    }
    
    override fun onSortOptionSelected(sortId: String) {
        currentSortId = sortId
        applyFiltersAndSort()
    }
    
    override fun onFilterTagToggled(tagId: String) {
        currentData?.let { data ->
            val updatedTags = data.filterTags.map { tag ->
                if (tag.id == tagId) {
                    tag.copy(isSelected = !tag.isSelected)
                } else {
                    tag
                }
            }
            
            currentData = data.copy(filterTags = updatedTags)
            applyFiltersAndSort()
        }
    }
    
    override fun onSearchQueryChanged(query: String) {
        currentData?.let { data ->
            val updatedParams = data.searchParams.copy(searchQuery = query)
            val searchResults = model.searchHotels(updatedParams)
            
            // 应用当前的筛选和排序
            val filteredResults = model.filterHotels(searchResults, data.filterTags)
            val sortedResults = model.sortHotels(filteredResults, currentSortId)
            
            view?.updateSearchResults(sortedResults)
        }
    }
    
    override fun onHotelClicked(hotel: HotelItem) {
        // 判断该酒店是否是当前列表中价格最低的
        val isCheapest = currentData?.hotels?.let { hotels ->
            val lowestPrice = hotels.minOfOrNull { it.price }
            hotel.price == lowestPrice
        } ?: false

        model.saveHotelSelection(hotel, isCheapest)
        view?.onHotelSelected(hotel)
    }
    
    override fun onMapButtonClicked() {
        view?.showMapView()
    }
    
    override fun onMoreButtonClicked() {
        view?.showMoreOptions()
    }
    
    override fun onFirstStayBenefitClicked() {
        // 处理首住好礼点击事件
        currentData?.let { data ->
            // 筛选出有首住优惠的酒店
            val benefitHotels = data.hotels.filter { it.benefitInfo != null }
            if (benefitHotels.isNotEmpty()) {
                view?.updateSearchResults(benefitHotels)
            }
        }
    }
    
    override fun applyFiltersAndSort() {
        currentData?.let { data ->
            // 应用筛选
            val filteredHotels = model.filterHotels(data.hotels, data.filterTags)
            
            // 应用排序
            val sortedHotels = model.sortHotels(filteredHotels, currentSortId)
            
            // 更新排序选项状态
            val updatedSortOptions = data.sortOptions.map { option ->
                option.copy(isSelected = option.id == currentSortId)
            }
            
            val updatedData = data.copy(
                sortOptions = updatedSortOptions,
                hotels = sortedHotels
            )
            
            currentData = updatedData
            
            if (sortedHotels.isEmpty()) {
                view?.showNoResults("没有符合筛选条件的酒店")
            } else {
                view?.showHotelListData(updatedData)
            }
        }
    }
    
    // 重置筛选条件
    fun resetFilters() {
        currentData?.let { data ->
            val resetTags = data.filterTags.map { it.copy(isSelected = false) }
            currentData = data.copy(filterTags = resetTags)
            currentSortId = "welcome"
            applyFiltersAndSort()
        }
    }
    
    // 获取当前筛选状态
    fun getActiveFiltersCount(): Int {
        return currentData?.filterTags?.count { it.isSelected } ?: 0
    }
}