package com.nze.nzexchange.controller.common

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.R
import com.nze.nzexchange.bean.Accmoney
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.tools.CopyTool
import kotlinx.android.synthetic.main.activity_show_bank_pay_method.*

class ShowBankPayMethodActivity : NBaseActivity(), View.OnClickListener {

    val closeIv: ImageView by lazy { iv_close_bank }
    val userNameTv: TextView by lazy { tv_user_name_bank }
    val bankNameTv: TextView by lazy { tv_bank_name_bank }
    val subbranchTv: TextView by lazy { tv_subbranch_bank }//支行
    val cardTv: TextView by lazy { tv_card_bank }
    val copyIv: ImageView by lazy { iv_copy_bank }
    val confirmBtn: TextView by lazy { btn_confirm_bank }
    lateinit var accmoney: Accmoney

    companion object {
        fun skip(context: Context, accmoney: Accmoney) {
            context.startActivity(Intent(context, ShowBankPayMethodActivity::class.java)
                    .putExtra(IntentConstant.PARAM_ACCOUNT, accmoney))
        }
    }

    override fun getRootView(): Int = R.layout.activity_show_bank_pay_method

    override fun initView() {
        setWindowStatusBarColor(R.color.transparent)
        intent?.let {
            accmoney = it.getParcelableExtra(IntentConstant.PARAM_ACCOUNT)
        }

        userNameTv.text = accmoney.trueName
        bankNameTv.text = accmoney.accmoneyBanktype
        subbranchTv.text = accmoney.accmoneyBank
        cardTv.text = accmoney.accmoneyBankcard

        closeIv.setOnClickListener(this)
        copyIv.setOnClickListener(this)
        confirmBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close_bank -> {
                onBackPressed()
            }
            R.id.iv_copy_bank -> {
                if (CopyTool.copy(cardTv.text.toString()))
                    showToast("复制成功")
            }
            R.id.btn_confirm_bank -> {
                onBackPressed()
            }
        }
    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.FADE

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
