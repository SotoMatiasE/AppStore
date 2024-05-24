package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter (private var store: MutableList<Store>, private var listener: OnClickListener) :
    RecyclerView.Adapter<StoreAdapter.ViewHolder>()
{
        private lateinit var mContext: Context //m SER REFIERE A QUE ES MIEMBRO DE LA CLASE, ESTANDAR BPRACT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        //inflar vista
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_store, parent, false)



        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val store = store.get(position)

        with(holder){
            setListener(store)

            bindView.tvName.text = store.name
        }
    }

    override fun getItemCount(): Int = store.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val bindView = ItemStoreBinding.bind(view) //vinculacion del item_store con StoreAdapter

        //litenner para vincular ViewHolder con los eventos de cada componente
        fun setListener(store: Store){
            bindView.root.setOnClickListener {
                listener.onClick(store)
            }
        }
    }
}