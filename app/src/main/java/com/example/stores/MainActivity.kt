package com.example.stores

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mbinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mbinding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mbinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mbinding.edName.text.toString().trim())

            Thread{ //esto hace que corra en otro hilo/segundo plano
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }

        setupRecyclerView()
    }


    //Funcion para recyclerView
    private fun setupRecyclerView() {
        //pasamos un arreglo vacio "mutableListOf" y este context
        mAdapter = StoreAdapter(mutableListOf(), this)
        //pide el contexto donde crearse y la cantidad de columnas
        mGridLayout = GridLayoutManager(this, 2)


        getStores()

        //Configuracion del RECYCLERVIEW
        mbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    //mostrar todos los datos/stores
    private fun getStores(){
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>() //queue significa fila o cola
        Thread{
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()

        mAdapter.setStores(queue.take())
    }


    /*
    * OnClickListener llamamos al obj con su funcion
    * */
    override fun onClick(storeEntity: StoreEntity) {

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isaFavorite = !storeEntity.isaFavorite //actualizacion de vista

        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        mAdapter.update(queue.take())
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            queue.add(storeEntity)
        }.start()
        mAdapter.delete(queue.take())
    }
}