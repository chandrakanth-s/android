package com.cm.retailer.extensions.binding.models

import android.databinding.BindingAdapter
import android.os.Build
import android.text.Html
import android.text.InputType
import android.text.TextUtils
import android.text.util.Linkify
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import java.text.DateFormat
import java.util.*


object TextViewDataBinder {


    @JvmStatic
    @BindingAdapter("inputType")
    fun bindInputType(editText: EditText, inputType: Int?) {
        if (null == inputType || -1 == inputType) {
            editText.inputType = InputType.TYPE_NULL
        } else {
            editText.inputType = inputType

            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.isCursorVisible = true
        }
    }


    @JvmStatic
    @BindingAdapter("maxLines")
    fun bindMaxLines(textView: TextView, maxLines: Int?) {
        if (null == maxLines || 0 >= maxLines) {
            textView.maxLines = -1
        } else {
            textView.maxLines = maxLines
        }
    }

    @JvmStatic
    @BindingAdapter("imeDoneClickListener")
    fun bindImeDoneClickListener(editText: EditText, imeDoneClickListener: DoneActionClickListener?) {
        if (null == imeDoneClickListener) {
            editText.setOnEditorActionListener(null)
        } else {
            val listener = TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.action == KeyEvent.ACTION_DOWN
                        && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    imeDoneClickListener.onDoneActionClicked()
                    true
                } else {
                    false
                }
            }
            editText.setOnEditorActionListener(listener)
        }
    }

    interface DoneActionClickListener {
        fun onDoneActionClicked()
    }

    @JvmStatic
    @BindingAdapter("htmlText")
    fun bindHTMLText(textview: TextView, time: String){
        if(time.toLowerCase().contains("iframe") || TextUtils.isEmpty(time)){
            textview.visibility = View.GONE
            return
        }
        var htmlText = time.replace("\n", "<br>")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textview.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            textview.text =  Html.fromHtml(htmlText)
        }
        Linkify.addLinks(textview, Linkify.ALL)
    }

    @JvmStatic
    @BindingAdapter("ddMMMyyyy")
    fun ddMMMyyyy(textview: TextView, time: Date){
        val dateFormat = DateFormat.getDateInstance()
        textview.text =  dateFormat.format(time)
    }

}