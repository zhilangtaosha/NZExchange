package com.nze.nzexchange.database.dao.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.bean.TransactionPairsBean
import com.nze.nzexchange.database.DbHelper
import com.nze.nzexchange.database.dao.PairDao

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/12
 */
class PairDaoImpl : PairDao {
    val dbs = mutableListOf<SQLiteDatabase>()

    override fun update(it: TransactionPairsBean) {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        cv.put("createTime", it.createTime)
        cv.put("currency", it.currency)
        cv.put("exchangeRate", it.exchangeRate)
        cv.put("mainCurrency", it.mainCurrency)
        cv.put("status", it.status)
        cv.put("transactionPair", it.transactionPair)
        cv.put("gain", it.gain)
        cv.put("optional", it.optional)
        db.update(DbHelper.TAB_TRANSACTION_PAIR, cv, "tid=?", arrayOf("${it.id}"))
        db.close()
    }

    override fun deleteAll() {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.writableDatabase
        val sql = "delete from ${DbHelper.TAB_TRANSACTION_PAIR}"
        db.execSQL(sql)
        db.close()
    }


    override fun add(bean: TransactionPairsBean) {

    }

    override fun addList(list: List<TransactionPairsBean>) {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.writableDatabase
        dbs.add(db)
        val cv = ContentValues()
        list.forEach {
            if (isExistById(db, it.id)) {
                update(db, cv, it)
            } else {
                insert(db, cv, it)
            }
        }
    }

    private fun isExistById(db: SQLiteDatabase, tid: String): Boolean {
        val sql = "select * from ${DbHelper.TAB_TRANSACTION_PAIR} where tid=$tid"
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        var isExist = !cursor.isAfterLast
        cursor.close()
        return isExist
    }

    private fun update(db: SQLiteDatabase, cv: ContentValues, it: TransactionPairsBean) {
        cv.clear()
        cv.put("createTime", it.createTime)
        cv.put("currency", it.currency)
        cv.put("exchangeRate", it.exchangeRate)
        cv.put("mainCurrency", it.mainCurrency)
        cv.put("status", it.status)
        cv.put("transactionPair", it.transactionPair)
        cv.put("gain", it.gain)
        cv.put("optional", it.optional)
        db.update(DbHelper.TAB_TRANSACTION_PAIR, cv, "tid=?", arrayOf("${it.id}"))
    }

    private fun insert(db: SQLiteDatabase, cv: ContentValues, it: TransactionPairsBean) {
        cv.clear()
        cv.put("tid", it.id)
        cv.put("createTime", it.createTime)
        cv.put("currency", it.currency)
        cv.put("exchangeRate", it.exchangeRate)
        cv.put("mainCurrency", it.mainCurrency)
        cv.put("status", it.status)
        cv.put("transactionPair", it.transactionPair)
        cv.put("gain", it.gain)
        cv.put("optional", it.optional)
        db.insert(DbHelper.TAB_TRANSACTION_PAIR, null, cv)
    }


    override fun findByKey(key: String): List<TransactionPairsBean> {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("select * from ${DbHelper.TAB_TRANSACTION_PAIR} where transactionPair like ?", arrayOf("%$key%"))
        cursor.moveToFirst()
        val list = mutableListOf<TransactionPairsBean>()
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
                    cursor.getInt(9)
            )
            list.add(bean)
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return list
    }


    fun close() {
        dbs.forEach {
            if (it.isOpen)
                it.close()
        }
    }
}