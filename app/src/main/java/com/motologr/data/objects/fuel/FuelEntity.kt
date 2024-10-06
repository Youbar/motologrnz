package com.motologr.data.objects.fuel

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.motologr.data.objects.vehicle.VehicleEntity
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Fuel",
    foreignKeys = [ForeignKey(entity = VehicleEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("vehicleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class FuelEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "fuelType") val fuelType: Int,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "litres") val litres: BigDecimal,
    @ColumnInfo(name = "purchaseDate") val purchaseDate: Date,
    @ColumnInfo(name = "odometerReading") val odometerReading: Int,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToFuelObject() : Fuel {
        val fuel : Fuel = Fuel(fuelType, price, litres, purchaseDate, odometerReading, vehicleId)
        fuel.id = id
        return fuel
    }
}

@Dao
interface FuelDao {
    @Query("SELECT * FROM Fuel")
    fun getAll(): List<FuelEntity>

    @Query("SELECT * FROM Fuel WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<FuelEntity>

    @Update
    fun updateFuel(fuel: FuelEntity)

    @Insert
    fun insert(vararg fuel: FuelEntity)

    @Query("DELETE FROM Fuel WHERE id = :id")
    fun delete(id: Int)
}