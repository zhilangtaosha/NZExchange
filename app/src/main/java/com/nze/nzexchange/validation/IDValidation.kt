package com.nze.nzexchange.validation

import android.content.Context
import android.widget.Toast
import com.nze.nzeframework.validation.ValidationExecutor
import java.util.regex.Pattern

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/13
 */
class IDValidation : ValidationExecutor() {
    override fun doValidate(context: Context?, text: String?): Boolean {
        val pattern1 = "(^\\d{15}\$)|(^\\d{18}\$)|(^\\d{17}(\\d|X|x)\$)"
        val r1 = Pattern.compile(pattern1).matcher(text).find()
        if (!r1) {
            Toast.makeText(context, "身份证填写错误", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}