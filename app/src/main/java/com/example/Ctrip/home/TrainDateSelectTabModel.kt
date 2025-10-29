package com.example.Ctrip.home

import android.content.Context
import java.time.LocalDate

class TrainDateSelectTabModel(private val context: Context) : TrainDateSelectTabContract.Model {

    private var selectedDate: LocalDate = getTodayDate()

    override fun getSelectedDate(): LocalDate {
        return selectedDate
    }

    override fun setSelectedDate(date: LocalDate) {
        selectedDate = date
    }

    override fun getTodayDate(): LocalDate {
        // 根据需求，固定设置为10月20日
        return LocalDate.of(2025, 10, 20)
    }

    override fun isDateAvailable(date: LocalDate): Boolean {
        // 只能选择今天及以后的日期
        val today = getTodayDate()
        return !date.isBefore(today)
    }
}
