package com.nze.nzexchange.database.dao

import com.nze.nzexchange.bean.SoketMarketBean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/7/23
 */
interface SoketPairDao {

   fun add(mMarketList: MutableList<SoketMarketBean>)
}