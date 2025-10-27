package com.example.Ctrip.home

import java.time.LocalDate

class HotelDetailTabPresenter(private val model: HotelDetailTabModel) : HotelDetailTabContract.Presenter {
    
    private var view: HotelDetailTabContract.View? = null
    private var currentData: HotelDetailData? = null
    
    override fun attachView(view: HotelDetailTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadHotelDetail(hotelId: String, searchParams: HotelListSearchParams) {
        view?.showLoading()
        
        val data = model.getHotelDetailData(hotelId, searchParams)
        if (data != null) {
            currentData = data
            view?.showHotelDetailData(data)
        } else {
            view?.showError("酒店详情加载失败")
        }
        
        view?.hideLoading()
    }
    
    override fun onImageTabClicked(tab: String) {
        currentData?.let { data ->
            val updatedData = data.copy(currentImageTab = tab)
            currentData = updatedData
            view?.onImageTabChanged(tab)
            view?.showHotelDetailData(updatedData)
        }
    }
    
    override fun onFavoriteClicked() {
        currentData?.let { data ->
            val isFavorite = model.toggleFavorite(data.hotel.id)
            view?.updateFavoriteStatus(isFavorite)
        }
    }
    
    override fun onShareClicked() {
        view?.showShareOptions()
        currentData?.let { data ->
            model.saveHotelDetailAction("share_hotel", mapOf(
                "hotel_id" to data.hotel.id,
                "hotel_name" to data.hotel.name
            ))
        }
    }
    
    override fun onCartClicked() {
        view?.showCartUpdated()
        currentData?.let { data ->
            model.saveHotelDetailAction("view_cart", mapOf(
                "hotel_id" to data.hotel.id
            ))
        }
    }
    
    override fun onMoreClicked() {
        view?.showMoreOptions()
    }
    
    override fun onDateClicked() {
        currentData?.let { data ->
            view?.onDateChanged(data.searchParams.checkInDate, data.searchParams.checkOutDate)
        }
    }
    
    override fun onGuestCountClicked() {
        currentData?.let { data ->
            view?.onGuestCountChanged(data.searchParams.roomCount, data.searchParams.guestCount)
        }
    }
    
    override fun onRoomFilterToggled(tagId: String) {
        currentData?.let { data ->
            val updatedTags = data.filterTags.map { tag ->
                if (tag.id == tagId) {
                    tag.copy(isSelected = !tag.isSelected)
                } else {
                    tag
                }
            }
            
            val filteredRoomTypes = model.filterRoomTypes(data.roomTypes, updatedTags)
            val updatedData = data.copy(
                filterTags = updatedTags,
                roomTypes = filteredRoomTypes
            )
            
            currentData = updatedData
            view?.showHotelDetailData(updatedData)
            
            model.saveHotelDetailAction("filter_rooms", mapOf(
                "tag_id" to tagId,
                "filtered_count" to filteredRoomTypes.size
            ))
        }
    }
    
    override fun onRoomTypeClicked(roomType: RoomType) {
        view?.navigateToRoomTypeDetails(roomType)
        model.saveHotelDetailAction("select_room_type", mapOf(
            "room_type_id" to roomType.id,
            "room_type_name" to roomType.name,
            "price" to roomType.price
        ))
    }
    
    override fun onContactHotelClicked() {
        view?.showContactHotel()
        currentData?.let { data ->
            model.saveHotelDetailAction("contact_hotel", mapOf(
                "hotel_id" to data.hotel.id,
                "hotel_name" to data.hotel.name
            ))
        }
    }
    
    override fun onPromotionClicked(promotion: PromotionItem) {
        model.saveHotelDetailAction("click_promotion", mapOf(
            "promotion_id" to promotion.id,
            "promotion_title" to promotion.title,
            "promotion_type" to promotion.type
        ))
    }
    
    override fun onLocationClicked() {
        currentData?.let { data ->
            model.saveHotelDetailAction("view_location", mapOf(
                "hotel_id" to data.hotel.id,
                "address" to data.locationInfo.address
            ))
        }
    }
}