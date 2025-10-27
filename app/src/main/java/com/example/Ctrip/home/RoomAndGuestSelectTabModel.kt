package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

interface RoomAndGuestSelectTabModel : RoomAndGuestSelectTabContract.Model

class RoomAndGuestSelectTabModelImpl(private val context: Context) : RoomAndGuestSelectTabModel {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("room_guest_select_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private var currentRoomCount = 1
    private var currentAdultCount = 1
    private var currentChildCount = 0
    
    override fun getRoomAndGuestSelectData(): RoomAndGuestSelectData {
        return RoomAndGuestSelectData(
            roomCount = currentRoomCount,
            adultCount = currentAdultCount,
            childCount = currentChildCount
        )
    }
    
    override fun updateRoomCount(count: Int) {
        if (count >= 1 && count <= 10) { // 限制房间数量在1-10之间
            currentRoomCount = count
        }
    }
    
    override fun updateAdultCount(count: Int) {
        if (count >= 1 && count <= 20) { // 限制成人数量在1-20之间
            currentAdultCount = count
        }
    }
    
    override fun updateChildCount(count: Int) {
        if (count >= 0 && count <= 10) { // 限制儿童数量在0-10之间
            currentChildCount = count
        }
    }
    
    override fun saveRoomAndGuestSelection(selection: RoomAndGuestSelection) {
        val selectionData = mapOf(
            "action" to "room_guest_selected",
            "timestamp" to System.currentTimeMillis(),
            "room_count" to selection.roomCount,
            "adult_count" to selection.adultCount,
            "child_count" to selection.childCount
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_room_guest_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun resetSelection() {
        currentRoomCount = 1
        currentAdultCount = 1
        currentChildCount = 0
    }
}