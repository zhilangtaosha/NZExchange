package com.nze.nzexchange.controller.my.setting

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.SelectLanguageBean
import com.nze.nzexchange.controller.base.NBaseAda
import com.nze.nzexchange.tools.selectlanguage.MultiLanguageUtil
import kotlinx.android.synthetic.main.lv_select_language.view.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/7/22
 */
class SelectLanguageAdapter(mContext: Context) : NBaseAda<SelectLanguageBean, SelectLanguageAdapter.ViewHolder>(mContext) {
    override fun setLayout(): Int = R.layout.lv_select_language

    override fun createViewHold(convertView: View): ViewHolder = ViewHolder(convertView)

    override fun initView(vh: ViewHolder, item: SelectLanguageBean, position: Int) {

        vh.languageTv.text = MultiLanguageUtil.getInstance().getLanguageName(mContext, item.type)
        if (MultiLanguageUtil.getInstance().languageType == item.type) {
            vh.chooseIv.visibility = View.VISIBLE
        } else {
            vh.chooseIv.visibility = View.GONE
        }
    }

    class ViewHolder(view: View) {
        val languageTv: TextView = view.tv_language_lsl
        val chooseIv: ImageView = view.iv_choose_lsl
    }
}