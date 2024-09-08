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

@Entity(tableName = "Service",
    foreignKeys =
    [ForeignKey(entity = VehicleEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("vehicleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)])
data class ServiceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "serviceType") val serviceType: Int,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "serviceDate") val serviceDate: Date,
    @ColumnInfo(name = "serviceProvider") val serviceProvider: String,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToServiceObject() : Service {
        val service = Service(serviceType, price, serviceDate, serviceProvider, comment, vehicleId)
        service.id = id
        return service
    }
}

@Dao
interface ServiceDao {
    @Query("SELECT * FROM Service")
    fun getAll(): List<ServiceEntity>

    @Query("SELECT * FROM Service WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<ServiceEntity>

    @Insert
    fun insert(vararg service: ServiceEntity)

    @Delete
    fun delete(service: ServiceEntity)
}