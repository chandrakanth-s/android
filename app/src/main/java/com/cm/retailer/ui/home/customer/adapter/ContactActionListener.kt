package com.cm.retailer.ui.home.customer.adapter

import com.cm.retailer.data.Customer

interface ContactActionListener{

    fun onClick(element: Customer)

    fun onSms(element: Customer)

    fun onCall(element: Customer)

}