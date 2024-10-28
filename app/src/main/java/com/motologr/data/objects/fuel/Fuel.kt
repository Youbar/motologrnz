package com.motologr.data.objects.fuel

import com.motologr.data.DataManager
import com.motologr.data.EnumConstants
import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Fuel(var fuelType: Int,
           var price: BigDecimal,
           var litres: BigDecimal,
           var purchaseDate: Date,
           var odometerReading: Int,
           override var vehicleId: Int) : Loggable(purchaseDate, 100, price, vehicleId) {

    fun returnFuelType() : String {
        return when (fuelType) {
            EnumConstants.FuelType.Unleaded91.ordinal -> {
                "91 Unleaded"
            }
            EnumConstants.FuelType.Unleaded95.ordinal -> {
                "95 Unleaded"
            }
            EnumConstants.FuelType.Unleaded98.ordinal -> {
                "98 Unleaded"
            }
            EnumConstants.FuelType.Diesel.ordinal -> {
                "Diesel"
            }
            else -> {
                "Invalid"
            }
        }
    }

    fun convertToFuelEntity() : FuelEntity {
        val fuelEntity = FuelEntity(id, fuelType, price, litres, purchaseDate, odometerReading, vehicleId)
        return fuelEntity
    }
}