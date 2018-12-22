package com.cm.retailer.ui.home.notifications

import android.databinding.ObservableList
import com.cm.retailer.data.Product
import com.cm.retailer.ui.global.base.adapter.HeaderFooterList
import com.cm.retailer.ui.global.base.adapter.RecyclerViewAdapterDelegated
import com.cm.retailer.ui.home.product.list.ProductListViewModel

/**
 * Created by chandra on 30-07-2018.
 */

class StockAdapter(viewModel: ProductListViewModel) : RecyclerViewAdapterDelegated<Any>(){

    override var dataList: List<Any> = HeaderFooterList(null,null,emptyList())
    private var productsList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(StockDelegate(viewModel))
        setHasStableIds(true)
        productsList.updateHeaderVisibility(true, this)
        productsList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = productsList[position]
        return item.hashCode().toLong();
    }

    fun updateData(data: ObservableList<Product>) {
        releaseData()
        productsList = productsList.copy(data = data)
        notifyDataSetChanged()
    }

    fun releaseData() {
        productsList = productsList.copy(data = emptyList())
        notifyDataSetChanged()
    }

    fun showPageProgress(show: Boolean) {
        productsList.updateFooterVisibility(show, this)
    }

}