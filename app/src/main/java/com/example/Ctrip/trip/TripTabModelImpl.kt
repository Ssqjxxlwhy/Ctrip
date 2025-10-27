package com.example.Ctrip.trip

import android.content.Context
import com.example.Ctrip.model.TripData
import com.google.gson.Gson
import java.io.IOException

class TripTabModelImpl(private val context: Context) : TripTabContract.Model {
    
    override fun getTripData(): TripData {
        return try {
            val jsonString = context.assets.open("data/trip_data.json").bufferedReader().use { it.readText() }
            val result = Gson().fromJson(jsonString, TripData::class.java)
            result ?: getDefaultData()
        } catch (e: Exception) {
            // Return default data if any error occurs
            getDefaultData()
        }
    }
    
    private fun getDefaultData(): TripData {
        return TripData(
            hasTrips = false,
            routePlanning = com.example.Ctrip.model.RoutePlanning(
                attractions = emptyList(),
                planningButtonText = "开始规划",
                supportText = "支持智能推荐线路"
            ),
            travelMap = com.example.Ctrip.model.TravelMap(),
            cityRecommendations = emptyList()
        )
    }
}