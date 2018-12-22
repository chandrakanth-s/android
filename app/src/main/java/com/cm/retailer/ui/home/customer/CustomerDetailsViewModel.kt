package com.cm.retailer.ui.home.customer

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.text.TextUtils
import com.cm.retailer.Utils
import com.cm.retailer.data.Customer
import com.cm.retailer.data.Product
import com.cm.retailer.data.ProductInfo
import com.cm.retailer.data.Purchases
import com.cm.retailer.extensions.binding.toObservable
import com.cm.retailer.extensions.observable.ObservableString
import com.cm.retailer.extensions.observable.OptimizedObservableArrayList
import com.cm.retailer.ui.home.customer.products.ProductChoiceViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.realm.Realm
import java.util.*
import kotlin.collections.ArrayList


class CustomerDetailsViewModel : ViewModel() {
    protected val destroyDisposables: CompositeDisposable = CompositeDisposable()
    internal val TAG = this.javaClass.canonicalName

    var state = ObservableField<CustomerDetailsStateModel>()
    var isFormValid = ObservableBoolean(false)
    var totalAmount = ObservableString("0.0")
    val selectedProducts:ArrayList<ProductInfo>? = ArrayList()

    init {

        state.set(CustomerDetailsStateModel.Data(
                ObservableString(""),
                ObservableString(""),
                OptimizedObservableArrayList(),
                ObservableString("")
        ))

        state.toObservable()
                .filter { it is CustomerDetailsStateModel.Data }
                .map { it as CustomerDetailsStateModel.Data }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    var condition1 = state
                            .toObservable()
                            .switchMap {state ->
                                if (state is CustomerDetailsStateModel.Data) {
                                    state.name.data.toObservable().map { it.isNotBlank() }
                                } else {
                                    Observable.just(false)
                                }
                            }

                    var condition2 = state
                            .toObservable()
                            .switchMap {state ->
                                if (state is CustomerDetailsStateModel.Data) {
                                    state.mobileNumber.data.toObservable().map { it.isNotBlank() }
                                } else {
                                    Observable.just(false)
                                }
                            }

                    val condition3 = state.toObservable()
                            .switchMap {
                                if (it is CustomerDetailsStateModel.Data) {
                                    val validations = it.choices.map {
                                        it.validate()
                                    }
                                    Observable.combineLatest(validations) { t ->
                                        t.fold(true) { acc, any -> acc && (any as kotlin.Boolean) }
                                    }
                                } else {
                                    Observable.just(false)
                                }
                            }

                    var condition4 = state
                            .toObservable()
                            .switchMap {state ->
                                if (state is CustomerDetailsStateModel.Data) {
                                    state.address.data.toObservable().map { it.isNotBlank() }
                                } else {
                                    Observable.just(false)
                                }
                            }

                    val result1 = Observable.combineLatest(condition1,
                            condition2,
                            BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })

