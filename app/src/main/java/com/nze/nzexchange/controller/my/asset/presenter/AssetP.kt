package com.nze.nzexchange.controller.my.asset.presenter

import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.RealNameAuthenticationBean
import com.nze.nzexchange.bean.UserAssetBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.common.AuthorityDialog
import com.nze.nzexchange.extend.OnErrorRs
import com.nze.nzexchange.extend.OnSuccessRs

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/21
 */
class AssetP(act: NBaseActivity) : BaseActivityP(act) {

    fun getReanNameAuthentication(userBean: UserBean, onSuccessRs: OnSuccessRs<RealNameAuthenticationBean>, onErrorRs: OnErrorRs) {
        RealNameAuthenticationBean.getReanNameAuthentication(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, userBean.tokenReqVo.tokenSystreeId)
                .compose(activity.netTfWithDialog())
                .subscribe({
                    onSuccessRs?.invoke(it)
                }, onErrorRs)
    }

    fun getOtcAsset(userBean: UserBean, onSuccessRs: OnSuccessRs<MutableList<UserAssetBean>>, onErrorRs: OnErrorRs) {
        UserAssetBean.getUserAssets(userBean?.userId!!, userBean!!.tokenReqVo.tokenUserId, userBean!!.tokenReqVo.tokenUserKey)
                .compose(activity.netTfWithDialog())
                .subscribe(onSuccessRs, onErrorRs)
    }
}