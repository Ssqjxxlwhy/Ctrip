package com.example.Ctrip.home

class TrainPaymentSuccessTabPresenter(
    private val model: TrainPaymentSuccessTabModel
) : TrainPaymentSuccessTabContract.Presenter {

    private var view: TrainPaymentSuccessTabContract.View? = null

    override fun attachView(view: TrainPaymentSuccessTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun loadPaymentSuccessData(paymentData: TicketPaymentTabContract.PaymentData) {
        view?.showLoading()

        try {
            val successData = model.getPaymentSuccessData(paymentData)
            view?.showPaymentSuccess(successData)
        } catch (e: Exception) {
            view?.showError("加载支付信息失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }

    override fun onViewOrderClicked() {
        view?.navigateToOrderDetail()
    }

    override fun onContinueShoppingClicked() {
        view?.navigateToHome()
    }
}
