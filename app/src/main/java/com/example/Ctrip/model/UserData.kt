package com.example.Ctrip.model

data class UserData(
    val userProfile: UserProfile,
    val membershipInfo: MembershipInfo,
    val statistics: UserStatistics,
    val menuItems: List<MenuItem>,
    val wallet: WalletInfo,
    val promotions: List<PromotionItem>,
    val myPublish: MyPublishInfo,
    val bottomPromotion: BottomPromotion?
)

data class UserProfile(
    val avatar: String = "👤",
    val displayName: String,
    val memberTitle: String,
    val followers: Int = 0,
    val following: Int = 0,
    val likes: Int = 0,
    val praised: Int = 0
)

data class MembershipInfo(
    val level: String,
    val levelIcon: String,
    val benefits: List<MemberBenefit>
)

data class MemberBenefit(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val color: String
)

data class UserStatistics(
    val favorites: Int,
    val browsingHistory: Int,
    val points: Int,
    val coupons: Int
)

data class MenuItem(
    val id: String,
    val title: String,
    val icon: String,
    val count: Int = 0,
    val hasNotification: Boolean = false
)

data class WalletInfo(
    val title: String = "我的钱包",
    val subtitle: String = "去实名",
    val walletItems: List<WalletItem>
)

data class WalletItem(
    val id: String,
    val name: String,
    val amount: String,
    val description: String,
    val hasPromotion: Boolean = false,
    val promotionText: String = ""
)

data class PromotionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val backgroundColor: String
)

data class MyPublishInfo(
    val title: String = "我的发布",
    val moreText: String = "携程社区",
    val items: List<PublishItem>
)

data class PublishItem(
    val id: String,
    val title: String,
    val icon: String,
    val color: String
)

data class BottomPromotion(
    val id: String,
    val title: String,
    val subtitle: String,
    val buttonText: String,
    val imageUrl: String = ""
)