package com.motologr.data.objects.maint

import com.motologr.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Service(var serviceType: Int,
              var price: BigDecimal,
              var serviceDate: Date,
              var serviceProvider: String,
              var comment: String,
              override var vehicleId: Int) : Loggable(serviceDate, 1, price, vehicleId) {
    fun returnServiceType() : String {
        return when (serviceType) {
            0 -> {
                "Oil Change"
            }
            1 -> {
                "General"
            }
            2 -> {
                "Full"
            }
            else -> {
                "Invalid"
            }
        }
    }

    fun convertToServiceEntity() : ServiceEntity {
        val serviceEntity = ServiceEntity(id, serviceType, price, serviceDate, serviceProvider, comment, vehicleId)
        return serviceEntity
    }
}