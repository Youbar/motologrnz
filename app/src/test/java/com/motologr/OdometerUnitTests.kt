package com.motologr

import com.motologr.data.objects.fuel.Fuel
import com.motologr.data.objects.vehicle.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Test

class OdometerUnitTests : UnitTestBase() {
    @Test
    fun getLatestOdometerReading_DefaultReading() {
        val vehicle = returnDefaultVehicle()

        val latestOdometerReading = vehicle.getLatestOdometerReading()

        assertEquals(125000, latestOdometerReading)
    }

    @Test
    fun getLatestOdometerReading_NegativeReading() {
        val vehicle = returnDefaultVehicle()

        val someDate = returnDate(2024, 5, 14)
        val fuel = Fuel(0, 125.0.toBigDecimal(), 0.0.toBigDecimal(), someDate, -1, 0)
        vehicle.logFuel(fuel)
        val latestOdometerReading = vehicle.getLatestOdometerReading()

        assertEquals(125000, latestOdometerReading)
    }

    @Test
    fun getLatestOdometerReading_ZeroReading() {
        val vehicle = returnDefaultVehicle()

        val someDate = returnDate(2024, 5, 14)
        val fuel = Fuel(0, 125.0.toBigDecimal(), 0.0.toBigDecimal(), someDate, 0, 0)
        vehicle.logFuel(fuel)
        val latestOdometerReading = vehicle.getLatestOdometerReading()

        assertEquals(125000, latestOdometerReading)
    }

    @Test
    fun getLatestOdometerReading_LessThanInitialOdometerReading() {
        val vehicle = returnDefaultVehicle()

        val someDate = returnDate(2024, 5, 14)
        val fuel = Fuel(0, 125.0.toBigDecimal(), 0.0.toBigDecimal(), someDate, 124700, 0)
        vehicle.logFuel(fuel)
        val latestOdometerReading = vehicle.getLatestOdometerReading()

        assertEquals(125000, latestOdometerReading)
    }
}