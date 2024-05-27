package com.example.stores

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.LinkedBlockingQueue

class EditStoreFragment : Fragment() {
    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mStoreEntity: StoreEntity? = null
    private var mIsEditMode: Boolean = false
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    { mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    //esto pertenece al ciclo de vida del fragment siempre va despues del View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if (id != null && id != 0L) {
            mIsEditMode = true
            getStore(id)
        }else {
            mIsEditMode = false
            //uso de alta
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "") //inicializado
        }

        setUpActionBar()

        //Configuracion de GLIDE para imagenes con url
        setUpTextFields()
    }


    private fun setUpActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title =
            if (mIsEditMode) getString(R.string.edit_store_title_edit) else getString(R.string.edit_store_title_add)
        setHasOptionsMenu(true)
    }

 /*   private fun setUpActionBar() {
        //conseguir la actividad del fragment y casterarla como MainActivity
        mActivity = activity as? MainActivity
        //indicar flecha de restroseso
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //configurar nombre de la action bar
        mActivity?.supportActionBar?.title = if (mIsEditMode)getString(R.string.edit_store_title_edit)
                                            else getString(R.string.edit_store_title_add)

        //acceso al menu
        setHasOptionsMenu(true)
    }
*/
    private fun setUpTextFields() {
        with (mBinding){
        //validar textField en tiempo real quita el error de valores no ingresados
        edName.addTextChangedListener { validateFields(tilName) }
        edPhone.addTextChangedListener { validateFields(tilPhone) }
        edPhotoUrl.addTextChangedListener {
            validateFields(tilPhotoUrl)
            loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL) //config cache
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    private fun getStore(id: Long) {
        val queue = LinkedBlockingQueue<StoreEntity?>()
        Thread{
            mStoreEntity = StoreApplication.database.storeDao().getStoreById(id)
            queue.add(mStoreEntity)
        }.start()
        queue.take()?.let{ setUiStore(it) }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        //asigna valor editable con extenciones
        with(mBinding){
            edName.text = storeEntity.name.editable()
            edPhone.text = storeEntity.phone.editable()
            edWebSite.text = storeEntity.webSite.editable()
            edPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    //funcion de alcance
    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*val store = StoreEntity(
        name = mBinding.edName.text.toString().trim(),
        phone = mBinding.edPhone.text.toString().trim(),
        webSite = mBinding.edWebSite.text.toString().trim(),
        photoUrl = mBinding.edPhotoUrl.text.toString().trim(),)*/
        //asi la variable global funciona en caso de edicion o carga
        return when(item.itemId){
            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_save -> {
                if (mStoreEntity != null &&
                    validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)){
                    with(mStoreEntity!!) {
                        name = mBinding.edName.text.toString().trim()
                        phone = mBinding.edPhone.text.toString().trim()
                        webSite = mBinding.edWebSite.text.toString().trim()
                        photoUrl = mBinding.edPhotoUrl.text.toString().trim()
                    }

                    val queue = LinkedBlockingQueue<StoreEntity>()
                    Thread {
                        if (mIsEditMode) StoreApplication.database.storeDao().updateStore(mStoreEntity!!)
                        else mStoreEntity!!.id = StoreApplication.database.storeDao().addStore(mStoreEntity!!)
                        queue.add(mStoreEntity)
                    }.start()

                    with(queue.take()) {
                        hideKeyboard()

                        if (mIsEditMode){
                            mActivity?.updateStore(this)

                            Snackbar.make(mBinding.root,
                                R.string.edit_store_message_save_success,
                                Snackbar.LENGTH_SHORT).show()
                        } else {
                            mActivity?.addStore(this)

                            Toast.makeText(mActivity, R.string.edit_store_message_save_success, Toast.LENGTH_SHORT).show()

                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg  textFields: TextInputLayout) : Boolean {
        var isValid = true

        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                isValid = false
            }else textField.error = null //se limpian en tiempo real los errores
        }

        if (!isValid) Snackbar.make(mBinding.root, R.string.edit_store_message_valid,
                                                        Snackbar.LENGTH_SHORT).show()

        return isValid
    }

    private fun validateFields(): Boolean {
        //validamos campos requeridos
        var isValid = true
        //el primero obtiene el foco
        //validacion campo faltante marca error
        if (mBinding.edPhotoUrl.text.toString().trim().isEmpty()){
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.edPhotoUrl.requestFocus() //solisita el foco
            isValid = false
        }
        if (mBinding.edPhone.text.toString().trim().isEmpty()){
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.edPhone.requestFocus() //solisita el foco
            isValid = false
        }
        if (mBinding.edName.text.toString().trim().isEmpty()){
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.edName.requestFocus() //solisita el foco
            isValid = false
        }

        return isValid
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
















