package com.motologr.ui.data.objects.insurance

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Insurance")
data class InsuranceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "insurer") val insurer: String,
    @ColumnInfo(name = "insurancePolicyStartDate") val insurancePolicyStartDate: Date,
    @ColumnInfo(name = "coverage") val coverage: Int,
    @ColumnInfo(name = "billingCycle") val billingCycle: Int,
    @ColumnInfo(name = "billing") val billing: BigDecimal,
    @ColumnInfo(name = "lastBill") val lastBill: Date,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToInsuranceObject() : Insurance {
        val insurance = Insurance(insurer, insurancePolicyStartDate, coverage, billingCycle, billing, lastBill, vehicleId)
        insurance.id = id
        return insurance
    }
}

@Dao
interface InsuranceDao {
    @Query("SELECT * FROM Insurance")
    fun getAll(): List<InsuranceEntity>

    @Query("SELECT * FROM Insurance WHERE vehicleId == :vehicleId")
    fun getAllByVehicleId(vehicleId : Int): List<InsuranceEntity>

    @Insert
    fun insert(vararg repair: InsuranceEntity)

    @Delete
    fun delete(repair: InsuranceEntity)
}

@Entity(tableName = "InsuranceBill")
data class InsuranceBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "billingDate") val billingDate: Date,
    @ColumnInfo(name = "price") val price: BigDecimal,
    @ColumnInfo(name = "insuranceId") val insuranceId: Int)
{
    constructor(billingDate: Date, price: BigDecimal, insuranceId: Int) : this(0, billingDate, price, insuranceId)

    fun convertToInsuranceBillObject() : InsuranceBill {
        val insuranceBill = InsuranceBill(billingDate, price, insuranceId)
        return insuranceBill
    }
}

@Dao
interface InsuranceBillDao {
    @Query("SELECT * FROM InsuranceBill")
    fun getAll(): List<InsuranceBillEntity>

    @Query("SELECT * FROM InsuranceBill WHERE insuranceId == :insuranceId")
    fun getAllByVehicleId(insuranceId : Int): List<InsuranceBillEntity>

    @Insert
    fun insert(vararg insuranceBill: InsuranceBillEntity)

    @Delete
    fun delete(insuranceBill: InsuranceBillEntity)
}