package com.example.Ctrip.home

data class QuickAction(
    val id: String,
    val title: String,
    val icon: String,
    val color: String
)

data class UserInfo(
    val memberLevel: String,
    val points: Int
)

data class SearchSuggestion(
    val text: String,
    val type: String
)

data class ContentSection(
    val id: String,
    val title: String,
    val icon: String
)

data class PromotionBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val price: String,
    val imageUrl: String,
    val backgroundColor: String
)

data class HomeTabData(
    val userInfo: UserInfo,
    val mainActions: List<QuickAction>,
    val secondaryActions: List<QuickAction>,
    val searchSuggestions: List<SearchSuggestion>,
    val contentSections: List<ContentSection>,
    val promotionBanners: List<PromotionBanner>
)