package com.motologr

import com.motologr.ui.data.objects.maint.Repair
import com.motologr.ui.data.objects.maint.Service
import com.motologr.ui.data.objects.maint.Wof
import com.motologr.ui.data.objects.vehicle.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class VehicleExpensesTests : UnitTestBase() {
    @Test
    fun vehicleExpensesTests_FinancialYear_NoInsurance() {
        // Create vehicle
        val wofDate = returnDate(2024, 7, 5)
        val regDate = returnDate(2024, 5, 14)
        val vehicle = Vehicle(0, "Mazda", "323", 1989, wofDate, regDate, 125000)

        // Add repair 1 within FY
        var repairDate = returnDate(2024, 4, 1)
        var repair = Repair(0, 250.0.toBigDecimal(), repairDate, "Avonhead Automotive", "Brake pads", vehicle.id)
        vehicle.repairLog.addRepairToRepairLog(repair)

        // Add repair 2 outside FY
        repairDate = returnDate(2024, 3, 31)
        repair = Repair(0, 125.0.toBigDecimal(), repairDate, "Avonhead Automotive", "Brake pads", vehicle.id)
        vehicle.repairLog.addRepairToRepairLog(repair)

        // Add service 1 within FY
        var serviceDate = returnDate(2024, 7, 5)
        var service = Service(0, 50.0.toBigDecimal(), serviceDate, "Avonhead Automotive", "Oil change", vehicle.id)
        vehicle.serviceLog.addServiceToServiceLog(service)

        // Add service 2 outside FY
        serviceDate = returnDate(2025, 6, 9)
        service = Service(0, 1000.0.toBigDecimal(), serviceDate, "Avonhead Automotive", "Deluxe service", vehicle.id)
        vehicle.serviceLog.addServiceToServiceLog(service)

        val newWofDate = returnDate(2025, 3, 31)
        val wof = Wof(wofDate, newWofDate, 45.0.toBigDecimal(), vehicle.id)
        vehicle.wofLog.addWofToWofLog(wof)

        val total = vehicle.returnExpensesWithinFinancialYear()
        assertEquals(345.0.toBigDecimal(), total)
    }
}