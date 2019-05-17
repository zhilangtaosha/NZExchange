package com.nze.nzexchange.controller.my.paymethod.presenter

import com.nze.nzeframework.ui.BaseActivityPresenter
import com.nze.nzeframework.ui.OnErrorRs
import com.nze.nzeframework.ui.OnSuccessRs
import com.nze.nzexchange.bean.SetPayMethodBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/16
 */
class PayMethodPresenter(activity: NBaseActivity, mIView: PayMethodView) : BaseActivityPresenter<PayMethodView>(activity, mIView) {

    /**
     * 设置付款方式
     */
    fun addPayMethod(
            userBean: UserBean,
            onSuccess: OnSuccessRs<SetPayMethodBean>,
            onError: OnErrorRs,
            accmoneyBank: String? = null,
            accmoneyBankcard: String? = null,
            accmoneyBanktype: String? = null,
            accmoneyWeixinurl: String? = null,
            accmoneyWeixinacc: String? = null,
            accmoneyZfburl: String? = null,
            accmoneyZfbacc: String? = null,
            curBuspwUcode: String? = null,
            accmoneyBpaySn: String? = null,
            accmoneyBpayAccount: String? = null,
            accmoneyFrBank: String? = null,
            accmoneyFrBankcard: String? = null,
            accmoneyFrAccount: String? = null
    ) {
        SetPayMethodBean.setPayMethodNet(userBean.tokenReqVo.tokenUserId,
                userBean.tokenReqVo.tokenUserKey,
                accmoneyBank,
                accmoneyBankcard,
                accmoneyBanktype,
                accmoneyWeixinurl,
                accmoneyWeixinacc,
                accmoneyZfburl,
                accmoneyZfbacc,
                curBuspwUcode,
                accmoneyBpaySn,
                accmoneyBpayAccount,
                accmoneyFrBank,
                accmoneyFrBankcard,
                accmoneyFrAccount)
                .compose(getAct().netTfWithDialog())
                .subscribe(onSuccess, onError)
    }

    /**
     * 获取所有付款方式
     * 通过该方法可以判断哪些支付方式没有设置，哪些已经设置
     */
    fun getAllPayMethod(userBean: UserBean, onSuccessRs: OnSuccessRs<SetPayMethodBean>, onError: OnErrorRs) {
        SetPayMethodBean.getPayMethodNet(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, userBean.tokenReqVo.tokenSystreeId)
                .compose(getAct().netTfWithDialog())
                .subscribe(onSuccessRs,onError)
    }
}

interface PayMethodView {

}