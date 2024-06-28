package com.motologr.ui.data.objects.maint

import com.motologr.ui.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

class Service(var serviceType: Int, var price: BigDecimal, var serviceDate: Date, var serviceProvider: String, var comment: String) : Loggable(serviceDate, 1, price) {
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
}