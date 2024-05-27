package com.example.stores

import androidx.room.Entity
import androidx.room.PrimaryKey

//Convertir data clas a enditad es decir en una tabla
//SI LAS VARIABLES NO SE INICIALIZAN SON CAMPOS REQUERIDOS
@Entity(tableName = "StoreEntity")
data class StoreEntity(@PrimaryKey(autoGenerate = true)
                       var id: Long = 0,
                       var name: String,
                       var phone: String,
                       var webSite: String = "",
                       var photoUrl: String,
                       var isaFavorite: Boolean = false)