                    val result2 = Observable.combineLatest(condition3,
                            result1,
                            BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })

                    Observable.combineLatest(condition4,
                            result2,
                            BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })
                            .subscribe {
                                isFormValid.set(it)
                                var currentState = state.get()
                                if(it && currentState is CustomerDetailsStateModel.Data){
                                    currentState.choices.toObservable()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe { var total = 0.0
                                                it.forEach {choice->
                                                    if(TextUtils.isDigitsOnly(choice.units.data.get()) && !TextUtils.isEmpty(choice.units.data.get())){
                                                        total += Utils.amountNormalize(Utils.calculate(choice.units.data.get(),choice.priceForKind)).toDouble()
                                                    }
                                                }
                                                totalAmount.data.set(total.toString())
                                            }
                                }
                            }
                            .addTo(destroyDisposables)
                }
                .addTo(destroyDisposables)


    }


    fun removeChoice() {
        val currentState = state.get()
        if (currentState is CustomerDetailsStateModel.Data) {
            if(currentState.choices.size > 1){
                val newChoice = currentState.choices.toMutableList()
                newChoice.removeAt(currentState.choices.size - 1)
                state.set(currentState.copy(choices = OptimizedObservableArrayList(newChoice)))
            }
        }
    }

    fun saveDetails(){
        var currentState = state.get()
        if(currentState is CustomerDetailsStateModel.Data){
            var realmInstance = Realm.getDefaultInstance()

            var existingCustomer = realmInstance.where(Customer::class.java).equalTo("mobileNumber",currentState.mobileNumber.get()).findFirst()

            if(existingCustomer!=null){
                state.set(CustomerDetailsStateModel.Error("User with these details already exists"))
                return
            }

            var customer = Customer()
            customer.name = currentState.name.get()
            customer.mobileNumber = currentState.mobileNumber.get()
            customer.address = currentState.address.get()
            realmInstance.executeTransaction { realm ->
                realm.insertOrUpdate(customer)
                var purchases = mutableListOf<Purchases>()
                currentState.choices.forEach {
                    var p = Purchases()
                    p.amount = it.amount.get()
                    p.name = it.name.get()
                    p.units = it.units.get()
                    p.customerId = customer.id
                    p.productId = it.id?:""
                    purchases.add(p)
                }
                realm.insert(purchases)
                state.set(CustomerDetailsStateModel.RecordAdded())
            }
        }
    }


    fun updateDetails(){
        var currentState = state.get()
        if(currentState is CustomerDetailsStateModel.Data){
            var realmInstance = Realm.getDefaultInstance()

            var existingCustomer = realmInstance.where(Customer::class.java).equalTo("mobileNumber",currentState.mobileNumber.get()).findFirst()

            if(existingCustomer==null){
                state.set(CustomerDetailsStateModel.Error("User does not exists with these details"))
                return
            }

            val now = Calendar.getInstance()
            now.set(Calendar.HOUR, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            now.set(Calendar.HOUR_OF_DAY, 0)
            val end = Calendar.getInstance()
            val rows = realmInstance.where(Purchases::class.java).equalTo("customerId",existingCustomer.id).and().between("createdDate",now.time,end.time).findAll()

            realmInstance.executeTransaction { realm ->
                rows.deleteAllFromRealm()
                var purchases = mutableListOf<Purchases>()
                currentState.choices.forEach {
                    var p = Purchases()
                    p.amount = it.amount.get()
                    p.name = it.name.get()
                    p.units = it.units.get()
                    p.customerId = existingCustomer.id
                    p.productId = it.id!!
                    purchases.add(p)
                }
                realm.insert(purchases)
                state.set(CustomerDetailsStateModel.RecordAdded())
            }
        }
    }

    fun productsSelected(products: ArrayList<ProductInfo>){
        this.selectedProducts?.clear()
        this.selectedProducts?.addAll(products)
        var currentState = state.get()
        if(currentState is CustomerDetailsStateModel.Data){
            var selectedChoices = currentState.choices

            products.forEach {product ->
                var filtered = selectedChoices.filter { it.id == product.productId}.toList()
                if(filtered!=null && filtered.isNotEmpty()){

                }else{
                    selectedChoices.add(ProductChoiceViewModel(product.name,"1",product.amount,priceForKind = product.amount,kind = product.kind,id = product.productId))
                }
            }

            var a = selectedProducts?.map { it.productId }?.toList()
            if(a!=null){
                selectedChoices.removeAll(selectedChoices.filter { !(a?.contains(it.id)) })
            }
            state.set(currentState.copy(choices = selectedChoices))
        }
    }

    fun viewCustomerSales(customerId: String){
        var realm = Realm.getDefaultInstance()
        var customer = realm.where(Customer::class.java).equalTo("id",customerId).findFirst()
        var currentState = state.get()
        if(currentState is CustomerDetailsStateModel.Data){
            if(customer!=null){
                var purchasesEntries = realm.where(Purchases::class.java)
                        .equalTo("customerId",customerId)
                        .between("createdDate", Utils.getCurrentDayStart(), Utils.getCurrentDate()).findAll()
                var products : ObservableList<ProductChoiceViewModel> = OptimizedObservableArrayList()
                var index = 0
                purchasesEntries.forEach {
                    var productItem = realm.where(Product::class.java).equalTo("id",it.productId).findFirst()
                    var p: Double
                    if(productItem!=null){
                        p = productItem.price.toDouble()
                        selectedProducts?.add(ProductInfo(it.name,productItem.kind,productItem.price,productItem.id))
                        var amtValue = p * it.units.toDouble()
                        products.add(ProductChoiceViewModel(it.name,it.units,amtValue.toString(),0, id = it.productId,kind = productItem?.kind?:"Unit",priceForKind = productItem?.price))
                        index += 1
                    }
                }
                state.set(currentState.copy(name = ObservableString(customer.name),mobileNumber = ObservableString(customer.mobileNumber), choices = products, address = ObservableString(customer.address)))
            }
        }
    }

}
