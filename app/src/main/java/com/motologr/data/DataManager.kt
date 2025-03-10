package com.motologr.data

import android.content.Context
import android.content.SharedPreferences
import android.widget.DatePicker
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import com.motologr.MainActivity
import com.motologr.R
import com.motologr.data.objects.vehicle.Vehicle
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

fun DatePicker.getDate(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

fun Calendar.toCalendar(date: Date) : Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

object DataManager {

    private var isInitialised = false

    fun initialiseDataManager() {
        isInitialised = true
    }

    fun isInitialised(): Boolean {
        return isInitialised
    }

    fun updateTitle(activity: FragmentActivity?, newTitle: String) {
        val toolbar: Toolbar? = activity?.findViewById(R.id.toolbar)
        if (toolbar != null)
            toolbar.title = newTitle
    }

    fun roundOffDecimal(number: Double): String {
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number)
    }

    private var vehicleArray = ArrayList<Vehicle>()
    fun createNewVehicle(
        brandName: String,
        modelName: String,
        year: Int,
        expiryWOF: Date,
        regExpiry: Date,
        odometer: Int,
    ) {
        val newVehicle =
            Vehicle(fetchIdForVehicle(), brandName, modelName, year, expiryWOF, regExpiry, odometer)
        vehicleArray.add(newVehicle)

        postVehicleToDb(newVehicle)
    }

    fun createNewVehicle(newVehicle: Vehicle) {
        vehicleArray.add(newVehicle)

        postVehicleToDb(newVehicle)
    }

    fun pullVehicleFromDb(newVehicle: Vehicle) {
        vehicleArray.add(newVehicle)
    }

    private fun postVehicleToDb(newVehicle: Vehicle) {
        Thread {
            MainActivity.getDatabase()
                ?.vehicleDao()
                ?.insert(newVehicle.convertToVehicleEntity())
        }.start()
    }

    fun returnVehicle(index: Int): Vehicle? {
        if (vehicleArray.lastIndex >= index) {
            return vehicleArray[index]
        }

        return null
    }

    fun isVehicles(): Boolean {
        return vehicleArray.isNotEmpty()
    }

    fun returnVehicleById(id: Int): Vehicle? {
        val vehicle: Vehicle? = vehicleArray.find { v -> v.id == id }

        return vehicle
    }

    private var activeVehicle: Int = -1

    fun setActiveVehicle(int: Int) {
        activeVehicle = int
    }

    fun setFirstVehicleActive() {
        if (vehicleArray.size > 0)
            setActiveVehicle(0)
    }

    fun setLatestVehicleActive() {
        setActiveVehicle(vehicleArray.lastIndex)
    }

    fun returnActiveVehicle(): Vehicle? {
        if (vehicleArray.lastIndex >= activeVehicle && activeVehicle >= 0) {
            return vehicleArray[activeVehicle]
        }

        return null
    }

    fun isActiveVehicleUsingRucs() : Boolean {
        val vehicle = returnActiveVehicle() ?: return false
        return vehicle.isUseRoadUserCharges
    }

    fun returnVehicleArrayLength(): Int {
        return vehicleArray.size
    }

    private fun changeActiveVehicleImage(newVehicleImageId: Int) {
        val activeVehicle = this.returnActiveVehicle()!!
        activeVehicle.vehicleImage = newVehicleImageId

        Thread {
            MainActivity.getDatabase()
                ?.vehicleDao()
                ?.updateVehicleImage(newVehicleImageId, activeVehicle.id)
        }.start()
    }

    fun changeActiveVehicleImageId(isArtPackEnabled: Boolean): Int {
        val currentVehicleImageId = this.returnActiveVehicle()?.vehicleImage ?: return 0

        var newVehicleImageId = 0

        if (currentVehicleImageId < 2 && !isArtPackEnabled)
            newVehicleImageId = currentVehicleImageId + 1
        else if (currentVehicleImageId < 7 && isArtPackEnabled)
            newVehicleImageId = currentVehicleImageId + 1

        changeActiveVehicleImage(newVehicleImageId)

        return newVehicleImageId
    }

    private var idCounterLoggable: Int = 0

    fun setIdCounterLoggable() {
        val maxIdForLoggable = MainActivity.getDatabase()
            ?.loggableDao()
            ?.getMaxId() ?: 0

        // A long time ago I was a lazy man and decided not to record insurance bills as loggables in the db
        // The time has come to pay for that laziness
        val maxIdForInsuranceBill = MainActivity.getDatabase()
            ?.insuranceBillDao()
            ?.getMaxId() ?: 0

        val maxId = maxOf(maxIdForLoggable, maxIdForInsuranceBill)

        idCounterLoggable = maxId + 1
    }

    fun fetchIdForLoggable(): Int {
        idCounterLoggable += 1

        return (idCounterLoggable - 1)
    }

    private var idCounterVehicle: Int = 0

    fun setIdCounterVehicle() {
        val maxId = MainActivity.getDatabase()
            ?.vehicleDao()
            ?.getMaxId()

        if (maxId != null)
            idCounterVehicle = maxId + 1
    }

    fun fetchIdForVehicle(): Int {
        idCounterVehicle += 1

        return (idCounterVehicle - 1)
    }

    private var idCounterInsurance: Int = 0

    fun setIdCounterInsurance() {
        val maxId = MainActivity.getDatabase()
            ?.insuranceDao()
            ?.getMaxId()

        if (maxId != null)
            idCounterInsurance = maxId + 1
    }

    fun fetchIdForInsurance(): Int {
        idCounterInsurance += 1

        return (idCounterInsurance - 1)
    }
}