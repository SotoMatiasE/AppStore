package com.example.stores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.databinding.ItemStoreBinding

class StoreAdapter (private var stores: MutableList<StoreEntity>, private var listener: OnClickListener) :
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
        val store = stores.get(position)

        with(holder){
            setListener(store)

            binding.tvName.text = store.name
            binding.cbFavorite.isChecked = store.isaFavorite
        }
    }

    override fun getItemCount(): Int = stores.size


    fun setStores(stores: MutableList<StoreEntity>) {
        //sustituimos el arreglo
        this.stores = stores
        notifyDataSetChanged()
    }

    fun add(storeEntity: StoreEntity) {
        stores.add(storeEntity)
        notifyDataSetChanged()//REFRESCA LA VISTA DEL ADAPTADOR PARA VISUALIZAR ELEMENTO AGREGADO
    }

    fun update(storeEntity: StoreEntity) {
        //averiguar index de la store
        val index =  stores.indexOf(storeEntity)
        if (index != -1) {
            stores.set(index, storeEntity)
            notifyItemChanged(index)
        }
    }
    fun delete(storeEntity: StoreEntity) {
        //averiguar index de la store
        val index =  stores.indexOf(storeEntity)
        if (index != -1) {
            stores.removeAt(index)
            notifyItemRemoved(index)
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemStoreBinding.bind(view) //vinculacion del item_store con StoreAdapter

        //litenner para vincular ViewHolder con los eventos de cada componente
        fun setListener(storeEntity: StoreEntity){
            with(binding.root) {
                setOnClickListener {listener.onClick(storeEntity)}

                setOnLongClickListener {
                    listener.onDeleteStore(storeEntity)
                    true
                }
            }
            binding.cbFavorite.setOnClickListener {
                listener.onFavoriteStore(storeEntity)
            }
        }
    }
}