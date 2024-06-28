package com.motologr.ui.data.objects.fuel

import com.motologr.ui.data.DataManager
import com.motologr.ui.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Fuel(var fuelType: Int,
           var price: BigDecimal,
           var litres: BigDecimal,
           var purchaseDate: Date,
           var odometerReading: Int,
           override var vehicleId: Int) : Loggable(purchaseDate, 100, price, vehicleId) {

    // type 0 = 91, 1 = 95, 2 = 98, 3 = diesel
    fun returnFuelType() : String {
        return when (fuelType) {
            0 -> {
                "91 Unleaded"
            }
            1 -> {
                "95 Unleaded"
            }
            2 -> {
                "98 Unleaded"
            }
            3 -> {
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