package com.motologr.ui.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.motologr.ui.data.objects.LoggableDao
import com.motologr.ui.data.objects.LoggableEntity
import com.motologr.ui.data.objects.fuel.FuelEntity
import com.motologr.ui.data.objects.fuel.FuelDao
import com.motologr.ui.data.objects.maint.RepairDao
import com.motologr.ui.data.objects.maint.RepairEntity
import com.motologr.ui.data.objects.maint.ServiceDao
import com.motologr.ui.data.objects.maint.ServiceEntity
import com.motologr.ui.data.objects.maint.Wof
import com.motologr.ui.data.objects.maint.WofDao
import com.motologr.ui.data.objects.maint.WofEntity
import com.motologr.ui.data.objects.reg.RegDao
import com.motologr.ui.data.objects.reg.RegEntity
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

@Database(entities = [FuelEntity::class,
    LoggableEntity::class,
    VehicleEntity::class,
    RegEntity::class,
    WofEntity::class,
    RepairEntity::class,
    ServiceEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fuelDao(): FuelDao
    abstract fun loggableDao(): LoggableDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun repairDao(): RepairDao
    abstract fun serviceDao(): ServiceDao
    abstract fun regDao(): RegDao
    abstract fun wofDao(): WofDao
}