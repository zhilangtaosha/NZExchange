package com.nze.nzexchange.controller.my.asset.presenter

import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.*
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.extend.OnErrorRs
import com.nze.nzexchange.extend.OnSuccessRs
import com.nze.nzexchange.http.CRetrofit

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/20
 */
class LegalP(activity: NBaseActivity) : BaseActivityP(activity) {

    /**
     * 法币充值记录
     */
    fun getRechargeHistory(
            userBean: UserBean,
            page: Int,
            pageSize: Int,
            onSuccess: OnSuccessRs<MutableList<LegalRechargeHistoryBean>>,
            onError: OnErrorRs
    ) {
        CRetrofit.instance
                .userService()
                .getLegalRechargeHistory(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, page, pageSize)
                .compose(activity.netTf())
                .subscribe(onSuccess, onError)
    }

    /**
     * 法币提现记录
     */
    fun getWithdrawHistory(
            userBean: UserBean,
            page: Int,
            pageSize: Int,
            onSuccess: OnSuccessRs<MutableList<LegalWithdrawHistoryBean>>,
            onError: OnErrorRs
    ) {
        CRetrofit.instance
                .userService()
                .getLegalWithdrawHistory(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, page, pageSize)
                .compose(activity.netTf())
                .subscribe(onSuccess, onError)
    }

    //获取法币账户信息
    fun getLegalAccountInfo(userBean: UserBean, onSuccess: OnSuccessRs<LegalAccountBean>, onError: OnErrorRs) {
        CRetrofit.instance
                .userService()
                .getLegalAccountInfo(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey)
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccess, onError)
    }

    //获取公司收款账号
    fun getCompanyPaymethod(successRs: OnSuccessRs<MutableList<CompanyPaymentBean>>, errorRs: OnErrorRs) {
        CompanyPaymentBean.getCompanyPaymethod()
                .compose(activity.netTfWithDialog())
                .subscribe(successRs, errorRs)
    }
}