package com.motologr.ui.data.objects.reg

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Reg")
data class RegEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "newRegExpiryDate") val newRegExpiryDate: Date,
    @ColumnInfo(name = "regExpiryDate") val regExpiryDate: Date,
    @ColumnInfo(name = "monthsExtended") val monthsExtended: Int,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToWofObject() : Reg {
        val reg = Reg(newRegExpiryDate, regExpiryDate, monthsExtended, price, vehicleId)
        reg.id = id
        return reg
    }
}

@Dao
interface RegDao {
    @Query("SELECT * FROM Reg")
    fun getAll(): List<RegEntity>

    @Query("SELECT * FROM Reg WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<RegEntity>

    @Insert
    fun insert(vararg service: RegEntity)

    @Delete
    fun delete(service: RegEntity)
}