package com.example.Ctrip.home

class RoomTypeDetailsTabPresenter(
    private val model: RoomTypeDetailsTabContract.Model
) : RoomTypeDetailsTabContract.Presenter {
    
    private var view: RoomTypeDetailsTabContract.View? = null
    private var currentData: RoomTypeDetailsData? = null
    
    override fun attachView(view: RoomTypeDetailsTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadRoomTypeDetails(roomType: RoomType, searchParams: HotelListSearchParams) {
        view?.showLoading()
        
        try {
            val data = model.getRoomTypeDetailsData(roomType, searchParams)
            if (data != null) {
                currentData = data
                view?.showRoomTypeDetailsData(data)
            } else {
                view?.showError("房型详情加载失败")
            }
        } catch (e: Exception) {
            view?.showError("房型详情加载失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onBackPressed() {
        model.saveRoomTypeDetailsAction("back_pressed", emptyMap())
    }
    
    override fun onChatClicked() {
        model.saveRoomTypeDetailsAction("chat_clicked", emptyMap())
        view?.onContactHotel()
    }
    
    override fun onImageClicked(index: Int) {
        currentData?.let { data ->
            if (index in 0 until data.imageGallery.size) {
                val updatedData = data.copy(currentImageIndex = index)
                currentData = updatedData
                view?.onImageChanged(index)
                model.saveRoomTypeDetailsAction("image_clicked", mapOf("index" to index))
            }
        }
    }
    
    override fun onViewAllFacilitiesClicked() {
        model.saveRoomTypeDetailsAction("view_all_facilities_clicked", emptyMap())
        view?.onFacilitiesExpanded()
    }
    
    override fun onGiftDetailsClicked() {
        model.saveRoomTypeDetailsAction("gift_details_clicked", emptyMap())
        view?.onGiftDetailsShown()
    }
    
    override fun onSpecialOfferClicked(offer: SpecialOffer) {
        model.saveRoomTypeDetailsAction("special_offer_clicked", mapOf(
            "offerId" to offer.id,
            "offerTitle" to offer.title
        ))
        view?.onSpecialOfferClicked(offer)
    }
    
    override fun onContactHotelClicked() {
        model.saveRoomTypeDetailsAction("contact_hotel_clicked", emptyMap())
        view?.onContactHotel()
    }
    
    override fun onAddToCartClicked() {
        currentData?.let { data ->
            model.addToCart(data.roomType.id, data.searchParams)
            model.saveRoomTypeDetailsAction("add_to_cart_clicked", mapOf(
                "roomTypeId" to data.roomType.id,
                "roomTypeName" to data.roomType.name
            ))
            view?.onAddToCart()
        }
    }
    
    override fun onBookingClicked() {
        currentData?.let { data ->
            model.saveRoomTypeDetailsAction("booking_clicked", mapOf(
                "roomTypeId" to data.roomType.id,
                "roomTypeName" to data.roomType.name,
                "price" to data.priceInfo.currentPrice
            ))
            view?.navigateToInfoConfirmation(data.roomType, data.searchParams)
        }
    }
}