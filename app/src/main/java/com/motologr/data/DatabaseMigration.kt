package com.motologr.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigration {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Wof ADD COLUMN wofProvider TEXT NOT NULL DEFAULT ''")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Vehicle ADD COLUMN vehicleImage INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""CREATE TABLE Addons (
                            id INTEGER PRIMARY KEY NOT NULL,
                            productId TEXT NOT NULL,
                            isAcknowledged INTEGER NOT NULL,
                            isRefunded INTEGER NOT NULL)
                            """.trimIndent())
        }
    }

    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE Addons ADD COLUMN purchaseToken TEXT NOT NULL DEFAULT ''")
        }
    }

    // FK Constraint for Loggable Table
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_Loggable (
                    id INTEGER PRIMARY KEY NOT NULL, 
                    sortableDate INTEGER NOT NULL, 
                    classId INTEGER NOT NULL, 
                    unitPrice TEXT NOT NULL, 
                    vehicleId INTEGER NOT NULL,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_Loggable (id, sortableDate, classId, unitPrice, vehicleId) 
                SELECT id, sortableDate, classId, unitPrice, vehicleId FROM Loggable"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE Loggable")
            db.execSQL("ALTER TABLE Temp_Loggable RENAME TO Loggable")
        }
    }

    // FK Constraint for Fuel Table
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_Fuel (
                    id INTEGER PRIMARY KEY NOT NULL, 
                    fuelType INTEGER NOT NULL, 
                    price TEXT NOT NULL, 
                    litres TEXT NOT NULL,
                    purchaseDate INTEGER NOT NULL, 
                    odometerReading INTEGER NOT NULL, 
                    vehicleId INTEGER NOT NULL,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_Fuel (id, fuelType, price, litres, purchaseDate, odometerReading, vehicleId)
                SELECT id, fuelType, price, litres, purchaseDate, odometerReading, vehicleId FROM Fuel"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE Fuel")
            db.execSQL("ALTER TABLE Temp_Fuel RENAME TO Fuel")
        }
    }

    // FK Constraint for InsuranceBill Table
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_InsuranceBill (
                    id INTEGER PRIMARY KEY NOT NULL, 
                    billingDate INTEGER NOT NULL,
                    price TEXT NOT NULL, 
                    insuranceId INTEGER NOT NULL,
                    vehicleId INTEGER NOT NULL,
                    FOREIGN KEY (insuranceId) REFERENCES Insurance(id) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_InsuranceBill (id, billingDate, price, insuranceId, vehicleId)
                SELECT id, billingDate, price, insuranceId, vehicleId FROM InsuranceBill"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE InsuranceBill")
            db.execSQL("ALTER TABLE Temp_InsuranceBill RENAME TO InsuranceBill")
        }
    }

    // FK Constraint for Insurance Table
    val MIGRATION_8_9 = object : Migration(8, 9) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_Insurance (
                    id INTEGER PRIMARY KEY NOT NULL,
                    insurer TEXT NOT NULL,
                    insurancePolicyStartDate INTEGER NOT NULL,
                    coverage INTEGER NOT NULL,
                    billingCycle INTEGER NOT NULL,
                    billing TEXT NOT NULL,
                    lastBill INTEGER NOT NULL,
                    vehicleId INTEGER NOT NULL,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_Insurance (id, insurer, insurancePolicyStartDate, coverage, billingCycle,
                    billing, lastBill, vehicleId)
                SELECT id, insurer, insurancePolicyStartDate, coverage, billingCycle,
                    billing, lastBill, vehicleId FROM Insurance"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE Insurance")
            db.execSQL("ALTER TABLE Temp_Insurance RENAME TO Insurance")
        }
    }

    // FK Constraint for Repair Table
    val MIGRATION_9_10 = object : Migration(9, 10) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_Repair (
                    id INTEGER PRIMARY KEY NOT NULL,
                    repairType INTEGER NOT NULL,
                    price TEXT NOT NULL,
                    repairDate INTEGER NOT NULL,
                    repairProvider TEXT NOT NULL,
                    comment TEXT NOT NULL,
                    vehicleId INTEGER NOT NULL,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_Repair (id, repairType, price, repairDate, repairProvider, comment, vehicleId)
                SELECT id, repairType, price, repairDate, repairProvider, comment, vehicleId FROM Repair"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE Repair")
            db.execSQL("ALTER TABLE Temp_Repair RENAME TO Repair")
        }
    }

    // FK Constraint for Service Table
    val MIGRATION_10_11 = object : Migration(10, 11) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_Service (
                    id INTEGER PRIMARY KEY NOT NULL,
                    serviceType INTEGER NOT NULL,
                    price TEXT NOT NULL,
                    serviceDate INTEGER NOT NULL,
                    serviceProvider TEXT NOT NULL,
                    comment TEXT NOT NULL,
                    vehicleId INTEGER NOT NULL,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_Service (id, serviceType, price, serviceDate, serviceProvider, comment, vehicleId)
                SELECT id, serviceType, price, serviceDate, serviceProvider, comment, vehicleId FROM Service"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE Service")
            db.execSQL("ALTER TABLE Temp_Service RENAME TO Service")
        }
    }

    // FK Constraint for Reg Table
    val MIGRATION_11_12 = object : Migration(11, 12) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_Reg (
                    id INTEGER PRIMARY KEY NOT NULL,
                    newRegExpiryDate INTEGER NOT NULL,
                    regExpiryDate INTEGER NOT NULL,
                    monthsExtended INTEGER NOT NULL,
                    price TEXT NOT NULL,
                    vehicleId INTEGER NOT NULL,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_Reg (id, newRegExpiryDate, regExpiryDate, monthsExtended, price, vehicleId)
                SELECT id, newRegExpiryDate, regExpiryDate, monthsExtended, price, vehicleId FROM Reg"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE Reg")
            db.execSQL("ALTER TABLE Temp_Reg RENAME TO Reg")
        }
    }

    // FK Constraint for Wof Table
    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            val queryForDatabase = """
                CREATE TABLE IF NOT EXISTS Temp_Wof (
                    id INTEGER PRIMARY KEY NOT NULL,
                    wofDate INTEGER NOT NULL,
                    wofCompletedDate INTEGER NOT NULL,
                    price TEXT NOT NULL,
                    vehicleId INTEGER NOT NULL,
                    wofProvider TEXT NOT NULL,
                    FOREIGN KEY (vehicleId) REFERENCES Vehicle(id) ON UPDATE CASCADE ON DELETE CASCADE
                )"""
            db.execSQL(queryForDatabase)

            val queryCopyDataBase = """
                INSERT OR REPLACE INTO Temp_Wof (id, wofDate, wofCompletedDate, price, vehicleId, wofProvider)
                SELECT id, wofDate, wofCompletedDate, price, vehicleId, wofProvider FROM Wof"""

            db.execSQL(queryCopyDataBase)
            db.execSQL("DROP TABLE Wof")
            db.execSQL("ALTER TABLE Temp_Wof RENAME TO Wof")
        }
    }
}