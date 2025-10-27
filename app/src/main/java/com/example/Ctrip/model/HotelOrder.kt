package com.example.Ctrip.model

import java.time.LocalDate

enum class HotelOrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

data class GuestInfo(
    val name: String,
    val idCard: String,
    val phone: String
)

data class HotelOrder(
    val orderId: String,
    val city: String,
    val hotelName: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val roomType: String,
    val guestInfo: GuestInfo,
    val status: HotelOrderStatus,
    val totalPrice: Double,
    val createdAt: LocalDate
)