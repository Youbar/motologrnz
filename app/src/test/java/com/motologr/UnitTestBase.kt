package com.motologr

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

open class UnitTestBase {
    fun returnDate(year : Int, month : Int, dayOfMonth : Int) : Date {
        val localDate = LocalDate.of(year, month, dayOfMonth)
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }
}
