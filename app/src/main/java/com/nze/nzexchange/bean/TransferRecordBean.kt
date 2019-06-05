package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import retrofit2.http.Query

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/18
 */
data class TransferRecordBean(
        val amount: Double,
        val createTime: Long,
        val from: String,
        val id: String,
        val status: Int,//1001:开始划转 / 1002:扣款成功 / 1003:转账成功
        val to: String,
        val token: String,
        val userId: String
) {
    companion object {
        val ACCOUNT_OTC = "outside"
        val ACCOUNT_BIBI = "coin"
        fun transferRecord(userId: String, from: String? = null, to: String? = null): Flowable<Result<MutableList<TransferRecordBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .assetService()
                        .transferRecord(userId, from, to)
            }
        }
    }
}