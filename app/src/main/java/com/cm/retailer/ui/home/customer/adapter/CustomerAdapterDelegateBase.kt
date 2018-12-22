package com.cm.retailer.ui.home.notifications

import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper
import com.cm.retailer.BR
import com.cm.retailer.ui.global.base.adapter.AdapterDelegateBase
import com.cm.retailer.ui.home.HomeViewModel

internal abstract class CustomerAdapterDelegateBase<in Binding : ViewDataBinding>(private val viewModel: HomeViewModel) :
    AdapterDelegateBase<Binding, List<Any>>() {

    @CallSuper
    override fun onBindViewHolder(items: List<Any>, position: Int, binding: Binding, payloads: MutableList<Any>) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}