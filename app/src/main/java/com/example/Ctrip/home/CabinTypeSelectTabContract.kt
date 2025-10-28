package com.example.Ctrip.home

// 舱型选择页面数据模型
data class CabinTypeSelectData(
    val flightInfo: FlightBasicInfo,
    val travelTip: TravelTip,
    val membershipBenefit: MembershipBenefit,
    val cabinTypes: List<CabinTypeOption>,
    val ticketOptions: List<TicketOption>
)

data class FlightBasicInfo(
    val route: String, // "上海 → 北京"
    val date: String, // "10-29周三"
    val departureTime: String, // "07:20"
    val arrivalTime: String, // "09:40"
    val departure: String, // "浦东T2"
    val arrival: String, // "大兴"
    val airline: String, // "吉祥"
    val flightNumber: String, // "HO1251"
    val services: List<String>, // ["有餐食", "中机型", "到达准点率100%"]
    val isFavorite: Boolean = false
)

data class TravelTip(
    val title: String, // "行程提示"
    val content: String, // "该航班到达机场为北京大兴机场..."
    val isExpandable: Boolean = true
)

data class MembershipBenefit(
    val title: String, // "升级白金解锁"
    val benefits: String, // "酒店免费取消｜延迟退房 等"
    val isUpgradeable: Boolean = true
)

data class CabinTypeOption(
    val id: String,
    val name: String, // "经济舱"
    val priceFrom: String, // "¥392起"
    val description: String? = null, // "享专属定制服务"
    val isSelected: Boolean = false
)

data class TicketOption(
    val id: String,
    val price: String, // "¥405"
    val originalPrice: String? = null,
    val discount: String, // "经济舱2.3折"
    val baggageInfo: String, // "托运行李额20KG"
    val refundInfo: String, // "退票¥284起"
    val changeInfo: String, // "改签¥203起"
    val pointsInfo: String? = null, // "积分最高可抵¥400"
    val benefits: List<String> = emptyList(), // ["赠接送机最高8折券"]
    val restrictions: String? = null, // "限使用身份证，且吉祥航会员..."
    val insurancePrice: String? = null, // "+¥48全能保障"
    val buttonText: String, // "订" or "选购"
    val buttonType: TicketButtonType = TicketButtonType.SELECT,
    val specialTag: String? = null // "人群特惠 青老年享"
)

enum class TicketButtonType {
    BOOK, // "订"
    SELECT // "选购"
}

// Contract接口定义
interface CabinTypeSelectTabContract {
    
    interface View {
        fun showCabinTypeData(data: CabinTypeSelectData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToOrderForm(ticketOptionId: String)
        fun onBack()
        fun showTravelTipDetails()
        fun showMembershipUpgrade()
        fun showInsuranceDetails()
        fun showReminderSettings()
        fun showMoreOptions()
        fun toggleFavorite()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadCabinTypeData(flightId: String)
        fun onCabinTypeSelected(cabinTypeId: String)
        fun onTicketOptionSelected(ticketOptionId: String)
        fun onBookTicket(ticketOptionId: String)
        fun onBackClicked()
        fun onTravelTipClicked()
        fun onMembershipBenefitClicked()
        fun onInsuranceClicked()
        fun onReminderClicked()
        fun onMoreOptionsClicked()
        fun onFavoriteClicked()
    }
    
    interface Model {
        fun getCabinTypeData(flightId: String): CabinTypeSelectData?
        fun saveCabinTypeSelection(cabinTypeId: String)
        fun saveTicketOptionSelection(ticketOptionId: String)
        fun saveBookingAction(ticketOptionId: String)
        fun toggleFlightFavorite(flightId: String): Boolean
    }
}