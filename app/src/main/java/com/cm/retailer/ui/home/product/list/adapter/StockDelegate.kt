package com.cm.retailer.ui.home.notifications
import com.cm.retailer.R
import com.cm.retailer.data.Product
import com.cm.retailer.databinding.ProductItemBinding
import com.cm.retailer.ui.home.product.list.ProductListViewModel

internal class StockDelegate(viewModel: ProductListViewModel) :
    StockListAdapterDelegateBase<ProductItemBinding>(viewModel) {

    override val layoutId: Int = R.layout.product_item

    override fun isForViewType(items: List<Any>, position: Int): Boolean {
        return items[position] is Product
    }

    override fun onBindViewHolder(items: List<Any>, position: Int, binding: ProductItemBinding, payloads: MutableList<Any>) {
        binding.element = items[position] as? Product
        super.onBindViewHolder(items, position, binding, payloads)
    }
}