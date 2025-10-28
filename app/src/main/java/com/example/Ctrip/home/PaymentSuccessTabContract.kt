package com.example.Ctrip.home

// 支付成功页面数据模型
data class PaymentSuccessData(
    val paymentInfo: PaymentSuccessInfo,
    val orderInfo: OrderSuccessInfo
)

data class PaymentSuccessInfo(
    val amount: Int, // 支付金额
    val paymentMethod: String // 支付方式 "微信支付"、"支付宝"等
)

data class OrderSuccessInfo(
    val orderNumber: String, // 订单号
    val status: String, // "订单已生成"
    val statusDescription: String, // "您的订单正在处理中，我们会尽快为您安排出票"
    val flightInfo: String, // 航班信息 "上海→北京 10-29 07:20"
    val estimatedDate: String // 预计日期 "10月29日（周三）"
)

// Contract接口定义
interface PaymentSuccessTabContract {

    interface View {
        fun showPaymentSuccessData(data: PaymentSuccessData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToOrderList()
        fun navigateToHome()
        fun onBack()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPaymentSuccessData(serviceData: ServiceSelectData)
        fun onViewOrderClicked()
        fun onContinueShoppingClicked()
        fun onBackClicked()
    }

    interface Model {
        fun getPaymentSuccessData(serviceData: ServiceSelectData): PaymentSuccessData?
        fun savePaymentSuccessAction(data: PaymentSuccessData)
    }
}
