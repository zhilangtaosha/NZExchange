package com.nze.nzexchange.http

import com.nze.nzexchange.bean.Result
import com.nze.nzexchange.bean.UserBean
import io.reactivex.Flowable
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/13
 */
class CommonRequest {

    companion object {

        fun busCheck(
                userBean: UserBean,
                busflowTag: String
        ): Flowable<Result<Any>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .busCheck(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, busflowTag)
            }
        }
    }

}