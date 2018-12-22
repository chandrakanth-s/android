package com.cm.retailer.ui.home.downloader

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.cm.retailer.Utils
import com.cm.retailer.data.Purchases
import com.cm.retailer.extensions.binding.toObservable
import com.cm.retailer.extensions.observable.ObservableString
import com.cm.retailer.extensions.observable.OptimizedObservableArrayList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.realm.Realm
import io.realm.RealmResults
import java.util.*


class DownloaderViewModel : ViewModel() {
    internal val TAG = this.javaClass.canonicalName
    var state = ObservableField<DownloaderStateModel>()
    var fromDateCalendar:Date
    var toDateCalendar:Date
    var customerId: String? = null
    protected val destroyDisposables: CompositeDisposable = CompositeDisposable()
    var isFormValid = ObservableBoolean(false)

    init {
        fromDateCalendar = Utils.getCurrentDayStart()
        toDateCalendar = Utils.getCurrentDate()

        /*state.set(DownloaderStateModel.Data(
                ObservableString(Utils.toServerFormat(fromDateCalendar)),
                ObservableString(Utils.toServerFormat(toDateCalendar)),
                OptimizedObservableArrayList(emptyList())))*/

        state.set(DownloaderStateModel.Data(
                ObservableString(""),
                ObservableString(""),
                OptimizedObservableArrayList(emptyList())))


        state.toObservable()
                .filter { it is DownloaderStateModel.Data }
                .map { it as DownloaderStateModel.Data }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    var condition1 = state
                            .toObservable()
                            .switchMap {state ->
                                if (state is DownloaderStateModel.Data) {
                                    state.fromDate.data.toObservable().map { it.isNotBlank() }
                                } else {
                                    Observable.just(false)
                                }
                            }

                    var condition2 = state
                            .toObservable()
                            .switchMap {state ->
                                if (state is DownloaderStateModel.Data) {
                                    state.toDate.data.toObservable().map { it.isNotBlank() }
                                } else {
                                    Observable.just(false)
                                }
                            }

                    Observable.combineLatest(condition1,
                            condition2,
                            BiFunction<Boolean, Boolean, Boolean> { t1, t2 -> t1 && t2 })
                            .subscribe {
                                isFormValid.set(it)
                            }
                            .addTo(destroyDisposables)
                }
                .addTo(destroyDisposables)

    }

    fun fetchRecords(){
        var realm = Realm.getDefaultInstance()
        var results: RealmResults<Purchases>? = null
        if(customerId == null){
            results = realm.where(Purchases::class.java).between("createdDate",fromDateCalendar,toDateCalendar).findAll()
        }else{
            results = realm.where(Purchases::class.java).equalTo("customerId",customerId).between("createdDate",fromDateCalendar,toDateCalendar).findAll()
        }
        var currentState = state.get() as DownloaderStateModel.Data
        if(results.isNotEmpty()){
            state.set(currentState.copy(entries = OptimizedObservableArrayList(realm.copyFromRealm(results))))
        }else{
            state.set(currentState.copy(entries = OptimizedObservableArrayList()))
        }
    }

}
