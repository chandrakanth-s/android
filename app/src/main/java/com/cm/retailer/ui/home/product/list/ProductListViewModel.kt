package com.cm.retailer.ui.home.product.list

import android.arch.lifecycle.ViewModel
import com.cm.retailer.data.Product
import com.cm.retailer.ui.home.customer.ItemActionListener
import io.realm.Realm


class ProductListViewModel : ViewModel() {

    var itemActionListener: ItemActionListener? = null

    fun getStock(): List<Product>{
        var realm = Realm.getDefaultInstance()
        var results = realm.where(Product::class.java).findAll()
        var stock = realm.copyFromRealm(results)
        return stock
    }
}
