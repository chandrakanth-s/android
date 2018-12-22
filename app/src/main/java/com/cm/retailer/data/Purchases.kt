package com.cm.retailer.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Purchases : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var customerId: String = ""
    var name: String = ""
    var units: String = ""
    var amount: String = ""
    var createdDate = Date()
    var modifiedDate = Date()
    var productId: String = ""
}