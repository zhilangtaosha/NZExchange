package com.nze.nzexchange.http

import com.nze.nzexchange.bean.TransactionPairsBean
import io.reactivex.Flowable
import retrofit2.http.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/7
 */
interface BibiService {

    @GET("market/getTransactionPairs")
    fun getTransactionPairs(@Query("mainCurrency") mainCurrency: String): Flowable<Result<MutableList<TransactionPairsBean>>>


    @GET("market/findTransactionPairs")
    fun findTransactionPairs(@Query("currency") currency: String): Flowable<Result<MutableList<TransactionPairsBean>>>

}