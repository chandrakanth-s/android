package com.cm.retailer

import android.text.TextUtils
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object Utils{

    @JvmStatic
    fun getformattedAmount(amount: String?): String? {
        var formattedAmount: String?
        var doubleamount: Double
        if (amount == null || amount == "null") {
            return "0"
        }
        if (amount.equals("-", ignoreCase = true)) {
            doubleamount = java.lang.Double.parseDouble(amount.substring(1,
                    amount.length))
            val formatter = DecimalFormat("#,##0.00")
            formattedAmount = formatter.format(doubleamount)
            // System.out.println(formattedAmount);
            return "-" + formattedAmount!!
        } else {
            doubleamount = java.lang.Double.parseDouble(amount)
            val formatter = DecimalFormat("#,##0.00")
            formattedAmount = formatter.format(doubleamount)
            // System.out.println(formattedAmount);
            return formattedAmount
        }
    }

    @JvmStatic
    fun getServerDateInString(time: Date): String{
        val dateFormat = DateFormat.getDateInstance()
        return dateFormat.format(time)
    }

    @JvmStatic
    fun getCurrentDayStart(): Date{
        var fD = Calendar.getInstance()
        fD.set(Calendar.HOUR, 0)
        fD.set(Calendar.MINUTE, 0)
        fD.set(Calendar.SECOND, 0)
        fD.set(Calendar.HOUR_OF_DAY, 0)
        return fD.time
    }

    @JvmStatic
    fun getCurrentDate(): Date{
        var fD = Calendar.getInstance()
        return fD.time
    }

    @JvmStatic
    fun toServerFormat(date: Date): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }

    @JvmStatic
    fun calculate(unit: String?, price: String?): String{
        if(price==null || unit == null || unit.isEmpty() || price.isEmpty() || !TextUtils.isDigitsOnly(unit) || !TextUtils.isDigitsOnly(price)){
            return "0.00"
        }
        return getformattedAmount((unit!!.toDouble() * price!!.toDouble()).toString())?:""
    }

    @JvmStatic
    fun amountNormalize(price: String?): String{
        var price = price?.replace(",","")
        return price?:"0.00"
    }



}