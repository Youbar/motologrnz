package com.motologr

import com.motologr.data.objects.insurance.Insurance
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

class InsuranceUnitTests : UnitTestBase() {
    @Test
    fun hasCurrentInsurance_expectFalse() {
        val localDate = LocalDate.of(2023, 1, 1)
        val startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val vehicle = returnDefaultVehicle()
        val insurance = Insurance(0, "AA", startDate, 0, 0, 15.30.toBigDecimal(), startDate, 0)
        insurance.generateInsuranceBills()
        vehicle.logInsurance(insurance)

        assert(!vehicle.hasCurrentInsurance())
    }

    @Test
    fun hasCurrentInsurance_expectTrue_oneMonthPrior() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)

        val vehicle = returnDefaultVehicle()
        val insurance = Insurance(0, "AA", calendar.time, 0, 0, 15.30.toBigDecimal(), calendar.time, 0)
        insurance.generateInsuranceBills()
        vehicle.logInsurance(insurance)

        assert(vehicle.hasCurrentInsurance())
    }

    @Test
    fun hasCurrentInsurance_expectTrue_atInstant() {
        val calendar = Calendar.getInstance()

        val vehicle = returnDefaultVehicle()
        val insurance = Insurance(0, "AA", calendar.time, 0, 0, 15.30.toBigDecimal(), calendar.time, 0)
        insurance.generateInsuranceBills()
        vehicle.logInsurance(insurance)

        assert(vehicle.hasCurrentInsurance())
    }
}