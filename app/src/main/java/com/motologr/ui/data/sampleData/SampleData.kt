package com.motologr.ui.data.sampleData

import com.motologr.ui.data.DataManager
import com.motologr.ui.data.objects.maint.Repair
import com.motologr.ui.data.objects.maint.Service
import com.motologr.ui.data.objects.vehicle.Vehicle
import java.text.SimpleDateFormat

class SampleData {
    init {
        val format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

        val vehicle = Vehicle("Mazda", "323",
            1989,
            format.parse("10/09/2024"),
            format.parse("08/08/2024"),
            1243)

        DataManager.CreateNewVehicle(vehicle)

        var service: Service = Service(0, 3103.23.toBigDecimal(), format.parse("31/03/2024"), "Av", "")
        var service2: Service = Service(0, 104.23.toBigDecimal(), format.parse("1/04/2024"), "Av", "")
        var repair: Repair = Repair(0, 104.23.toBigDecimal(), format.parse("1/04/2024"), "Av", "")
        var repair2: Repair = Repair(0, 104.24.toBigDecimal(), format.parse("1/04/2025"), "Av", "")

        DataManager.ReturnVehicle(0)?.logService(service)
        DataManager.ReturnVehicle(0)?.logService(service2)
        DataManager.ReturnVehicle(0)?.logRepair(repair)
        DataManager.ReturnVehicle(0)?.logRepair(repair2)

        DataManager.SetActiveVehicle(0)
    }
}