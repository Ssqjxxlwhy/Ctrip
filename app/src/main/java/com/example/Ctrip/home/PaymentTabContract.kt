package com.example.Ctrip.home

import com.example.Ctrip.model.*

// Data classes for Payment
data class PaymentData(
    val orderId: String,
    val hotelName: String,
    val amount: Int,
    val remainingTime: String,
    val paymentMethods: List<PaymentMethod>,
    val financialServices: List<FinancialService>
)

data class PaymentMethod(
    val id: String,
    val type: PaymentMethodType,
    val title: String,
    val description: String,
    val discountInfo: String? = null,
    val isSelected: Boolean = false
)

enum class PaymentMethodType {
    CTRIP_PAY,
    ALIPAY,
    WECHAT_PAY,
    BANK_CARD,
    OTHER
}

data class FinancialService(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val installmentOptions: List<InstallmentOption>? = null,
    val isSelected: Boolean = false,
    val discountInfo: String? = null
)

data class InstallmentOption(
    val months: Int,
    val monthlyAmount: Double,
    val serviceFee: Double,
    val description: String
)

// Contract interfaces
interface PaymentTabContract {
    
    interface View {
        fun showPaymentData(data: PaymentData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onPaymentMethodSelected(methodId: String)
        fun onFinancialServiceSelected(serviceId: String)
        fun onInstallmentOptionSelected(serviceId: String, months: Int)
        fun onPaymentConfirmed()
        fun onBackPressed()
        fun navigateBack()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPaymentData(confirmationData: com.example.Ctrip.home.InfoConfirmationData)
        fun onBackPressed()
        fun onPaymentMethodClicked(methodId: String)
        fun onFinancialServiceClicked(serviceId: String)
        fun onInstallmentOptionClicked(serviceId: String, months: Int)
        fun onPaymentConfirmed()
        fun onOtherPaymentMethodsClicked()
    }
    
    interface Model {
        fun getPaymentData(confirmationData: com.example.Ctrip.home.InfoConfirmationData): PaymentData?
        fun processPayment(methodId: String, serviceId: String?, installmentMonths: Int?): Boolean
        fun savePaymentOrder(orderData: Map<String, Any>)
    }
}