package com.motologr.data.objects.vehicle

class VehicleSettings(
    var brandName : String,
    var modelName : String,
    var modelYear : Int,
    val vehicleId : Int)
{
    var isUseRoadUserCharges : Boolean = false
    // Road User Charges Held at creation of vehicle
    var roadUserChargesHeld : Int = -1
}