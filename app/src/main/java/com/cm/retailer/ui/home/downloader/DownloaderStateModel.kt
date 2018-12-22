package com.cm.retailer.ui.home.downloader

import android.databinding.ObservableList
import com.cm.retailer.data.Purchases
import com.cm.retailer.extensions.observable.ObservableString

sealed class DownloaderStateModel(open val fromDate: ObservableString?=null,
                                  open val toDate: ObservableString?=null,
                                  open val entries: ObservableList<Purchases>?=null) {

    internal data class Data(override val fromDate: ObservableString,
                             override val toDate: ObservableString,
                             override val entries: ObservableList<Purchases>) : DownloaderStateModel(){

    }

    internal data class Error(val message: String) : DownloaderStateModel()

}