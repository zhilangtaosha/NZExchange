package com.nze.nzexchange.widget

import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.jakewharton.rxbinding2.view.RxView
import com.nze.nzeframework.validation.EditTextValidator
import com.nze.nzeframework.validation.ValidationExecutor
import com.nze.nzeframework.validation.ValidationModel
import com.nze.nzexchange.R
import com.nze.nzexchange.validation.EmptyValidation
import java.util.concurrent.TimeUnit

class CommonButton(context: Context, attrs: AttributeSet?) : Button(context, attrs) {
    var validator: EditTextValidator? = null
//    var onCommonClick: OnCommonButtonClick? = null
//
//    interface OnCommonButtonClick {
//        fun onClick(view: View);
//    }

    init {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        if (Build.VERSION.SDK_INT > 15) {
            background = ContextCompat.getDrawable(getContext(), R.drawable.selector_common_btn_bg)
        } else {
            setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.selector_common_btn_bg))
        }

        setTextColor(ContextCompat.getColor(context, R.color.selector_common_btn_text))

//        setOnClickListener {
//            RxView.clicks(this)
//                    .throttleFirst(2, TimeUnit.SECONDS)
//                    .subscribe {
//                        onCommonClick?.onClick(this)
//                    }
//        }


    }

    fun setOnCommonClick(click: View.OnClickListener) {
        RxView.clicks(this)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe {
                    click.onClick(this)
                }
    }

    fun initValidator(): CommonButton {
        validator = EditTextValidator(context).setButton(this)
        return this
    }

    fun add(editText: EditText, validationExecutor: ValidationExecutor = EmptyValidation()): CommonButton {
        validator?.add(ValidationModel(editText, validationExecutor))
        return this
    }

    fun executeValidator(): CommonButton {
        validator?.execute()
        return this
    }

    fun validate(): Boolean = validator?.validate()!!
}
