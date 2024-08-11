package com.motologr

import com.motologr.data.objects.vehicle.Vehicle
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

open class UnitTestBase {
    fun returnDate(year : Int, month : Int, dayOfMonth : Int) : Date {
        val localDate = LocalDate.of(year, month, dayOfMonth)
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    fun returnDefaultVehicle() : Vehicle {
        val wofDate = returnDate(2024, 7, 5)
        val regDate = returnDate(2024, 5, 14)
        return Vehicle(0, "Mazda", "323", 1989, wofDate, regDate, 125000)
    }
}
