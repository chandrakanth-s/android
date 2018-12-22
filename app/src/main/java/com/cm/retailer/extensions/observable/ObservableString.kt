package com.cm.retailer.extensions.observable

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import java.io.Serializable

class ObservableString(data: String? = null) : Serializable{

    val data = ObservableField<String>(data)
    val enabled = ObservableBoolean(true)

    fun get(): String =
        data.get().orEmpty()
}