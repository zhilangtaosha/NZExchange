package com.nze.nzexchange.controller.my.asset.withdraw.presenter

import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.CurrenyWithdrawAddressBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.OnErrorRs
import com.nze.nzexchange.extend.OnSuccessRs
import com.nze.nzexchange.http.NRetrofit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/29
 */
class CurrencyWithdrawP(activity: NBaseActivity) : BaseActivityP(activity) {


    fun getCurrencyWithdrawAddress(userBean: UserBean, token: String, onSuccessRs: OnSuccessRs<MutableList<CurrenyWithdrawAddressBean>>, onErrorRs: OnErrorRs) {
        NRetrofit.instance
                .bibiService()
                .getCurrencyWithdrawAddress(userBean.userId, token)
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccessRs, onErrorRs)
    }

    fun addCurrencyWithdrawAddress(userBean: UserBean, address: String, tokenName: String, onSuccessRs: OnSuccessRs<Any>, onErrorRs: OnErrorRs) {
        NRetrofit.instance
                .bibiService()
                .addCurrencyWithdrawAddress(userBean.userId, address, tokenName, userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey)
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccessRs, onErrorRs)
    }

    fun deleteCurrencyWithdrawAddress(userBean: UserBean, bean: CurrenyWithdrawAddressBean, addressList: MutableList<CurrenyWithdrawAddressBean>, onSuccessRs: OnSuccessRs<Any>, onErrorRs: OnErrorRs) {
        NRetrofit.instance
                .bibiService()
                .deleteCurrencyWithdrawAddress(userBean.userId, bean.address, userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey)
                .map {
                    if (it.success) {
                        addressList.remove(bean)
                    }
                    it
                }
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccessRs, onErrorRs)
    }
}