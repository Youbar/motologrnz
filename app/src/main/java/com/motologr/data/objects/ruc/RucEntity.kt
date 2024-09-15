package com.motologr.data.objects.ruc

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.motologr.data.objects.reg.Reg
import com.motologr.data.objects.vehicle.VehicleEntity
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Ruc",
    foreignKeys =
    [ForeignKey(entity = VehicleEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("vehicleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)])
data class RucEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "transactionDate") val transactionDate: Date,
    @ColumnInfo(name = "unitsPurchased") val unitsPurchased: Int,
    @ColumnInfo(name = "unitsHeldAfterTransaction") val unitsHeldAfterTransaction: Int,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToRucObject() : Ruc {
        val ruc = Ruc(transactionDate, unitsPurchased, unitsHeldAfterTransaction, price,vehicleId)
        ruc.id = id
        return ruc
    }
}

@Dao
interface RucDao {
    @Query("SELECT * FROM Ruc")
    fun getAll(): List<RucEntity>

    @Query("SELECT * FROM Ruc WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<RucEntity>

    @Insert
    fun insert(vararg ruc: RucEntity)

    @Delete
    fun delete(ruc: RucEntity)
}