package com.motologr.ui.data

import android.widget.DatePicker
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.motologr.MainActivity
import com.motologr.R
import com.motologr.ui.data.objects.vehicle.Vehicle
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date

fun DatePicker.getDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    return calendar.time
}

fun Calendar.toCalendar(date: Date) : Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

object DataManager {

    fun updateTitle(activity: FragmentActivity?, newTitle: String) {
        val toolbar = activity?.findViewById(R.id.toolbar) as Toolbar
        toolbar.title = newTitle
    }

    fun roundOffDecimal(number: Double): String {
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number)
    }

    private var vehicleArray = ArrayList<Vehicle>()
    fun CreateNewVehicle(brandName: String, modelName: String, year: Int, expiryWOF: Date, regExpiry: Date, odometer: Int) {
        val newVehicle = Vehicle(FetchIdForVehicle(), brandName, modelName, year, expiryWOF, regExpiry, odometer)
        vehicleArray.add(newVehicle)

        PostVehicleToDb(newVehicle)
    }

    fun CreateNewVehicle(newVehicle: Vehicle) {
        vehicleArray.add(newVehicle)

        PostVehicleToDb(newVehicle)
    }

    fun pullVehicleFromDb(newVehicle: Vehicle) {
        vehicleArray.add(newVehicle)
    }

    fun PostVehicleToDb(newVehicle: Vehicle) {
        Thread {
            MainActivity.getDatabase()
                ?.vehicleDao()
                ?.insert(newVehicle.convertToVehicleEntity())
        }.start()
    }

    fun ReturnVehicle(index : Int): Vehicle? {
        if (vehicleArray.lastIndex >= index) {
            return vehicleArray[index]
        }

        return null
    }

    fun isVehicles(): Boolean {
        return vehicleArray.isNotEmpty()
    }

    fun ReturnVehicleById(id : Int): Vehicle? {
        val vehicle: Vehicle? = vehicleArray.find { v -> v.id == id }

        return vehicle
    }

    private var activeVehicle : Int = -1

    fun SetActiveVehicle(int: Int) {
        activeVehicle = int;
    }

    fun setLatestVehicleActive() {
        SetActiveVehicle(vehicleArray.lastIndex)
    }

    fun ReturnActiveVehicle(): Vehicle? {
        if (vehicleArray.lastIndex >= activeVehicle && activeVehicle >= 0) {
            return vehicleArray[activeVehicle]
        }

        return null
    }

    fun ReturnVehicleArrayLength() : Int {
        return vehicleArray.size
    }

    private var idCounterLoggable: Int = 0

    fun setIdCounterLoggable() {
        Thread {
            val maxId = MainActivity.getDatabase()
                ?.loggableDao()
                ?.getMaxId()

            if (maxId != null)
                idCounterLoggable = maxId + 1
        }.start()
    }

    fun FetchIdForLoggable() : Int {
        idCounterLoggable += 1

        return (idCounterLoggable - 1)
    }

    private var idCounterVehicle: Int = 0

    fun setIdCounterVehicle() {
        Thread {
            val maxId = MainActivity.getDatabase()
                ?.vehicleDao()
                ?.getMaxId()

            if (maxId != null)
                idCounterVehicle = maxId + 1
        }.start()
    }

    fun FetchIdForVehicle() : Int {
        idCounterVehicle += 1

        return (idCounterVehicle - 1)
    }

    private var idCounterInsurance: Int = 0

    fun setIdCounterInsurance() {
        Thread {
            val maxId = MainActivity.getDatabase()
                ?.insuranceDao()
                ?.getMaxId()

            if (maxId != null)
                idCounterInsurance = maxId + 1
        }.start()
    }

    fun fetchIdForInsurance() : Int {
        idCounterInsurance += 1

        return (idCounterInsurance - 1)
    }
}