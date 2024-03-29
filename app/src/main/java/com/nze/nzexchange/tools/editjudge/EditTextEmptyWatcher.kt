package com.nze.nzexchange.tools.editjudge

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:监控输入框，如果为空则输入0
 * @创建时间：2019/5/7
 */
class EditTextEmptyWatcher : TextWatcher {
    lateinit var editText: EditText

    constructor(editText: EditText) {
        this.editText = editText
    }


    override fun afterTextChanged(s: Editable?) {
        judgeNumber(s!!, editText)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    fun judgeNumber(edt: Editable, editText: EditText) {
        val temp = edt.toString()
        val posDot = temp.indexOf(".")//返回指定字符在此字符串中第一次出现处的索引
        val index = editText.selectionStart//获取光标位置
        if (temp.length <= 0) {
            edt.append("0")
        }

    }

}