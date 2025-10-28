package com.example.Ctrip.home

// 填订单页面数据模型
data class OrderFormData(
    val flightInfo: OrderFlightInfo,
    val priceBreakdown: FlightPriceBreakdown,
    val restrictions: OrderRestrictions,
    val contactInfo: ContactInfo,
    val passengers: List<Passenger>,
    val insuranceOptions: InsuranceOptions
)

data class OrderFlightInfo(
    val tripType: String, // "单程"
    val route: String, // "上海→北京"
    val date: String, // "10-29 周三"
    val time: String, // "07:20"
    val airline: String, // "吉祥"
    val flightNumber: String // "HO1251"
)

data class FlightPriceBreakdown(
    val adultPrice: String, // "成人¥405"
    val servicePrice: String, // "全能保障服务¥48"
    val taxPrice: String, // "机建燃油¥70"
    val totalPrice: Int // 总价
)

data class OrderRestrictions(
    val idCardOnly: Boolean, // 限使用身份证
    val membershipRequired: String, // "且吉祥航会员"
    val verificationRequired: String, // "且携程实名认证用户的乘客可订"
    val additionalInfo: String // 额外说明
)

data class ContactInfo(
    val countryCode: String, // "+86"
    val phoneNumber: String, // "155 7163 1377"
    val isVerified: Boolean = false
)

data class Passenger(
    val id: String,
    val name: String,
    val idCard: String,
    val passengerType: String, // "成人"、"儿童"、"婴儿"
    val isSelected: Boolean = false
)

data class InsuranceOptions(
    val flightInsurance: FlightInsuranceGroup,
    val accidentInsurance: AccidentInsurance,
    val travelInsurance: TravelInsurance
)

data class FlightInsuranceGroup(
    val title: String, // "航意航延组合险(2025A)"
    val hasTerms: Boolean, // 是否有投保须知
    val options: List<InsuranceOption>,
    val selectedOptionId: String? = null
)

data class InsuranceOption(
    val id: String,
    val name: String, // "无保障"、"标准保障"、"尊享保障"
    val price: String, // "¥40/人"
    val benefits: List<String>, // 保障内容列表
    val isSelected: Boolean = false
)

data class AccidentInsurance(
    val name: String, // "航空意外险"
    val price: String, // "¥29/人"
    val description: String, // "航空意外保最高¥350万，含意外医疗、行李损失等保障"
    val hasTerms: Boolean,
    val isSelected: Boolean = false
)

data class TravelInsurance(
    val name: String, // "国内旅行险"
    val isExpanded: Boolean = false,
    val options: List<TravelInsuranceOption> = emptyList()
)

data class TravelInsuranceOption(
    val id: String,
    val name: String,
    val price: String,
    val description: String
)

// Contract接口定义
interface OrderFormTabContract {
    
    interface View {
        fun showOrderFormData(data: OrderFormData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToServiceSelect(orderData: OrderFormData)
        fun onBack()
        fun showCustomerService()
        fun showPassengerSelector()
        fun showContactEditor()
        fun showInsuranceTerms(insuranceType: String)
        fun updateInsuranceSelection(insuranceType: String, optionId: String)
        fun updateTotalPrice(newPrice: Int)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadOrderFormData(ticketOptionId: String)
        fun onBackClicked()
        fun onCustomerServiceClicked()
        fun onPassengerAddClicked()
        fun onContactEditClicked()
        fun onContactVerifyClicked()
        fun onInsuranceOptionSelected(insuranceType: String, optionId: String)
        fun onInsuranceTermsClicked(insuranceType: String)
        fun onTravelInsuranceToggle()
        fun onNextStepClicked()
        fun updateContactInfo(countryCode: String, phoneNumber: String)
        fun addPassenger(passenger: Passenger)
        fun removePassenger(passengerId: String)
    }
    
    interface Model {
        fun getOrderFormData(ticketOptionId: String): OrderFormData?
        fun saveOrderFormData(data: OrderFormData)
        fun updateInsuranceSelection(insuranceType: String, optionId: String)
        fun updateContactInfo(contactInfo: ContactInfo)
        fun addPassenger(passenger: Passenger)
        fun removePassenger(passengerId: String)
        fun calculateTotalPrice(data: OrderFormData): Int
        fun saveOrderAction(orderData: OrderFormData)
    }
}