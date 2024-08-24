package com.motologr

import com.motologr.data.DataManager
import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleImageIdUnitTests : UnitTestBase() {
    @Test()
    fun updateVehicleImageId_NoOverflow_0() {
        val vehicle = returnDefaultVehicle()
        DataManager.CreateNewVehicle(vehicle)
        DataManager.SetActiveVehicle(0)

        val newVehicleImageId = DataManager.changeActiveVehicleImageId()

        assertEquals(1, newVehicleImageId)
    }

    @Test()
    fun updateVehicleImageId_NoOverflow_1() {
        val vehicle = returnDefaultVehicle()
        DataManager.CreateNewVehicle(vehicle)
        DataManager.SetActiveVehicle(0)
        DataManager.ReturnActiveVehicle()!!.vehicleImage = 1

        val newVehicleImageId = DataManager.changeActiveVehicleImageId()

        assertEquals(2, newVehicleImageId)
    }

    @Test
    fun updateVehicleImageId_Overflow() {
        val vehicle = returnDefaultVehicle()
        vehicle.vehicleImage = 2
        DataManager.CreateNewVehicle(vehicle)
        DataManager.SetActiveVehicle(0)

        val newVehicleImageId = DataManager.changeActiveVehicleImageId()

        assertEquals(0, newVehicleImageId)
    }
}