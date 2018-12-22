package com.cm.retailer.ui.global.base.adapter

import io.scal.ambi.ui.global.base.adapter.SpinnerAdapterBase

class SpinnerAdapterSimple<T>(override val itemLayoutId: Int,
                              override val itemLayoutDropdownId: Int? = null) : SpinnerAdapterBase<T>()