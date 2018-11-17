package com.nze.nzexchange.validation

import android.content.Context
import com.nze.nzeframework.validation.ValidationExecutor

/**
 * @version ${VERSION}
 * @author: zwy
 * @类 说 明:
 * @创建时间：2018/11/17
 */
class EmptyValidation : ValidationExecutor() {
    override fun doValidate(context: Context?, text: String?): Boolean {
        return !text.isNullOrEmpty()
    }
}