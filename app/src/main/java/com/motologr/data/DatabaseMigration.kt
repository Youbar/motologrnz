package com.motologr.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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

// Specify FKs for tables