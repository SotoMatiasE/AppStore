package com.example.stores

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getAllStores() : MutableList<StoreEntity>

    //devuelve el item segun id
    @Query( "SELECT * FROM StoreEntity WHERE id = :id")
    fun getStoreById(id: Long) : StoreEntity

    //CRUD
    @Insert
    fun addStore(storeEntity: StoreEntity) : Long

    @Update
    fun updateStore(storeEntity: StoreEntity)

     @Delete
     fun deleteStore(storeEntity: StoreEntity)
}