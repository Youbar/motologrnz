package com.motologr.ui.data.objects.vehicle

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.Date

@Entity(tableName = "Vehicle")
data class VehicleEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "brandName") val brandName: String,
    @ColumnInfo(name = "modelName") val modelName: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "expiryWOF") val expiryWOF: Date,
    @ColumnInfo(name = "regExpiry") val regExpiry: Date,
    @ColumnInfo(name = "odometer") val odometer: Int)
{
    fun convertToVehicleObject() : Vehicle {
        val vehicle = Vehicle(id, brandName, modelName, year, expiryWOF, regExpiry, odometer)
        return vehicle
    }
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM Loggable WHERE id = (SELECT MAX(id) FROM Vehicle)")
    fun getMaxId(): Int

    @Query("SELECT * FROM Vehicle")
    fun getAll(): List<VehicleEntity>

    @Insert
    fun insert(vararg vehicle: VehicleEntity)

    @Delete
    fun delete(vehicle: VehicleEntity)
}