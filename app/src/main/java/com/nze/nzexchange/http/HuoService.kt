package com.nze.nzexchange.http

import com.nze.nzexchange.bean.OrderPendBean
import com.nze.nzexchange.bean.Result
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/18
 */
interface HuoService {

    //校验火币市场交易对是否存在
    @GET("huobikline/checkMarket")
    fun checkMarket(
            @Query("market") market: String
    ): Flowable<Result<Any>>
}