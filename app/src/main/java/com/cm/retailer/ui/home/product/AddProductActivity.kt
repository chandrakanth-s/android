package com.cm.retailer.ui.home.product

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.cm.retailer.R
import com.cm.retailer.data.Kind
import com.cm.retailer.data.Product
import com.cm.retailer.data.ProductInfo
import com.cm.retailer.databinding.ActivityAddProductBinding
import com.cm.retailer.extensions.binding.toObservable
import com.cm.retailer.extensions.observable.ObservableString
import com.cm.retailer.ui.global.base.adapter.BaseActivity
import com.cm.retailer.ui.global.base.adapter.SpinnerAdapterSimple
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.realm.Realm
import java.util.*


class AddProductActivity : BaseActivity(){

    var enableMenu = false
    var viewModel = AddProductViewModel()
    protected val destroyDisposables: CompositeDisposable = CompositeDisposable()
    lateinit var binding: ActivityAddProductBinding
    var productItem: Product?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product)
        binding.viewModel = viewModel
        binding.executePendingBindings()

        initToolbar()
        initObservers()
        initPollEndsSpinner()

        var args = intent.extras
        if(args!=null){
            var productId = args.getString("PRODUCT_ID","")
            if(productId.isNotEmpty()){
                var currentState = viewModel.state.get() as AddProductStateModel.Data

                productItem = Realm.getDefaultInstance().where(Product::class.java).equalTo("id",productId).findFirst()

                if(productItem!=null){
                    viewModel.state.set(currentState.copy(
                            name = ObservableString(productItem?.productName),
                            units = ObservableString(productItem?.quantity),
                            price = ObservableString(productItem?.price),
                            quantity = ObservableString(productItem?.unitsOfKind),
                            productId = productItem?.id
                    ))
                    var index = 0
                    Kind.values().forEach {
                        if(it.value == productItem?.kind){
                            System.out.println("---------------------> KIND="+it.value + " -- " + index)
                            viewModel.kind.set(it)
                            binding.sAsUser.setSelection(Kind.values().indexOf(it))
                            index += 1
                        }
                    }
                }
            }
        }
    }

    private fun initToolbar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(getString(R.string.add_product))
    }

    private fun initObservers(){
        viewModel.isFormValid
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    enableMenu = it
                    invalidateOptionsMenu()
                }
                .addTo(destroyDisposables)
    }

    private fun initPollEndsSpinner() {
        val pollEndsAdapter = SpinnerAdapterSimple<Kind>(R.layout.item_kind_spinner,R.layout.item_kind_spinner_dropdown)
        var list = EnumSet.allOf(Kind::class.java).toList()
        pollEndsAdapter.updateData(list)
        binding.sAsUser.adapter = pollEndsAdapter
        binding.sAsUser.setSelection(0)

        binding.sAsUser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.kind.set(list.get(position))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.add_product, menu)
        menu.findItem(R.id.action_save).isEnabled = enableMenu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                viewModel.saveProduct(productItem)
                        .toObservable()
                        .subscribe {
                            var args = Bundle()
                            args.putSerializable("PRODUCT",ProductInfo(it.productName,it.kind,it.price,it.id))
                            var intent = Intent()
                            intent.putExtras(args)
                            setResult(Activity.RESULT_OK,intent)
                            finish()
                        }

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }


}