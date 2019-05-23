package com.nze.nzexchange.database.dao.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.database.DbHelper
import com.nze.nzexchange.database.dao.SearchHistoryDao

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/13
 */
class SearchHistoryDaoImpl : SearchHistoryDao {

    override fun add(it: TransactionPairsBean) {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put("tid", it.id)
        cv.put("createTime", it.createTime)
        cv.put("currency", it.currency)
        cv.put("exchangeRate", it.exchangeRate)
        cv.put("mainCurrency", it.mainCurrency)
        cv.put("status", it.status)
        cv.put("transactionPair", it.transactionPair)
        cv.put("gain", it.gain)
        cv.put("optional", it.optional)
        cv.put("volume", it.volume)
        cv.put("popular",it.popular)
        cv.put("deal",it.deal)
        db.insert(DbHelper.TAB_SEARCH_HISTORY, null, cv)
        db.close()
    }

    override fun findAll(): List<TransactionPairsBean> {
        val list = mutableListOf<TransactionPairsBean>()
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("select * from ${DbHelper.TAB_SEARCH_HISTORY}", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val bean = TransactionPairsBean(
                    cursor.getLong(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getString(1),
                    cursor.getString(5),
                    null,
                    cursor.getInt(6),
                    cursor.getString(7),
                    cursor.getDouble(8),
                    cursor.getInt(9),
                    cursor.getDouble(10),
                    cursor.getInt(11),
                    cursor.getDouble(12)
            )
            list.add(bean)
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return list
    }

    override fun delete() {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val sql = "delete from ${DbHelper.TAB_SEARCH_HISTORY}"
        db.execSQL(sql)
        db.close()
    }
}