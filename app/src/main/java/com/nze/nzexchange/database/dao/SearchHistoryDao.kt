package com.nze.nzexchange.database.dao

import com.nze.nzexchange.bean.TransactionPairsBean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/13
 */
interface SearchHistoryDao {

    fun add(bean: TransactionPairsBean)

    fun findAll(): List<TransactionPairsBean>

    fun delete()
}