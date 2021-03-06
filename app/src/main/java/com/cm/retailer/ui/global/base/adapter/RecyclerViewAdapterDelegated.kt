package com.cm.retailer.ui.global.base.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate
import com.hannesdorfmann.adapterdelegates3.AdapterDelegatesManager

abstract class RecyclerViewAdapterDelegated<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val delegatesManager = AdapterDelegatesManager<List<T>>()

    protected open var dataList: List<T> = emptyList()

    protected fun addDelegate(delegate: AdapterDelegate<List<T>>) {
        delegatesManager.addDelegate(delegate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegatesManager.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(dataList, position, holder)
    }

    override fun getItemViewType(position: Int): Int =
        delegatesManager.getItemViewType(dataList, position)

    override fun getItemCount(): Int = dataList.size

}