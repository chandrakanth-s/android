package com.cm.retailer.ui.home.customer

import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import com.cm.retailer.extensions.observable.ObservableString
import com.cm.retailer.ui.home.customer.products.ProductChoiceViewModel

sealed class CustomerDetailsStateModel(open val name: ObservableString?=null,
                                       open val mobileNumber: ObservableString? = null,
                                       open val choices: ObservableList<ProductChoiceViewModel>? = null,
                                       open val address: ObservableString? =null) {




    internal data class Data(override val name: ObservableString,
                             override val mobileNumber: ObservableString,
                             override val choices: ObservableList<ProductChoiceViewModel>,
                             override val address: ObservableString) : CustomerDetailsStateModel(){

        var isFormValid = ObservableBoolean(false)

    }

    internal data class RecordAdded(val recordAdded: Boolean?=true) : CustomerDetailsStateModel()

    internal data class Error(val message: String) : CustomerDetailsStateModel()

}