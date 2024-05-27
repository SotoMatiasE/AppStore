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

        mBinding.fab.setOnClickListener {launchEditFragment()}

        setupRecyclerView()
    }
    private fun launchEditFragment(args: Bundle? = null) {
        //instancia del fragment
        val fragment = EditStoreFragment()
        if (args != null) fragment.arguments = args
        //gestor de que trea para controlar fragment
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.ContainerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

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
    private fun getStores() {
        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
        Thread{
            val stores = StoreApplication.database.storeDao().getAllStores()
            queue.add(stores)
        }.start()

        mAdapter.setStores(queue.take())
    }


    /*
    * OnClickListener llamamos al obj con su funcion
    * */
    override fun onClick(storeId: Long) {
        //enviar argumentos al fragment
        val args = Bundle()//se le pasan argumentos a esta variable
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isaFavorite = !storeEntity.isaFavorite //actualizacion de vista

        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        updateStore(queue.take())
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        //Elimina el item de recyclerview
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        mAdapter.delete(queue.take())
    }

    /*MainAux*/
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}










