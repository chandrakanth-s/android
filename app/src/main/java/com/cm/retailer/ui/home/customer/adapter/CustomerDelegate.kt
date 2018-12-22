package com.cm.retailer.ui.home.notifications
import com.cm.retailer.R
import com.cm.retailer.data.Customer
import com.cm.retailer.databinding.ItemCustomerBinding
import com.cm.retailer.ui.home.HomeViewModel

internal class CustomerDelegate(viewModel: HomeViewModel) :
    CustomerAdapterDelegateBase<ItemCustomerBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_customer

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is Customer
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ItemCustomerBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? Customer
        super.onBindViewHolder(items, position, binding, payloads)
    }
}