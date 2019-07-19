package com.nze.nzexchange.controller.otc.main

import com.nze.nzexchange.bean.UserAssetBean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/21
 */
interface IOtcView {

    fun refresh(tokenId: String?)

    fun setOtcAsset(userAssetBean: UserAssetBean)
}