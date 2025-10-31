package com.example.Ctrip.home

class TrainDetailTabPresenter(
    private val model: TrainDetailTabContract.Model,
    private val isStudentTicket: Boolean = false
) : TrainDetailTabContract.Presenter {

    private var view: TrainDetailTabContract.View? = null
    private var currentTrainId: String = ""
    private var currentSeatType: String = "二等座"
    private var trainDetailData: TrainDetailData? = null

    override fun attachView(view: TrainDetailTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadTrainDetail(trainId: String) {
        view?.showLoading()
        currentTrainId = trainId

        try {
            val data = model.getTrainDetail(trainId)
            if (data != null) {
                trainDetailData = data
                view?.showTrainDetail(data)
            } else {
                view?.showError("无法加载车次详情")
            }
        } catch (e: Exception) {
            view?.showError("加载失败: ${e.message}")
        } finally {
            view?.hideLoading()
        }
    }

    override fun onSeatTypeSelected(seatType: String) {
        if (currentSeatType == seatType) return

        currentSeatType = seatType
        view?.showLoading()

        try {
            val seatTypes = model.getSeatTypes(currentTrainId, seatType, isStudentTicket)
            val ticketOptions = model.getTicketOptions(currentTrainId, seatType, isStudentTicket)

            trainDetailData?.let { data ->
                val updatedData = data.copy(
                    seatTypes = seatTypes,
                    ticketOptions = ticketOptions
                )
                trainDetailData = updatedData
                view?.showTrainDetail(updatedData)
            }
        } catch (e: Exception) {
            view?.showError("切换座位类型失败")
        } finally {
            view?.hideLoading()
        }
    }

    override fun onTicketOptionClicked(optionId: String) {
        val option = trainDetailData?.ticketOptions?.find { it.id == optionId }
        if (option != null) {
            view?.navigateToOrderConfirm(option)
        }
    }

    override fun onBackClicked() {
        view?.navigateBack()
    }

    override fun onRefundPolicyClicked() {
        view?.showRefundPolicy()
    }

    override fun onNoticeClicked() {
        view?.showNotice()
    }

    override fun onShareClicked() {
        view?.share()
    }
}
