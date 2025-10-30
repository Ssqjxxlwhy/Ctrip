package com.example.Ctrip.home

class HotelPaymentSuccessTabPresenter(
    private val model: HotelPaymentSuccessTabModel
) : HotelPaymentSuccessTabContract.Presenter {

    private var view: HotelPaymentSuccessTabContract.View? = null

    override fun attachView(view: HotelPaymentSuccessTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    override fun loadPaymentSuccessData(paymentData: PaymentData, confirmationData: InfoConfirmationData) {
        view?.showLoading()

        try {
            val successData = model.getPaymentSuccessData(paymentData, confirmationData)
            if (successData != null) {
                view?.showPaymentSuccess(successData)
            } else {
                view?.showError("加载支付成功数据失败")
            }
        } catch (e: Exception) {
            view?.showError("加载数据时出错：${e.message}")
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
