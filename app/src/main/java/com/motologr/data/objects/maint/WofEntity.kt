package com.motologr.data.objects.maint

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.motologr.data.objects.vehicle.VehicleEntity
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Wof",
    foreignKeys =
    [ForeignKey(entity = VehicleEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("vehicleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)])
data class WofEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "wofDate") val wofDate: Date,
    @ColumnInfo(name = "wofCompletedDate") val wofCompletedDate: Date,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int,
    @ColumnInfo(name = "wofProvider") val wofProvider: String)
{
    fun convertToWofObject() : Wof {
        val wof = Wof(wofDate, wofCompletedDate, price, vehicleId, wofProvider)
        wof.id = id
        return wof
    }
}

@Dao
interface WofDao {
    @Query("SELECT * FROM Wof")
    fun getAll(): List<WofEntity>

    @Query("SELECT * FROM Wof WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<WofEntity>

    @Insert
    fun insert(vararg service: WofEntity)

    @Delete
    fun delete(service: WofEntity)
}