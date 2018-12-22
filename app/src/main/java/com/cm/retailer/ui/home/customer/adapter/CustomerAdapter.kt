package com.cm.retailer.ui.home.notifications

import com.cm.retailer.data.Customer
import com.cm.retailer.ui.global.base.adapter.HeaderFooterList
import com.cm.retailer.ui.global.base.adapter.RecyclerViewAdapterDelegated
import com.cm.retailer.ui.home.HomeViewModel

/**
 * Created by chandra on 30-07-2018.
 */

class CustomerAdapter(viewModel: HomeViewModel) : RecyclerViewAdapterDelegated<Any>(){

    override var dataList: List<Any> = HeaderFooterList(null,null,emptyList())
    private var productsList: HeaderFooterList
        get() = dataList as HeaderFooterList
        set(value) {
            dataList = value
        }

    init {
        addDelegate(CustomerDelegate(viewModel))
        setHasStableIds(true)
        productsList.updateHeaderVisibility(true, this)
        productsList.updateFooterVisibility(false, this)
    }

    override fun getItemId(position: Int): Long {
        val item = productsList[position]
        return item.hashCode().toLong();
    }

    fun updateData(data: List<Customer>) {
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