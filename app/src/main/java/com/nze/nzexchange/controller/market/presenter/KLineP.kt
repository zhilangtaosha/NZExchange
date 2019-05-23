package com.nze.nzexchange.controller.market.presenter

import android.view.View
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzeframework.ui.BaseActivityP
import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.bean.UserBean
import com.nze.nzexchange.config.EventCode
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.http.HRetrofit
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import org.greenrobot.eventbus.EventBus

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/21
 */
class KLineP(activity: NBaseActivity) : BaseActivityP(activity) {


    fun optionalHandler(userBean: UserBean, pairBean: TransactionPairsBean, rs: (optional: Int) -> Unit) {
        if (pairBean.optional != 1) {
            addOptional(pairBean.id, userBean?.userId!!)
                    .compose(activity.netTfWithDialog())
                    .subscribe({
                        pairBean.optional = 1
                        EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELF_SELECT, pairBean))
                        activity.showToast(it.message)
                        rs.invoke(1)
                    }, {
                        pairBean.optional = 0
                        rs.invoke(0)
                    })
        } else {
            deleteOptional(pairBean.id, userBean?.userId!!)
                    .compose(activity.netTfWithDialog())
                    .subscribe({
                        activity.showToast(it.message)
                        pairBean.optional = 0
                        EventBus.getDefault().post(EventCenter<TransactionPairsBean>(EventCode.CODE_SELF_SELECT, pairBean))
                        rs.invoke(0)
                    }, {
                        pairBean.optional = 1
                        rs.invoke(1)
                    })
        }
    }

    //添加自选
    private fun addOptional(currencyId: String, userId: String): Flowable<Result<String>> {
        return Flowable.defer {
            NRetrofit.instance
                    .bibiService()
                    .addOptional(currencyId, userId)
        }
    }

    //删除自选
    private fun deleteOptional(currencyId: String, userId: String): Flowable<Result<String>> {
        return Flowable.defer {
            NRetrofit.instance
                    .bibiService()
                    .deleteOptional(currencyId, userId)
        }
    }
}