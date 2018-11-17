package com.nze.nzexchange.controller.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nze.nzeframework.ui.BaseActivity


abstract class NBaseActivity : BaseActivity() {

    fun skipActivity(cls: Class<*>) {
        startActivity(Intent(this, cls))
    }

    
}