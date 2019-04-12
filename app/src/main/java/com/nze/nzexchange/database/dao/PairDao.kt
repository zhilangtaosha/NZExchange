package com.nze.nzexchange.database.dao

import com.nze.nzexchange.bean.TransactionPairsBean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 交易对保存数据库
 * @创建时间：2019/4/12
 */
interface PairDao {

    fun add(bean: TransactionPairsBean)

    fun addList(list: List<TransactionPairsBean>)

    fun findByKey(key: String):List<TransactionPairsBean>

    fun deleteAll()

}