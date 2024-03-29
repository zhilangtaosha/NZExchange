package com.nze.nzexchange.validation

import android.content.Context
import android.widget.Toast
import com.nze.nzeframework.validation.ValidationExecutor
import java.util.regex.Pattern

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/28
 */
class PasswordValidation : ValidationExecutor() {

    override fun doValidate(context: Context, text: String): Boolean {
        val len: Int = text.length
        if (len > 0) {
            if (len < 8 || len > 16) {
                Toast.makeText(context, "密码为8-16位数字和字母组合", Toast.LENGTH_SHORT).show()
                return false
            }
            val pattern = "^\\w*[a-zA-Z]+\\w*\$"
            val r = Pattern.compile(pattern).matcher(text).find()

            val pattern2 = "^\\w*[0-9]+\\w*\$"
            val r2 = Pattern.compile(pattern2).matcher(text).find()
            if (!r||!r2) {
                Toast.makeText(context, "密码为8-16位数字和字母组合", Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}