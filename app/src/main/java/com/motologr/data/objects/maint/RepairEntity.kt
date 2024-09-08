package com.motologr.data.objects.maint

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.motologr.data.objects.fuel.FuelEntity
import com.motologr.data.objects.vehicle.VehicleEntity
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Repair",
    foreignKeys =
    [ForeignKey(entity = VehicleEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("vehicleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)])
data class RepairEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "repairType") val repairType: Int,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "repairDate") val repairDate: Date,
    @ColumnInfo(name = "repairProvider") val repairProvider: String,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToRepairObject() : Repair {
        val repair = Repair(repairType, price, repairDate, repairProvider, comment, vehicleId)
        repair.id = id
        return repair
    }
}

@Dao
interface RepairDao {
    @Query("SELECT * FROM Repair")
    fun getAll(): List<RepairEntity>

    @Query("SELECT * FROM Repair WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<RepairEntity>

    @Insert
    fun insert(vararg repair: RepairEntity)

    @Delete
    fun delete(repair: RepairEntity)
}