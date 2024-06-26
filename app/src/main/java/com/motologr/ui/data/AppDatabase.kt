package com.motologr.ui.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}

public class Converters {
    @TypeConverter
    fun fromTimestamp(value : Long?) : Date? {
        if (value == null)
            return null

        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date : Date?) :  Long? {
        if (date == null)
            return null

        return date.time
    }
}

@Entity
@TypeConverters(Converters::class)
data class FuelLoggable(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "fuel_type") val fuelType: Int,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "litres") val litres: Double,
    @ColumnInfo(name = "purchase_date") val purchaseDate: Date,
    @ColumnInfo(name = "odometer_reading") val odometerReading: Int
)

@Database(entities = [User::class, FuelLoggable::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}