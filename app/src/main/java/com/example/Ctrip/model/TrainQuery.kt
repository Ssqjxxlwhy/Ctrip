package com.example.Ctrip.model

import java.time.LocalDate
import java.time.LocalTime

enum class TrainType {
    HIGH_SPEED,
    BULLET,
    EXPRESS,
    FAST,
    REGULAR
}

data class TrainQueryFilter(
    val departureCity: String,
    val arrivalCity: String,
    val travelDate: LocalDate,
    val trainTypes: List<TrainType>? = null,
    val seatTypes: List<SeatType>? = null,
    val earliestDepartureTime: LocalTime? = null,
    val latestDepartureTime: LocalTime? = null,
    val maxPrice: Double? = null
)

data class TrainResult(
    val trainId: String,
    val trainInfo: TrainInfo,
    val availableSeats: Map<SeatType, Int>,
    val prices: Map<SeatType, Double>,
    val duration: String
)