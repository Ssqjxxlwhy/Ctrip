package com.example.Ctrip.utils

import java.time.LocalDate

object DateUtils {
    /**
     * 固定的当前日期 - 2025年10月20日
     * 这样确保酒店预订日期范围总是可用的
     */
    fun getCurrentDate(): LocalDate {
        return LocalDate.of(2025, 10, 20)
    }
    
    /**
     * 获取明天的日期
     */
    fun getTomorrowDate(): LocalDate {
        return getCurrentDate().plusDays(1)
    }
}