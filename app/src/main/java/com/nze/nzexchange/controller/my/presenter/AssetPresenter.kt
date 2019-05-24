package com.nze.nzexchange.controller.my.presenter

import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.CurrencyWithdrawInfoBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.OnErrorRs
import com.nze.nzexchange.extend.OnSuccessRs

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/24
 */
class AssetPresenter(activity: NBaseActivity) : BaseActivityP(activity) {

    fun currencyWithdrawAndRechargeInfo(currency: String, onSuccessRs: OnSuccessRs<CurrencyWithdrawInfoBean>, onErrorRs: OnErrorRs) {
        CurrencyWithdrawInfoBean.getCurrencyWithdrawInfo(currency)
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccessRs, onErrorRs)
    }
}