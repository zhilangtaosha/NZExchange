package com.nze.nzexchange.bean

import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/20
 */
data class BibiAssetBean(
        val address: String,
        val available: Double,
        val currency: String,
        val freeze: Double
) {

    companion object {

        fun getUserAssetsNet(userId: String): Flowable<Result<MutableList<BibiAssetBean>>> {
            return Flowable.defer {
                NRetrofit.instance
                        .assetService()
                        .getUserAssets(userId)
            }
        }


        fun getList() = mutableListOf<BibiAssetBean>().apply {
            for (i in 1..10) {
                add(BibiAssetBean("sdf65465651", 6.58, "CURRENCY$i", 1.20))
            }
        }
    }
}