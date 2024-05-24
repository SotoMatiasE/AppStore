package com.example.stores

import android.app.Application
import androidx.room.Room

//esta clase nos ayuda con las clases de data base
class StoreApplication : Application() {
    //patron singleton para acceder a la BD de cualquier parte de la app
    companion object{
        lateinit var database: StoreDatabase
    }

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(this,
                   StoreDatabase::class.java,
             "StoreDatabase").build()
    }

}