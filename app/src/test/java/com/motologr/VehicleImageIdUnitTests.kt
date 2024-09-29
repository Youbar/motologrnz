package com.motologr

import com.motologr.data.DataManager
import com.motologr.data.objects.vehicle.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class VehicleImageIdUnitTests : UnitTestBase() {
    private lateinit var vehicle : Vehicle

    @Before
    fun setupTest() {
        if (!this::vehicle.isInitialized) {
            vehicle = returnDefaultVehicle()
            DataManager.createNewVehicle(vehicle)
            DataManager.setActiveVehicle(0)
            vehicle = DataManager.returnActiveVehicle()!!
        }
    }

    @Test()
    fun updateVehicleImageId_NoOverflow_0() {

        val newVehicleImageId = DataManager.changeActiveVehicleImageId(false)

        assertEquals(1, newVehicleImageId)
    }

    @Test()
    fun updateVehicleImageId_NoOverflow_1() {
        vehicle.vehicleImage = 1
        val newVehicleImageId = DataManager.changeActiveVehicleImageId(false)

        assertEquals(2, newVehicleImageId)
    }

    @Test
    fun updateVehicleImageId_Overflow() {
        vehicle.vehicleImage = 2
        val newVehicleImageId = DataManager.changeActiveVehicleImageId(false)

        assertEquals(0, newVehicleImageId)
    }

    @Test
    fun updateVehicleImageId_NoOverflow_ArtPackEnabled() {
        vehicle.vehicleImage = 2
        val newVehicleImageId = DataManager.changeActiveVehicleImageId(true)

        assertEquals(3, newVehicleImageId)
    }

    @Test
    fun updateVehicleImageId_Overflow_ArtPackEnabled() {
        vehicle.vehicleImage = 7
        val newVehicleImageId = DataManager.changeActiveVehicleImageId(true)

        assertEquals(0, newVehicleImageId)
    }
}