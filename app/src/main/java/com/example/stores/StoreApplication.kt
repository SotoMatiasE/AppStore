package com.example.stores

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//esta clase nos ayuda con las clases de data base
class StoreApplication : Application() {
    //patron singleton para acceder a la BD de cualquier parte de la app
    companion object{
        lateinit var database: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //aca se altera la base de datos
                db.execSQL("ALTER TABLE StoreEntity ADD COLUMN photoUrl TEXT NOT NULL DEFAULT ''")
            }
        }

        database = Room.databaseBuilder(this,
                   StoreDatabase::class.java,
             "StoreDatabase")
            .addMigrations(MIGRATION_1_2)
            .build()
    }

}