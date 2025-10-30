package com.example.Ctrip.home

class TicketPaymentTabPresenter(
    private val model: TicketPaymentTabModel
) : TicketPaymentTabContract.Presenter {

    private var view: TicketPaymentTabContract.View? = null
    private var currentPaymentData: TicketPaymentTabContract.PaymentData? = null

    override fun attachView(view: TicketPaymentTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun loadPaymentData(confirmData: InfoConfirmData) {
        view?.showLoading()

        try {
            val paymentData = model.getPaymentData(confirmData)
            if (paymentData != null) {
                currentPaymentData = paymentData
                view?.showPaymentData(paymentData)
            } else {
                view?.showError("未找到订单信息")
            }
        } catch (e: Exception) {
            view?.showError("加载订单信息失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }

    override fun onPayClicked() {
        currentPaymentData?.let { data ->
            // 这里可以添加支付逻辑
            view?.navigateToPaymentSuccess()
        }
    }

    override fun onBackClicked() {
        view?.navigateBack()
    }

    override fun onBookingNotesClicked() {
        view?.showBookingNotes()
    }

    override fun onPriceDetailsClicked() {
        view?.showPriceDetails()
    }

    override fun onCancelOrderClicked() {
        view?.cancelOrder()
    }
}