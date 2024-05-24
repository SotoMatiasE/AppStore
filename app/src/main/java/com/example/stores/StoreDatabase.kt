package com.example.stores

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = arrayOf(StoreEntity::class), version = 1)
abstract class StoreDatabase : RoomDatabase(){
    //configurar DAO
    abstract fun getStoreDao(): StoreDao
}