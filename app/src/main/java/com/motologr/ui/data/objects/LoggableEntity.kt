package com.motologr.ui.data.objects

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import com.motologr.ui.data.logging.Loggable
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = "Loggable")
data class LoggableEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "sortableDate") val sortableDate: Date,
    @ColumnInfo(name = "classId") val classId: Int,
    @ColumnInfo(name = "unitPrice") val unitPrice: BigDecimal,
    @ColumnInfo(name = "vehicleId") val vehicleId: Int)
{
    fun convertToLoggableObject() : Loggable {
        val loggable : Loggable = Loggable(sortableDate, classId, unitPrice, vehicleId)
        loggable.id = id
        return loggable
    }
}

@Dao
interface LoggableDao {
    @Query("SELECT * FROM Loggable WHERE id = (SELECT MAX(id) FROM Loggable)")
    fun getMaxId(): Int

    @Query("SELECT * FROM Loggable")
    fun getAll(): List<LoggableEntity>

    @Insert
    fun insert(vararg fuel: LoggableEntity)

    @Delete
    fun delete(fuel: LoggableEntity)
}