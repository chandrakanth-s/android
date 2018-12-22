package com.cm.retailer.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Customer : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = ""
    var mobileNumber: String = ""
    var address: String = ""
    var createdDate = Date()
    var modifiedDate = Date()
}