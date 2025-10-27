package com.example.Ctrip.home

class PaymentTabPresenter(private val model: PaymentTabContract.Model) : PaymentTabContract.Presenter {
    
    private var view: PaymentTabContract.View? = null
    private var currentPaymentData: PaymentData? = null
    private var selectedPaymentMethodId: String? = null
    private var selectedFinancialServiceId: String? = null
    private var selectedInstallmentMonths: Int? = null
    
    override fun attachView(view: PaymentTabContract.View) {
        this.view = view
    }
    
    override fun detachView() {
        this.view = null
    }
    
    override fun loadPaymentData(confirmationData: InfoConfirmationData) {
        view?.showLoading()
        
        try {
            val paymentData = model.getPaymentData(confirmationData)
            if (paymentData != null) {
                currentPaymentData = paymentData
                // Set default selected payment method
                selectedPaymentMethodId = paymentData.paymentMethods.firstOrNull { it.isSelected }?.id
                view?.showPaymentData(paymentData)
            } else {
                view?.showError("加载支付信息失败")
            }
        } catch (e: Exception) {
            view?.showError("加载支付信息失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onBackPressed() {
        view?.navigateBack()
    }
    
    override fun onPaymentMethodClicked(methodId: String) {
        selectedPaymentMethodId = methodId
        view?.onPaymentMethodSelected(methodId)
        
        // Update payment data to reflect selection
        currentPaymentData?.let { data ->
            val updatedMethods = data.paymentMethods.map { method ->
                method.copy(isSelected = method.id == methodId)
            }
            val updatedData = data.copy(paymentMethods = updatedMethods)
            currentPaymentData = updatedData
            view?.showPaymentData(updatedData)
        }
    }
    
    override fun onFinancialServiceClicked(serviceId: String) {
        selectedFinancialServiceId = if (selectedFinancialServiceId == serviceId) null else serviceId
        view?.onFinancialServiceSelected(serviceId)
        
        // Update financial services selection
        currentPaymentData?.let { data ->
            val updatedServices = data.financialServices.map { service ->
                service.copy(isSelected = service.id == selectedFinancialServiceId)
            }
            val updatedData = data.copy(financialServices = updatedServices)
            currentPaymentData = updatedData
            view?.showPaymentData(updatedData)
        }
    }
    
    override fun onInstallmentOptionClicked(serviceId: String, months: Int) {
        selectedInstallmentMonths = months
        view?.onInstallmentOptionSelected(serviceId, months)
    }
    
    override fun onPaymentConfirmed() {
        val methodId = selectedPaymentMethodId
        if (methodId == null) {
            view?.showError("请选择支付方式")
            return
        }
        
        view?.showLoading()
        
        try {
            val success = model.processPayment(
                methodId = methodId,
                serviceId = selectedFinancialServiceId,
                installmentMonths = selectedInstallmentMonths
            )
            
            if (success) {
                view?.onPaymentConfirmed()
            } else {
                view?.showError("支付处理失败，请重试")
            }
        } catch (e: Exception) {
            view?.showError("支付失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }
    
    override fun onOtherPaymentMethodsClicked() {
        // Handle other payment methods click
        view?.showError("其他支付方式功能待实现")
    }
}