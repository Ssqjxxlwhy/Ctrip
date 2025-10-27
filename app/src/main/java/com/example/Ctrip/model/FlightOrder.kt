package com.example.Ctrip.model

import java.time.LocalDate
import java.time.LocalDateTime

enum class FlightOrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

enum class CabinClass {
    ECONOMY,
    PREMIUM_ECONOMY,
    BUSINESS,
    FIRST
}

data class PassengerInfo(
    val name: String,
    val idCard: String,
    val phone: String,
    val passportNumber: String? = null
)

data class FlightInfo(
    val flightNumber: String,
    val airline: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val departureTime: LocalDateTime,
    val arrivalTime: LocalDateTime
)

data class FlightOrder(
    val orderId: String,
    val flightInfo: FlightInfo,
    val cabinClass: CabinClass,
    val passengerInfo: PassengerInfo,
    val status: FlightOrderStatus,
    val price: Double,
    val bookingDate: LocalDate
)