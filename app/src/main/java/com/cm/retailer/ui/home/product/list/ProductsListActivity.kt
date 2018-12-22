package com.cm.retailer.ui.home.product.list

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableList
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.cm.retailer.R
import com.cm.retailer.data.Product
import com.cm.retailer.data.ProductInfo
import com.cm.retailer.databinding.ActivityProductsListBinding
import com.cm.retailer.extensions.observable.OptimizedObservableArrayList
import com.cm.retailer.ui.global.base.adapter.BaseActivity
import com.cm.retailer.ui.home.customer.ItemActionListener
import com.cm.retailer.ui.home.notifications.StockAdapter
import com.cm.retailer.ui.home.product.AddProductActivity
import java.io.Serializable

class ProductsListActivity : BaseActivity(){

    var viewModel = ProductListViewModel()
    lateinit var binding : ActivityProductsListBinding
    lateinit var adapter: StockAdapter
    var stockList: ObservableList<Product> = OptimizedObservableArrayList()
    var selectProducts = false
    var selectedProducts:ObservableList<ProductInfo> = OptimizedObservableArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_products_list)
        binding.viewModel = viewModel
        binding.executePendingBindings()

        var args = intent.extras
        if(args!=null){
            selectProducts = args.getBoolean("SELECT_PRODUCTS",false)
            var i = args.getSerializable("SELECTED_PRODUCTS")
            if(i!=null){
                selectedProducts.addAll(args.getSerializable("SELECTED_PRODUCTS") as ArrayList<ProductInfo>)
            }
        }

        initToolbar()
        initRecyclerView()

        viewModel.itemActionListener = object : ItemActionListener{

            override fun onClick(element: Any) {
                if(selectProducts){
                    (element as Product).isSelected = !element.isSelected
                    adapter.notifyDataSetChanged()
                    invalidateOptionsMenu()
                }else{
                    var intent = Intent(this@ProductsListActivity,AddProductActivity::class.java)
                    intent.putExtra("PRODUCT_ID",(element as Product).id)
                    startActivityForResult(intent,1)
                }
            }
        }
    }

    private fun initToolbar(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(if(selectProducts) getString(R.string.select_product) else getString(R.string.products))
    }

    private fun initRecyclerView(){
        adapter = StockAdapter(viewModel)
        stockList.clear()
        stockList.addAll(viewModel.getStock())
        if(selectedProducts!=null && selectedProducts.isNotEmpty()){
            stockList.forEach {product ->
                var v = selectedProducts.filter { it.productId == product.id }.toList()
                product.isSelected = v!=null && v.isNotEmpty()
            }
        }
        adapter.updateData(stockList)
        binding.rcvProducts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcvProducts.adapter = adapter
        binding.rcvProducts.setItemViewCacheSize(30)
        binding.rcvProducts.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.stock, menu)
        var filtered = stockList.filter { it.isSelected }
        menu.findItem(R.id.action_done).isVisible = (filtered.isNotEmpty())
        menu.findItem(R.id.action_add_product).isVisible = !selectProducts
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_product -> {
                startActivityForResult(Intent(this@ProductsListActivity, AddProductActivity::class.java),1)
                return true
            }
            R.id.action_done -> {
                selectedProducts.clear()
                if(stockList.isNotEmpty()){
                    var filtered = stockList.filter { it.isSelected }
                    filtered.forEach {
                        selectedProducts.add(ProductInfo(it.productName,it.kind,it.price,it.id))
                    }
                    var args = Bundle()
                    args.putSerializable("PRODUCTS_LIST",selectedProducts as Serializable)
                    var intent = Intent()
                    intent.putExtras(args)
                    setResult(Activity.RESULT_OK,intent)
                }
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            initRecyclerView()
        }
    }

}