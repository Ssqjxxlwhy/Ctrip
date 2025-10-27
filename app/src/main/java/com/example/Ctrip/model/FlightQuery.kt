package com.example.Ctrip.model

import java.time.LocalDate
import java.time.LocalTime

data class FlightQueryFilter(
    val departureCity: String,
    val arrivalCity: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate? = null,
    val cabinClass: CabinClass? = null,
    val airlines: List<String>? = null,
    val earliestDepartureTime: LocalTime? = null,
    val latestDepartureTime: LocalTime? = null,
    val earliestArrivalTime: LocalTime? = null,
    val latestArrivalTime: LocalTime? = null,
    val maxPrice: Double? = null,
    val minDiscount: Double? = null
)

data class FlightResult(
    val flightId: String,
    val flightInfo: FlightInfo,
    val cabinClass: CabinClass,
    val price: Double,
    val discount: Double,
    val availableSeats: Int
)