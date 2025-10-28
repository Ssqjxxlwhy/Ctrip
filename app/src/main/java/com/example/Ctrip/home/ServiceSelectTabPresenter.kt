package com.example.Ctrip.home

import kotlinx.coroutines.*

class ServiceSelectTabPresenter(
    private val model: ServiceSelectTabContract.Model
) : ServiceSelectTabContract.Presenter {

    private var view: ServiceSelectTabContract.View? = null
    private var currentData: ServiceSelectData? = null
    private var countdownJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun attachView(view: ServiceSelectTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
        stopCountdown()
        coroutineScope.cancel()
    }

    override fun loadServiceSelectData(orderFormData: OrderFormData) {
        view?.showLoading()

        try {
            val data = model.getServiceSelectData(orderFormData)
            if (data != null) {
                currentData = data
                view?.showServiceSelectData(data)
                startCountdown()
            } else {
                view?.showError("无法加载服务选择信息")
            }
        } catch (e: Exception) {
            view?.showError("加载失败：${e.message}")
        } finally {
            view?.hideLoading()
        }
    }

    override fun onBackClicked() {
        stopCountdown()
        view?.onBack()
    }

    override fun onServiceToggled(serviceId: String) {
        currentData?.let { data ->
            val updatedServices = data.serviceOptions.map { service ->
                if (service.id == serviceId) {
                    service.copy(isSelected = !service.isSelected)
                } else {
                    service
                }
            }

            val updatedData = data.copy(serviceOptions = updatedServices)
            currentData = updatedData

            // 保存选择状态
            val selectedService = updatedServices.find { it.id == serviceId }
            selectedService?.let {
                model.saveServiceSelection(serviceId, it.isSelected)
            }

            // 更新总价
            val newTotalPrice = model.calculateTotalPrice(updatedData)
            view?.updateTotalPrice(newTotalPrice)
            view?.updateServiceSelection(serviceId, selectedService?.isSelected ?: false)
        }
    }

    override fun onServiceInfoClicked(serviceId: String) {
        view?.showServiceInfo(serviceId)
    }

    override fun onPriceDetailsClicked() {
        view?.showPriceDetails()
    }

    override fun onReceiptInfoClicked() {
        view?.showReceiptInfo()
    }

    override fun onPaymentClicked() {
        currentData?.let { data ->
            // 验证协议是否已勾选
            val allAgreed = data.agreements.all { it.isChecked }
            if (!allAgreed) {
                view?.showError("请阅读并同意相关协议")
                return
            }

            stopCountdown()
            model.savePaymentAction(data)
            view?.navigateToPayment(data)
        } ?: run {
            view?.showError("数据异常")
        }
    }

    override fun onAgreementToggled(agreementId: String) {
        currentData?.let { data ->
            val updatedAgreements = data.agreements.map { agreement ->
                if (agreement.id == agreementId) {
                    agreement.copy(isChecked = !agreement.isChecked)
                } else {
                    agreement
                }
            }

            val updatedData = data.copy(agreements = updatedAgreements)
            currentData = updatedData
            view?.showServiceSelectData(updatedData)
        }
    }

    override fun onAgreementClicked(agreementId: String) {
        view?.showAgreementDetails(agreementId)
    }

    override fun onShowMoreServicesClicked() {
        // 暂时显示提示，后续可扩展更多服务
        view?.showError("暂无更多服务")
    }

    override fun startCountdown() {
        stopCountdown() // 先停止之前的倒计时

        countdownJob = coroutineScope.launch {
            var remainingSeconds = model.getCountdownDuration()

            while (remainingSeconds > 0 && isActive) {
                delay(1000) // 每秒更新一次
                remainingSeconds--

                currentData?.let { data ->
                    val updatedPaymentInfo = data.paymentInfo.copy(
                        countdownSeconds = remainingSeconds
                    )
                    val updatedData = data.copy(paymentInfo = updatedPaymentInfo)
                    currentData = updatedData
                    view?.showServiceSelectData(updatedData)
                }
            }

            // 倒计时结束
            if (isActive) {
                view?.showCountdownExpired()
            }
        }
    }

    override fun stopCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }
}
