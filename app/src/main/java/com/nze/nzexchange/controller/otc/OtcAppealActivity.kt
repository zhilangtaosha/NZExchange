package com.nze.nzexchange.controller.otc

import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.widget.basepopup.BasePopupWindow
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.CommonListPopup
import com.nze.nzexchange.validation.EmptyValidation
import com.nze.nzexchange.widget.CommonButton
import kotlinx.android.synthetic.main.activity_otc_appeal.*

/**
 * OTC申诉页面
 */
class OtcAppealActivity : NBaseActivity() {
    val causeTv: TextView by lazy { tv_cause_aoa }
    val reasonEt: EditText by lazy { et_reason_aoa }
    val submitBtn: CommonButton by lazy { btn_submit_aoa }
    private val causeList: MutableList<String> = mutableListOf<String>("对方未付款", "对方未放行", "对方无应答", "对方有欺诈行为", "其它")
    private val causePopup: CommonListPopup by lazy {
        CommonListPopup(this).apply {
            addAllItem(causeList)
            setOnItemClick { position, item ->
                causeTv.text = item
            }
        }
    }

    override fun getRootView(): Int = R.layout.activity_otc_appeal

    override fun initView() {

        causeTv.setOnClickListener {
            causePopup.showPopupWindow()
        }

        submitBtn.initValidator()
                .add(reasonEt, EmptyValidation())
                .executeValidator()
                .setOnCommonClick {
                    if (submitBtn.validate()) {
                        showLoad()
                        Handler().postDelayed({
                            dismissLoad()
                            showToast("提交成功")
                            finish()
                        }, 2000)
                    }
                }

    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

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
