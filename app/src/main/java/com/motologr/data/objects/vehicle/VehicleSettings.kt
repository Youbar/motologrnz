package com.motologr.data.objects.vehicle

class VehicleSettings(
    var brandName: String,
    var modelName: String,
    var modelYear: Int)
{
    var isUseRoadUserCharges : Boolean = false
    var roadUserChargesHeld : Int = -1

}