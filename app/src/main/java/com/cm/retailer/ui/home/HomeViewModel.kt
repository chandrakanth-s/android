package com.cm.retailer.ui.home

import android.arch.lifecycle.ViewModel
import com.cm.retailer.Utils
import com.cm.retailer.data.Customer
import com.cm.retailer.data.Purchases
import com.cm.retailer.ui.home.customer.adapter.ContactActionListener
import io.realm.Realm
import java.util.*

class HomeViewModel : ViewModel() {

    internal val TAG = this.javaClass.canonicalName
    var itemClickListener: ContactActionListener? = null

    fun getCustomerDetails(): List<Customer>{
        var realm = Realm.getDefaultInstance()
        var result = realm.where(Customer::class.java).findAll()
        return realm.copyFromRealm(result)
    }

    fun getBillAmount(customerId: String):  Pair<String,String>{
        var details = mutableListOf<String>()
        val now = Calendar.getInstance()
        now.set(Calendar.HOUR, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)
        now.set(Calendar.HOUR_OF_DAY, 0)
        val end = Calendar.getInstance()
        var realm = Realm.getDefaultInstance()
        var result = realm.where(Purchases::class.java)
                .equalTo("customerId",customerId)
                .between("createdDate",now.time,end.time).findAll()
        var total = 0.0
        result.forEach {
            details.add(it.name+"-"+it.units)
            total += it.units.toDouble() * it.amount.toDouble()
        }
        return Pair(Utils.getformattedAmount(total.toString())?:"0.00",details.joinToString())
    }

}