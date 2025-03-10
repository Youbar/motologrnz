package com.motologr.data.objects.insurance

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

@Entity(tableName = "Insurance",
    foreignKeys =
        [ForeignKey(entity = VehicleEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("vehicleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)])
data class InsuranceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "insurer") val insurer: String,
    @ColumnInfo(name = "insurancePolicyStartDate") val insurancePolicyStartDate: Date,
    @ColumnInfo(name = "coverage") val coverage: Int,
    @ColumnInfo(name = "billingCycle") val billingCycle: Int,
    @ColumnInfo(name = "billing") val billing: BigDecimal,
    @ColumnInfo(name = "lastBill") val lastBill: Date,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int,
    @ColumnInfo(name = "isCancelled") val isCancelled: Boolean,
    @ColumnInfo(name = "insurancePolicyEndDate") val insurancePolicyEndDate: Date)
{
    constructor(insurer: String, insurancePolicyStartDate: Date, coverage: Int, billingCycle: Int, billing: BigDecimal, lastBill: Date, vehicleId: Int, isCancelled : Boolean, insurancePolicyEndDate : Date)
            : this(0, insurer, insurancePolicyStartDate, coverage, billingCycle, billing, lastBill, vehicleId, isCancelled, insurancePolicyEndDate)

    fun convertToInsuranceObject() : Insurance {
        val insurance = Insurance(id, insurer, insurancePolicyStartDate, coverage, billingCycle, billing, lastBill, vehicleId)
        insurance.id = id
        insurance.isCancelled = isCancelled
        insurance.insurancePolicyEndDate = insurancePolicyEndDate
        return insurance
    }
}

@Dao
interface InsuranceDao {
    @Query("SELECT * FROM Insurance")
    fun getAll(): List<InsuranceEntity>

    @Query("SELECT * FROM Insurance WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<InsuranceEntity>

    @Query("SELECT * FROM Insurance WHERE id = (SELECT MAX(id) FROM Insurance)")
    fun getMaxId(): Int

    @Insert
    fun insert(vararg insurance: InsuranceEntity)

    @Update
    fun updateInsurance(insurance: InsuranceEntity)

    @Delete
    fun delete(insurance: InsuranceEntity)

    @Query("DELETE FROM Insurance WHERE id = :id")
    fun delete(id: Int)
}

@Entity(tableName = "InsuranceBill",
    foreignKeys =
        [ForeignKey(entity = InsuranceEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("insuranceId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ),
        ForeignKey(entity = VehicleEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("vehicleId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)]
)
data class InsuranceBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "billingDate") val billingDate: Date,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "insuranceId") val insuranceId: Int,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    constructor(billingDate: Date, price: BigDecimal, insuranceId: Int, vehicleId: Int) : this(0, billingDate, price, insuranceId, vehicleId)

    fun convertToInsuranceBillObject() : InsuranceBill {
        val insuranceBill = InsuranceBill(billingDate, price, insuranceId, vehicleId)
        insuranceBill.id = id
        return insuranceBill
    }
}

@Dao
interface InsuranceBillDao {
    @Query("SELECT id FROM InsuranceBill WHERE id = (SELECT MAX(id) FROM InsuranceBill)")
    fun getMaxId(): Int

    @Query("SELECT * FROM InsuranceBill")
    fun getAll(): List<InsuranceBillEntity>

    @Query("SELECT * FROM InsuranceBill WHERE insuranceId == :insuranceId")
    fun getAllByVehicleId(insuranceId : Int): List<InsuranceBillEntity>

    @Insert
    fun insert(vararg insuranceBill: InsuranceBillEntity)

    @Update
    fun updateInsuranceBill(insuranceBill: InsuranceBillEntity)

    @Delete
    fun delete(insuranceBill: InsuranceBillEntity)

    @Query("DELETE FROM InsuranceBill WHERE id = :id")
    fun delete(id: Int)
}