package com.example.stores

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding
import java.util.concurrent.LinkedBlockingQueue

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)


        /*mbinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mbinding.edName.text.toString().trim())

            Thread{ //esto hace que corra en otro hilo/segundo plano
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }*/

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }

        setupRecyclerView()
    }

    private fun launchEditFragment() {
        //instancia del fragment
            val fragment = EditStoreFragment()
        //gestor de que trea para controlar fragment
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.ContainerMain, fragment) //ContainerMain es el activitymain
            fragmentTransaction.commit()//se aplican los cambios 
            fragmentTransaction.addToBackStack(null)//podemos volver a la mainActivity
        //decide como ejecutarse

        //mBinding.fab.hide()

        hideFab()
    }


    //Funcion para recyclerView
    private fun setupRecyclerView() {
        //pasamos un arreglo vacio "mutableListOf" y este context
        mAdapter = StoreAdapter(mutableListOf(), this)
        //pide el contexto donde crearse y la cantidad de columnas
        mGridLayout = GridLayoutManager(this, 2)

        getStores()

        //Configuracion del RECYCLERVIEW
        mBinding.recyclerView.apply {
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

    /*MainAux*/
    override fun hideFab(isVisible: Boolean) {
        if (isVisible)  {
            mBinding.fab.show()
        }else{
            mBinding.fab.hide()
        }
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {

    }
}










