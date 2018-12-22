package com.cm.retailer.ui.home.product

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.cm.retailer.data.Kind
import com.cm.retailer.data.Product
import com.cm.retailer.extensions.binding.toObservable
import com.cm.retailer.extensions.observable.ObservableString
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.realm.Realm
import java.util.*


class AddProductViewModel : ViewModel() {
    protected val destroyDisposables: CompositeDisposable = CompositeDisposable()
    internal val TAG = this.javaClass.canonicalName

    var state = ObservableField<AddProductStateModel>()
    var isFormValid = ObservableBoolean(false)
    var kind = ObservableField<Kind>(Kind.UNIT)

    init {
        state.set(AddProductStateModel.Data(
                ObservableString(""),
                ObservableString(""),
                ObservableString(""),
                ObservableString(""),
                ObservableString(""),
                ObservableString("")
        ))

        state.toObservable()
                .filter { it is AddProductStateModel.Data }
                .map { it as AddProductStateModel.Data }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    var condition1 = state
                            .toObservable()
                            .switchMap {state ->
                                if (state is AddProductStateModel.Data) {
                                    state.name.data.toObservable().map { it.isNotBlank() }
                                } else {
                                    Observable.just(false)
                                }
                            }
                            .distinctUntilChanged()

                    var condition4 = state
                            .toObservable()
                            .switchMap {state ->
                                if (state is AddProductStateModel.Data) {
                                    state.price.data.toObservable().map { it.isNotBlank() }
                                } else {
                                    Observable.just(false)
                                }
                            }
                            .distinctUntilChanged()

                    val result2 = Observable.combineLatest(condition1,
                            condition4,
                            BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })
                            .distinctUntilChanged()
                            .subscribe {
                                isFormValid.set(it)
                            }
                            .addTo(destroyDisposables)

                }
                .addTo(destroyDisposables)


    }

    fun saveProduct(productItem: Product?=null): ObservableField<Product> {
        var status = ObservableField<Product>()
        var realm = Realm.getDefaultInstance();
        realm.beginTransaction()
        var currentState = state.get() as AddProductStateModel.Data
        if(productItem==null){
            var product = realm.createObject(Product::class.java, UUID.randomUUID().toString())
            product.productName = currentState.name.get()
            product.productCode = currentState.code.get()
            product.price = currentState.price.get()
            product.quantity = currentState.quantity.get()
            product.productSummary = currentState.description.get()
            product.setKind(kind.get())
            product.unitsOfKind = currentState.units.get()
            realm.commitTransaction()
            status.set(product)
        }else{
            productItem.productName = currentState.name.get()
            productItem.productCode = currentState.code.get()
            productItem.price = currentState.price.get()
            productItem.quantity = currentState.quantity.get()
            productItem.productSummary = currentState.description.get()
            productItem.setKind(kind.get())
            productItem.unitsOfKind = currentState.units.get()
            realm.commitTransaction()
            status.set(productItem)
        }
        return status
    }


}
