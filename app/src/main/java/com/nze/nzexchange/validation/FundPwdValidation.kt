package com.nze.nzexchange.validation

import android.content.Context
import android.widget.Toast
import com.nze.nzeframework.validation.ValidationExecutor

/**
 * @version ${VERSION}
 * @author: zwy
 * @类 说 明:资金密码规则
 * @创建时间：2018/11/17
 */
class FundPwdValidation : ValidationExecutor() {
    override fun doValidate(context: Context?, text: String?): Boolean {
        val len: Int = text?.length!!
        if (len < 6) {
            Toast.makeText(context, "资金密码必须为6位数字", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}