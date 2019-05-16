package com.nze.nzexchange.controller.my.authentication.presenter

import com.nze.nzeframework.ui.BaseActivityPresenter
import com.nze.nzeframework.ui.OnErrorRs
import com.nze.nzeframework.ui.OnSuccessRs
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.my.paymethod.presenter.PayMethodView

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/16
 */
class AuthenticationP(activity: NBaseActivity, mIView: AuthenticationView) : BaseActivityPresenter<AuthenticationView>(activity, mIView) {

    fun getAuthentication(userBean: UserBean, onSuccess: OnSuccessRs<RealNameAuthenticationBean>, onError: OnErrorRs) {
        RealNameAuthenticationBean.getReanNameAuthentication(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, userBean.tokenReqVo.tokenSystreeId)
                .compose(getAct().netTfWithDialog())
                .subscribe(onSuccess, onError)
    }

}

interface AuthenticationView {

}