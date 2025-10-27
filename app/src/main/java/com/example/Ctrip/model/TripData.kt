package com.example.Ctrip.model

data class TripData(
    val hasTrips: Boolean = false,
    val routePlanning: RoutePlanning,
    val travelMap: TravelMap,
    val cityRecommendations: List<CityRecommendation>
)

data class RoutePlanning(
    val attractions: List<Attraction>,
    val planningButtonText: String = "开始规划",
    val supportText: String = "支持智能推荐线路"
)

data class Attraction(
    val id: String,
    val name: String,
    val type: String, // "attraction", "hotel", "station"
    val icon: String,
    val color: String,
    val description: String = ""
)

data class TravelMap(
    val title: String = "旅游地图",
    val subtitle: String = "发现你的旅行灵感",
    val icon: String = "🗺️"
)

data class CityRecommendation(
    val id: String,
    val cityName: String,
    val moreText: String = "攻略",
    val contents: List<CityContent>
)

data class CityContent(
    val id: String,
    val type: String, // "guide", "note", "planning"
    val title: String,
    val subtitle: String,
    val imageUrl: String = "",
    val tag: String = "",
    val author: String = "",
    val description: String = ""
)