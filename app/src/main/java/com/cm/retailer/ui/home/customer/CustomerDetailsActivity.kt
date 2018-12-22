package com.cm.retailer.ui.home.customer

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.cm.retailer.R
import com.cm.retailer.data.Product
import com.cm.retailer.data.ProductInfo
import com.cm.retailer.databinding.ActivityCustomerDetailsBinding
import com.cm.retailer.extensions.binding.toObservable
import com.cm.retailer.ui.global.base.adapter.BaseActivity
import com.cm.retailer.ui.home.downloader.DownloaderActivity
import com.cm.retailer.ui.home.product.AddProductActivity
import com.cm.retailer.ui.home.product.list.ProductsListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.realm.Realm


class CustomerDetailsActivity : BaseActivity(){

    var enableMenu = false
    var viewModel = CustomerDetailsViewModel()
    protected val destroyDisposables: CompositeDisposable = CompositeDisposable()
    lateinit var binding: ActivityCustomerDetailsBinding
    var customerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_details)
        binding.viewModel = viewModel
        binding.executePendingBindings()

        initToolbar()
        initObservers()

        var args = intent.extras
        if(args!=null){
            customerId = args.getString("CUSTOMER_ID")
            binding.llCustomerDetails.visibility = View.GONE
            supportActionBar?.title = getString(R.string.details)
            viewModel.viewCustomerSales(customerId!!)
        }

        binding.productActionListener = object: ChoiceActionListener{

            override fun removeChoice() {
                viewModel.removeChoice()
            }

            override fun addChoice() {
                var count = Realm.getDefaultInstance().where(Product::class.java).count()
                if(count > 0){
                    var intent = Intent(this@CustomerDetailsActivity,ProductsListActivity::class.java)
                    intent.putExtra("SELECT_PRODUCTS",true)
                    intent.putExtra("SELECTED_PRODUCTS",viewModel.selectedProducts)
                    startActivityForResult(intent,1)
                }else{
                    var intent = Intent(this@CustomerDetailsActivity,AddProductActivity::class.java)
                    intent.putExtra("CUSTOMER_DETAILS_ADD_PRODUCT",true)
                    startActivityForResult(intent,2)
                }
            }
        }
    }

    private fun initToolbar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(getString(R.string.add_customer))
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

        viewModel.state
                .toObservable()
                .filter { it is CustomerDetailsStateModel.RecordAdded }
                .map { it as CustomerDetailsStateModel.RecordAdded }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addTo(destroyDisposables)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.customer_purchases, menu)
        menu.findItem(R.id.action_save).isVisible = (enableMenu)
        menu.findItem(R.id.action_download).isVisible = (customerId!=null)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                if(customerId==null){
                    viewModel.saveDetails()
                }else{
                    viewModel.updateDetails()
                }
                return true
            }
            R.id.action_download -> {
                var intent = Intent(this@CustomerDetailsActivity,DownloaderActivity::class.java)
                intent.putExtra("CUSTOMER_ID",customerId)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK){
            if(requestCode == 1){
                var result = data?.getSerializableExtra("PRODUCTS_LIST") as ArrayList<ProductInfo>
                viewModel.productsSelected(result)
            }else{
                var result = data?.getSerializableExtra("PRODUCT") as ProductInfo
                var list = ArrayList<ProductInfo>()
                list.add(result)
                viewModel.productsSelected(list)
            }
        }
    }

}