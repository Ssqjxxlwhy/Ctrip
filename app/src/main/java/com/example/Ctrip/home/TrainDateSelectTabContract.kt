package com.example.Ctrip.home

import java.time.LocalDate

interface TrainDateSelectTabContract {

    interface View {
        fun showDateSelection(selectedDate: LocalDate)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onDateConfirmed(date: LocalDate)
        fun onClose()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onDateSelected(date: LocalDate)
        fun onCloseClicked()
        fun onDateConfirmed()
    }

    interface Model {
        fun getSelectedDate(): LocalDate
        fun setSelectedDate(date: LocalDate)
        fun getTodayDate(): LocalDate
        fun isDateAvailable(date: LocalDate): Boolean
    }
}
