package com.nze.nzexchange.controller.my.setting

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SelectLanguageBean
import com.nze.nzexchange.controller.main.MainActivity
import com.nze.nzexchange.tools.selectlanguage.MultiLanguageUtil
import kotlinx.android.synthetic.main.activity_select_language.*

class SelectLanguageActivity : AppCompatActivity() {
    private val listView: ListView by lazy { lv_asl }
    private val adapter: SelectLanguageAdapter by lazy { SelectLanguageAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_language)

        listView.adapter = adapter
        adapter.group = SelectLanguageBean.getList()

        listView.setOnItemClickListener { parent, view, position, id ->
            val bean = adapter.getItem(position)
            MultiLanguageUtil.getInstance().updateLanguage(bean!!.type)
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }
    }


}
