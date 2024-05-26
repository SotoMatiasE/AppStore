package com.example.stores

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    //esto pertenece al ciclo de vida del fragment siempre va despues del View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //conseguir la actividad del fragment y casterarla como MainActivity
        mActivity = activity as? MainActivity
        //indicar flecha de restroseso
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //configurar nombre de la action bar
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

        //acceso al menu
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            //accedemos a setDisplayHomeAsUpEnabled
            android.R.id.home -> {
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                true
            }
            R.id.action_save -> {
                val store = StoreEntity(
                    name = mBinding.edName.text.toString().trim(),
                    phone = mBinding.edPhone.text.toString().trim(),
                    webSite = mBinding.edWebSite.text.toString().trim())

                val queue = LinkedBlockingQueue<Long?>()
                Thread{
                    val id = StoreApplication.database.storeDao().addStore(store)
                    queue.add(id)
                    mActivity?.addStore(store)
                    hideKeyboard()
                }.start()
                queue.take()?.let{
                    Snackbar.make(mBinding.root,
                        getString(R.string.edit_store_message_sucsess),
                        Snackbar.LENGTH_SHORT).show()

                    mActivity?.onBackPressedDispatcher?.onBackPressed()
                }
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //se oculta teclado
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        //se desvincula la vista
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        //se oculta
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)//desvinculamos el fragment

        //acceder a elementos de actividad desde un fragment
        super.onDestroy()
    }
}
















