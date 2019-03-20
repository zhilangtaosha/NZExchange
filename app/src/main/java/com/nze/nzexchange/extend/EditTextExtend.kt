package com.nze.nzexchange.extend

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/11
 */

fun EditText.getContent(): String = text.toString().trim()

fun EditText.hidePassword() {
    transformationMethod = HideReturnsTransformationMethod.getInstance()
    setSelection(getContent().length)
}

fun EditText.showPassword() {
    transformationMethod = PasswordTransformationMethod.getInstance()
    setSelection(getContent().length)
}