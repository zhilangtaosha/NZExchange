package com.nze.nzexchange.controller.otc

import com.nze.nzeframework.ui.BaseActivity
import com.nze.nzeframework.ui.BaseActivityPresenter

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/17
 */
class PublicP(activity: BaseActivity, mIView: IPublicView) : BaseActivityPresenter<PublicP.IPublicView>(activity, mIView) {

    interface IPublicView {}
}