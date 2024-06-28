package com.motologr.ui.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.motologr.ui.data.objects.LoggableDao
import com.motologr.ui.data.objects.LoggableEntity
import com.motologr.ui.data.objects.fuel.FuelEntity
import com.motologr.ui.data.objects.fuel.FuelLoggableDao
import com.motologr.ui.data.objects.vehicle.VehicleDao
import com.motologr.ui.data.objects.vehicle.VehicleEntity
import java.math.BigDecimal
import java.util.Date

public class Converters {
    @TypeConverter
    fun fromTimestamp(value : Long?) : Date? {
        if (value == null)
            return null

        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date : Date?) :  Long? {
        if (date == null)
            return null

        return date.time
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal): String {
        return value.toString()
    }

    @TypeConverter
    fun stringToBigDecimal(value: String): BigDecimal {
        return BigDecimal(value)
    }
}

@Database(entities = [FuelEntity::class, LoggableEntity::class, VehicleEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fuelLoggableDao(): FuelLoggableDao
    abstract fun loggableDao(): LoggableDao
    abstract fun vehicleDao(): VehicleDao
}