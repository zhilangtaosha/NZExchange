package com.nze.nzexchange.controller.otc

import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzeframework.ui.BaseActivityPresenter
import com.nze.nzexchange.controller.base.NBaseActivity

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/17
 */
class PublicP(activity: NBaseActivity, mIView: IPublicView) : BaseActivityPresenter<PublicP.IPublicView>(activity, mIView) {

    fun publishNet() {

    }


    interface IPublicView {
        fun onPublishSuccess()
        fun onPublishFail()
    }
}