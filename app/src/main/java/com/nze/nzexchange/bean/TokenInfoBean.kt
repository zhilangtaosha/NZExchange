package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:获取数字货币信息
 * @创建时间：2019/4/1
 */
data class TokenInfoBean(
        val tokenAmount: String,
        val tokenChaintype: String,
        val tokenCirculate: String,
        val tokenCreateTime: Long,
        val tokenCreateUser: String,
        val tokenDesc: String,
        val tokenIco: String,
        val tokenId: String,
        val tokenOwner: String,
        val tokenPaper: String,
        val tokenPrice: String,
        val tokenQuery: String,
        val tokenStatus: Int,
        val tokenSymbol: String,
        val tokenTeam: String,
        val tokenTime: String,
        val tokenType: String,
        val tokenUpdateTime: Long,
        val tokenUpdateUser: String,
        val tokenWebsite: String
) {
    companion object {
        fun getTokenInfo(tokenName: String): Flowable<Result<TokenInfoBean>> {
            return Flowable.defer {
                NRetrofit.instance
                        .sellService()
                        .getTokenInfo(tokenName)
            }

        }

    }
}