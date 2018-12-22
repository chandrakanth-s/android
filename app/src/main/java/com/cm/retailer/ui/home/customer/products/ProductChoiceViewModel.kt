package com.cm.retailer.ui.home.customer.products

import android.arch.lifecycle.ViewModel
import com.cm.retailer.extensions.binding.toObservable
import com.cm.retailer.extensions.observable.ObservableString
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class ProductChoiceViewModel(name: String, units: String, amount: String, val index: Int?=0, val id: String?="", val priceForKind: String?="1", kind: String) : ViewModel() {

    val name = ObservableString(name)
    val units = ObservableString(units)
    val amount = ObservableString(amount)
    val kind = ObservableString(kind)

    fun validate(): Observable<Boolean> {

        var condition1 = name.data
                .toObservable()
                .map { it.isNotBlank() }

        var condition2 = units.data
                .toObservable()
                .map { it.isNotBlank() }

        var condition3 = amount.data
                .toObservable()
                .map { it.isNotBlank() }

        val result1 = Observable.combineLatest(condition1,
                condition2,
                BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })

        return Observable.combineLatest(result1,
                condition3,
                BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })

    }
}