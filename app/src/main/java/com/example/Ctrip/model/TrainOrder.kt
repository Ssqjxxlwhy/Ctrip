package com.example.Ctrip.model

import java.time.LocalDate
import java.time.LocalDateTime

enum class TrainOrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

enum class SeatType {
    HARD_SEAT,
    SOFT_SEAT,
    HARD_SLEEPER,
    SOFT_SLEEPER,
    BUSINESS_CLASS,
    FIRST_CLASS,
    SECOND_CLASS
}

data class TrainInfo(
    val trainNumber: String,
    val trainType: String,
    val departureStation: String,
    val arrivalStation: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime
)

data class TrainOrder(
    val orderId: String,
    val trainInfo: TrainInfo,
    val seatType: SeatType,
    val seatNumber: String,
    val passengerInfo: PassengerInfo,
    val travelDate: LocalDate,
    val status: TrainOrderStatus,
    val price: Double,
    val bookingDate: LocalDate
)