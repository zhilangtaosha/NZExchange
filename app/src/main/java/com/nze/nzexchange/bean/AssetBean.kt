package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import com.nze.nzexchange.http.Result
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/20
 */
data class AssetBean(var currency: String = "",
                     var amount: Double,
                     var tokenId: String = "",
                     var freeze: Double) {
    var available: Double = 0.0
        get() {
            return amount - freeze
        }

    companion object {
        fun getAssetsNet(userId: String): Flowable<Result<MutableList<AssetBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .buyService()
                        .getAssets(userId)
            }
        }
    }
}