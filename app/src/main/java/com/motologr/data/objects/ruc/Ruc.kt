package com.motologr.data.objects.ruc

import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Ruc(var purchaseDate: Date,
          var unitsPurchased: Int,
          var unitsHeldAfterTransaction: Int,
          var price: BigDecimal,
          override var vehicleId: Int,
          var isHistorical : Boolean = false) : Loggable(purchaseDate, 4, price, vehicleId) {

    fun convertToRucEntity() : RucEntity {
        val rucEntity = RucEntity(id, purchaseDate, unitsPurchased, unitsHeldAfterTransaction, price, vehicleId, isHistorical)
        return rucEntity
    }

    companion object {
        var calculateRucPriceLambda : (Int) -> BigDecimal = {
            units -> calculateRucPrice(units)
        }

        fun calculateRucPrice(numberOfUnitsInput : Int): BigDecimal {
            if (numberOfUnitsInput.toString().isEmpty())
                return 0.0.toBigDecimal()

            if (numberOfUnitsInput == 0)
                return 0.0.toBigDecimal()

            val numberOfUnits = numberOfUnitsInput.toBigDecimal()
            // 38 for EV
            val unitPrice = 76.0.toBigDecimal()
            val adminFee = 12.44.toBigDecimal()

            return numberOfUnits.multiply(unitPrice).add(adminFee)
        }
    }
}