package com.cm.retailer.ui.home.product

import android.databinding.ObservableBoolean
import com.cm.retailer.extensions.observable.ObservableString

sealed class AddProductStateModel(open val name: ObservableString?=null,
                                  open val code: ObservableString? = null,
                                  open val quantity: ObservableString? = null,
                                  open val price: ObservableString?= null,
                                  open val description: ObservableString?=null,
                                  open val units: ObservableString?=null) {



    internal data class Data(override val name: ObservableString,
                             override val code: ObservableString,
                             override val quantity: ObservableString,
                             override val price: ObservableString,
                             override val description: ObservableString,
                             override val units: ObservableString,
                             val productId: String?="") : AddProductStateModel(){

        var isFormValid = ObservableBoolean(false)

    }


}