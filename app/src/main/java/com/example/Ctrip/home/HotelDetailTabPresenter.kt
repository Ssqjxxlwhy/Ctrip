package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.example.Ctrip.utils.BookingHistoryManager
import com.google.gson.Gson
import java.time.LocalDate

class HotelDetailTabPresenter(private val model: HotelDetailTabModel, private val context: Context? = null) : HotelDetailTabContract.Presenter {

    private var view: HotelDetailTabContract.View? = null
    private var currentData: HotelDetailData? = null
    private val gson = Gson()
    private val sharedPreferences: SharedPreferences? by lazy {
        context?.getSharedPreferences("hotel_list_data", Context.MODE_PRIVATE)
    }
    
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

        // 记录预订信息（用于自动化测试验证）
        currentData?.let { data ->
            context?.let { ctx ->
                // 获取酒店在列表中的索引（如果有）
                val hotelIndex = model.getHotelIndex(data.hotel.id)
                // 获取房型在列表中的索引
                val roomIndex = data.roomTypes.indexOfFirst { it.id == roomType.id }

                // 判断是否选择了最便宜的房型
                val cheapestRoomPrice = data.roomTypes.minOfOrNull { it.price }
                val isCheapestRoom = (roomType.price == cheapestRoomPrice)

                // 从SharedPreferences读取酒店是否是列表中最便宜的
                val isCheapestHotel = try {
                    val hotelSelectionJson = sharedPreferences?.getString("current_hotel_selection", null)
                    if (hotelSelectionJson != null) {
                        val jsonObject = org.json.JSONObject(hotelSelectionJson)
                        jsonObject.optBoolean("is_cheapest_hotel", false)
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    false
                }

                // 只有当酒店是最便宜的，且房型也是最便宜的时，才标记为cheapest
                val selection = if (isCheapestHotel && isCheapestRoom) "cheapest" else null

                BookingHistoryManager.recordHotelBooking(
                    context = ctx,
                    city = data.hotel.city,
                    checkIn = data.searchParams.checkInDate.toString(),
                    checkOut = data.searchParams.checkOutDate.toString(),
                    hotelIndex = hotelIndex,
                    roomIndex = if (roomIndex >= 0) roomIndex else null,
                    selection = selection,
                    hotelName = data.hotel.name,
                    price = roomType.price.toDouble()
                )
            }
        }
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