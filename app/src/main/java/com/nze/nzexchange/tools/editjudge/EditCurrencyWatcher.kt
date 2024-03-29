package com.nze.nzexchange.tools.editjudge

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:监控数字货币输入框，只能输入8位小数
 * @创建时间：2019/5/7
 */
class EditCurrencyWatcher : TextWatcher {
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

        if (temp.indexOf(".")==0) {//第一位是小数点
            edt.delete(0, 1)
            return
        }
        if (posDot > 0 && temp.length - posDot - 1 > 8)//如果包含小数点
        {
            edt.delete(index - 1, index);//删除光标前的字符
            return;
        }
    }

}