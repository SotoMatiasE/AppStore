package com.example.stores

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mbinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mbinding.root)

        mbinding.btnSave.setOnClickListener {
            val store = Store(name = mbinding.edName.text.toString().trim())
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

        //Configuracion del RECYCLERVIEW
        mbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }

    }


    /*
    * OnClickListener llamamos al obj con su funcion
    * */
    override fun onClick(store: Store) {

    }
}