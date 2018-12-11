package com.nze.nzexchange.extend

import android.widget.CheckBox
import com.jakewharton.rxbinding2.InitialValueObservable
import com.jakewharton.rxbinding2.widget.RxCompoundButton

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:RxBinding扩展
 * @创建时间：2018/12/10
 */

fun CheckBox.getCheckListener(): InitialValueObservable<Boolean> {
    return RxCompoundButton.checkedChanges(this)
}
