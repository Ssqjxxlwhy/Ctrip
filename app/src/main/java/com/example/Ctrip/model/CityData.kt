package com.example.Ctrip.model

data class City(
    val cityId: String,
    val cityName: String,
    val cityCode: String,
    val province: String,
    val country: String,
    val isHot: Boolean = false,
    val pinyin: String,
    val firstLetter: String,
    val isInternational: Boolean = false
)

data class CityGroup(
    val letter: String,
    val cities: List<City>
)

data class CitySearchHistory(
    val cities: List<City>
)

enum class LocationRegion {
    DOMESTIC,
    INTERNATIONAL
}

data class CitySelectionData(
    val history: List<City>,
    val hotCities: List<City>,
    val domesticCities: Map<String, List<City>>,
    val internationalCities: Map<String, List<City>>,
    val isLocationEnabled: Boolean = false
)