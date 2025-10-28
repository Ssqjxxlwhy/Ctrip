package com.example.Ctrip.home

// 选服务页面数据模型
data class ServiceSelectData(
    val flightInfo: ServiceFlightInfo,
    val totalPrice: Int,
    val promotionBanner: ServicePromotionBanner,
    val serviceOptions: List<ServiceOption>,
    val paymentInfo: PaymentInfo,
    val agreements: List<Agreement>
)

data class ServiceFlightInfo(
    val route: String, // "上海→北京"
    val date: String, // "10-29"
    val cabinType: String // "经济舱"
)

data class ServicePromotionBanner(
    val title: String, // "安检不用排队"
    val subtitle: String // "省心又省时"
)

data class ServiceOption(
    val id: String,
    val name: String, // "快速安检"
    val price: String, // "¥1/份"
    val priceNote: String, // "机票同订价"
    val description: String, // 描述
    val features: List<String>, // ["有效期免费退订", "少排队", "省时省力", "尊贵体验"]
    val hasInfo: Boolean = true, // 是否有说明图标
    val isSelected: Boolean = false,
    val additionalInfo: String? = null // 额外说明信息
)

data class PaymentInfo(
    val totalPrice: Int, // 523
    val cabinType: String, // "经济舱"
    val hasDetails: Boolean = true, // 是否有明细
    val receiptInfo: ReceiptInfo,
    val countdownSeconds: Int = 300 // 倒计时秒数，默认5分钟
)

data class ReceiptInfo(
    val title: String, // "支付完成后可开具报销凭证，安心购票"
    val description: String // "所有行程结束后，可开具电子行程单或电子发票..."
)

data class Agreement(
    val id: String,
    val name: String, // "购票须知"
    val link: String? = null, // 协议链接
    val isChecked: Boolean = true
)

// Contract接口定义
interface ServiceSelectTabContract {

    interface View {
        fun showServiceSelectData(data: ServiceSelectData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToPayment(orderData: ServiceSelectData)
        fun onBack()
        fun showServiceInfo(serviceId: String)
        fun showPriceDetails()
        fun showReceiptInfo()
        fun updateServiceSelection(serviceId: String, isSelected: Boolean)
        fun updateTotalPrice(newPrice: Int)
        fun showCountdownExpired()
        fun showAgreementDetails(agreementId: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadServiceSelectData(orderFormData: OrderFormData)
        fun onBackClicked()
        fun onServiceToggled(serviceId: String)
        fun onServiceInfoClicked(serviceId: String)
        fun onPriceDetailsClicked()
        fun onReceiptInfoClicked()
        fun onPaymentClicked()
        fun onAgreementToggled(agreementId: String)
        fun onAgreementClicked(agreementId: String)
        fun onShowMoreServicesClicked()
        fun startCountdown()
        fun stopCountdown()
    }

    interface Model {
        fun getServiceSelectData(orderFormData: OrderFormData): ServiceSelectData?
        fun saveServiceSelection(serviceId: String, isSelected: Boolean)
        fun calculateTotalPrice(data: ServiceSelectData): Int
        fun savePaymentAction(data: ServiceSelectData)
        fun getCountdownDuration(): Int
    }
}
