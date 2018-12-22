package com.cm.retailer.extensions.binding.models

import android.databinding.BindingAdapter
import android.graphics.Color
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner

/**
 * Binder for databinding
 */
object ViewDataBinder {

    @JvmStatic
    @BindingAdapter("spinnerEnable")
    fun setSpinnerEnable(spinner: Spinner, enable: Boolean?) {
        if (null != enable) {
            spinner.isEnabled = enable
            spinner.isClickable = enable
        }
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginStart")
    fun setStartMargin(view: View, startMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(startMargin.toInt(), layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("tint")
    fun setTint(view: ImageView, color: String) {
        view.setColorFilter(Color.parseColor(color), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginTop")
    fun setTopMargin(view: View, topMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, topMargin.toInt(), layoutParams.rightMargin, layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginEnd")
    fun setEndMargin(view: View, endMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, endMargin.toInt(), layoutParams.bottomMargin)
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("android:layout_marginBottom")
    fun setBottomMargin(view: View, bottomMargin: Float) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, bottomMargin.toInt())
        view.layoutParams = layoutParams
    }

    @JvmStatic
    @BindingAdapter("android:layout_weight")
    fun setLayoutWeight(view: View, weight: Float?) {
        if (weight == null) {
            return
        }
        (view.layoutParams as? LinearLayout.LayoutParams)?.run {
            this.weight = weight
            view.layoutParams = this
        }
    }

    @JvmStatic
    @BindingAdapter("visibility")
    fun setVisibility(view: View, visibility: Boolean?) {
        if (null != visibility && visibility) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter("invisibility")
    fun setInVisibility(view: View, visibility: Boolean?) {
        if (null != visibility && visibility) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("colorResId")
    fun setColorResId(view: View, colorId: Int?) {
        if (null == colorId) {
            view.setBackgroundColor(Color.TRANSPARENT)
        } else {
            view.setBackgroundResource(colorId)
        }
    }

    @JvmStatic
    @BindingAdapter("android:onClick")
    fun setupClickListener(view: View, clickListener: View.OnClickListener?) {
        view.setOnClickListener(clickListener)
    }


}