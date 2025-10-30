package com.example.Ctrip.home

class InfoConfirmTabPresenter(private val model: InfoConfirmTabContract.Model) :
    InfoConfirmTabContract.Presenter {

    private var view: InfoConfirmTabContract.View? = null
    private var currentData: InfoConfirmData? = null

    override fun attachView(view: InfoConfirmTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadConfirmData(
        trainId: String,
        ticketOption: TrainTicketOption,
        selectedSeatType: String
    ) {
        view?.showLoading()

        val data = model.getConfirmData(trainId, ticketOption, selectedSeatType)
        if (data != null) {
            currentData = data
            view?.showConfirmData(data)
        } else {
            view?.showError("加载订单信息失败")
        }

        view?.hideLoading()
    }

    override fun onBackClicked() {
        view?.navigateBack()
    }

    override fun onLoginClicked() {
        view?.navigateToLogin()
    }

    override fun onAddPassengerClicked() {
        view?.showPassengerPicker()
    }

    override fun onPhoneEditClicked() {
        view?.showPhoneEditor()
    }

    override fun onCarriageSelected(carriageNumber: String) {
        currentData?.let { data ->
            val updatedCarriages = data.availableCarriages.map { carriage ->
                if (carriage.number == carriageNumber) {
                    carriage.copy(isSelected = !carriage.isSelected)
                } else {
                    carriage
                }
            }
            val updatedData = data.copy(availableCarriages = updatedCarriages)
            currentData = updatedData
            view?.showConfirmData(updatedData)
        }
    }

    override fun onSeatSelected(position: SeatPosition) {
        currentData?.let { data ->
            val updatedSeats = data.availableSeats.map { seat ->
                if (seat.position == position) {
                    seat.copy(isSelected = !seat.isSelected)
                } else {
                    seat
                }
            }
            val updatedData = data.copy(availableSeats = updatedSeats)
            currentData = updatedData
            view?.showConfirmData(updatedData)
        }
    }

    override fun onConfirmOrderClicked() {
        currentData?.let { data ->
            // 保存订单操作记录
            saveOrderAction(data)
            // 导航到支付页面
            view?.navigateToPayment(data)
        }
    }

    override fun onShowDetailClicked() {
        // 展示详情
    }

    override fun onSeatDiagramClicked() {
        view?.showSeatDiagram()
    }

    override fun saveOrderAction(data: InfoConfirmData) {
        model.saveOrderAction(data)
    }
}
