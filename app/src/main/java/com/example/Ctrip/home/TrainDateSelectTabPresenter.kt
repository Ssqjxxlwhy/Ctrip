package com.example.Ctrip.home

import java.time.LocalDate

class TrainDateSelectTabPresenter(private val model: TrainDateSelectTabContract.Model) : TrainDateSelectTabContract.Presenter {

    private var view: TrainDateSelectTabContract.View? = null
    private var selectedDate: LocalDate = model.getTodayDate()

    override fun attachView(view: TrainDateSelectTabContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun onDateSelected(date: LocalDate) {
        if (model.isDateAvailable(date)) {
            selectedDate = date
            model.setSelectedDate(date)
            view?.showDateSelection(date)
        } else {
            view?.showError("不能选择过去的日期")
        }
    }

    override fun onCloseClicked() {
        view?.onClose()
    }

    override fun onDateConfirmed() {
        view?.onDateConfirmed(selectedDate)
    }
}
