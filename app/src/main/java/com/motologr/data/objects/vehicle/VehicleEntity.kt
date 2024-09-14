package com.motologr.data.objects.vehicle

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
    @ColumnInfo(name = "odometer") val odometer: Int,
    @ColumnInfo(name = "vehicleImage") val vehicleImage: Int,
    @ColumnInfo(name = "isUseRoadUserCharges") val isUseRoadUserCharges: Boolean,
    @ColumnInfo(name = "roadUserChargesHeld") val roadUserChargesHeld: Int)
{
    fun convertToVehicleObject() : Vehicle {
        val vehicle = Vehicle(id, brandName, modelName, year, expiryWOF, regExpiry, odometer)
        vehicle.isUseRoadUserCharges = isUseRoadUserCharges
        vehicle.roadUserChargesHeld = roadUserChargesHeld
        vehicle.vehicleImage = vehicleImage
        return vehicle
    }
}

@Dao
interface VehicleDao {
    @Query("SELECT * FROM Vehicle WHERE id = (SELECT MAX(id) FROM Vehicle)")
    fun getMaxId(): Int

    @Query("SELECT * FROM Vehicle")
    fun getAll(): List<VehicleEntity>

    @Query("UPDATE Vehicle SET vehicleImage = :newVehicleImageId WHERE id = :vehicleId")
    fun updateVehicleImage(newVehicleImageId: Int, vehicleId: Int)

    @Query("UPDATE Vehicle SET expiryWOF = :expiryWOF, regExpiry = :regExpiry WHERE id = :vehicleId")
    fun updateVehicleCompliance(expiryWOF: Date, regExpiry : Date, vehicleId: Int)

    @Query("UPDATE Vehicle SET isUseRoadUserCharges = :isUseRoadUserCharges, roadUserChargesHeld = :roadUserChargesHeld WHERE id = :vehicleId")
    fun updateVehicleRUCs(isUseRoadUserCharges: Boolean, roadUserChargesHeld : Int, vehicleId: Int)


    @Insert
    fun insert(vararg vehicle: VehicleEntity)

    @Delete
    fun delete(vehicle: VehicleEntity)
}