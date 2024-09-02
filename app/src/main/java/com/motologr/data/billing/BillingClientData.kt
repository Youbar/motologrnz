package com.motologr.data.billing

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

class BillingClientData(val productId: String, var isAcknowledged : Boolean, var isRefunded : Boolean, var purchaseToken : String) {
    fun convertToAddonEntity() : AddonEntity {
        val addonEntity = AddonEntity(productId, isAcknowledged, isRefunded, purchaseToken)
        return addonEntity
    }
}

@Entity(tableName = "Addons")
data class AddonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "productId") val productId: String,
    @ColumnInfo(name = "isAcknowledged") val isAcknowledged: Boolean,
    @ColumnInfo(name = "isRefunded") val isRefunded: Boolean,
    @ColumnInfo(name = "purchaseToken") val purchaseToken: String)
{
    constructor(productId: String, isAcknowledged: Boolean, isRefunded: Boolean, purchaseToken: String) : this(0, productId, isAcknowledged, isRefunded, purchaseToken)

    fun convertToAddonObject() : BillingClientData {
        val addon = BillingClientData(productId, isAcknowledged, isRefunded, purchaseToken)
        return addon
    }
}

@Dao
interface AddonDao {
    @Query("SELECT * FROM Addons")
    fun getAll(): List<AddonEntity>

    @Query("SELECT * FROM Addons WHERE productId == :productId")
    fun getAllByProductId(productId : String): List<AddonEntity>

    @Query("UPDATE Addons SET isAcknowledged = 1 WHERE productId == :productId")
    fun acknowledgePurchase(productId: String)

    @Query("UPDATE Addons SET isRefunded = 1 WHERE productId == :productId AND purchaseToken == :purchaseToken")
    fun setRefunded(productId: String, purchaseToken: String)

    @Insert
    fun insert(vararg addon: AddonEntity)

    @Delete
    fun delete(addon: AddonEntity)
}