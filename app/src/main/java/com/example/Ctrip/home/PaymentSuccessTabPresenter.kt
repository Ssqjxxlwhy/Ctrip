package com.example.Ctrip.home

class PaymentSuccessTabPresenter(
    private val model: PaymentSuccessTabContract.Model
) : PaymentSuccessTabContract.Presenter {

    private var view: PaymentSuccessTabContract.View? = null
    private var currentData: PaymentSuccessData? = null

    override fun attachView(view: PaymentSuccessTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadPaymentSuccessData(serviceData: ServiceSelectData) {
        view?.showLoading()

        try {
            val data = model.getPaymentSuccessData(serviceData)
            if (data != null) {
                currentData = data
                model.savePaymentSuccessAction(data)
                view?.showPaymentSuccessData(data)
            } else {
                view?.showError("无法加载支付成功信息")
            }
        } catch (e: Exception) {
            view?.showError("加载失败：${e.message}")
        } finally {
            view?.hideLoading()
        }
    }

    override fun onViewOrderClicked() {
        view?.navigateToOrderList()
    }

    override fun onContinueShoppingClicked() {
        view?.navigateToHome()
    }

    override fun onBackClicked() {
        view?.onBack()
    }
}
