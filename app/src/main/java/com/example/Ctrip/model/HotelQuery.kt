package com.example.Ctrip.model

import java.time.LocalDate

data class HotelQueryFilter(
    val city: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Double? = null,
    val maxRating: Double? = null,
    val facilities: List<String>? = null
)

data class HotelInfo(
    val hotelId: String,
    val name: String,
    val city: String,
    val address: String,
    val rating: Double,
    val price: Double,
    val facilities: List<String>,
    val availableRoomTypes: List<String>
)