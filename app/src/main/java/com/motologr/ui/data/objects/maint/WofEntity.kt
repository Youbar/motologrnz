package com.motologr.ui.data.objects.maint

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Wof")
data class WofEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "wofDate") val wofDate: Date,
    @ColumnInfo(name = "wofCompletedDate") val wofCompletedDate: Date,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToWofObject() : Wof {
        val wof = Wof(wofDate, wofCompletedDate, price, vehicleId)
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