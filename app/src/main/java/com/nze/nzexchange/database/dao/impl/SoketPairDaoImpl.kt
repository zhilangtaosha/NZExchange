package com.nze.nzexchange.database.dao.impl

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.bean.SoketMarketBean
import com.nze.nzexchange.bean.SoketRankBean
import com.nze.nzexchange.database.DbHelper
import com.nze.nzexchange.database.dao.SoketPairDao

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/7/25
 */
class SoketPairDaoImpl : SoketPairDao {
    override fun add(mMarketList: MutableList<SoketMarketBean>) {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.writableDatabase
        val cv = ContentValues()
        mMarketList.forEach { market ->
            cv.clear()
            if (!isExistMainCurrency(db, market.money)) {
                cv.put("currency", market.money)
                db.insert(DbHelper.TAB_MAIN_CURRENCY, null, cv)
            }

            market.list.forEach {
                cv.clear()
                if (!isExistRank(db, it.market)) {
                    cv.put("market", it.market)
                    cv.put("mainCurrency", market.money)
                    cv.put("deal", it.deal)
                    cv.put("high", it.high)
                    cv.put("last", it.last)
                    cv.put("low", it.low)
                    cv.put("open", it.open)
                    cv.put("volume", it.volume)
                    cv.put("cny", it.cny)
                    cv.put("change", it.change)
                    cv.put("stock_prec", it.stock_prec)
                    cv.put("money_prec", it.money_prec)
                    cv.put("min_amount", it.min_amount)
                    db.insert(DbHelper.TAB_RANK, null, cv)
                }
            }
        }
        cv.clear()
    }

    fun isExistMainCurrency(db: SQLiteDatabase, mainCurrency: String): Boolean {
        val sql = "select * from ${DbHelper.TAB_MAIN_CURRENCY} where currency='${mainCurrency}'"
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        var isExist = !cursor.isAfterLast
        cursor.close()
        return isExist
    }


    fun isExistRank(db: SQLiteDatabase, market: String): Boolean {
        val sql = "select * from ${DbHelper.TAB_RANK} where market='${market}'"
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        var isExist = !cursor.isAfterLast
        cursor.close()
        return isExist
    }

    override fun getMainCurrency(): List<String> {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("select * from ${DbHelper.TAB_MAIN_CURRENCY}", null)
        cursor.moveToFirst()
        val list = arrayListOf<String>()
        while (!cursor.isAfterLast) {
            list.add(cursor.getString(0))
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return list
    }

    override fun getRankList(): List<SoketRankBean> {
        val dbHelper = DbHelper.getInstance(NzeApp.instance)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("select * from ${DbHelper.TAB_RANK}", null)
        val list = arrayListOf<SoketRankBean>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val bean = SoketRankBean(
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6),
                    cursor.getDouble(7),
                    cursor.getDouble(8),
                    cursor.getDouble(9),
                    cursor.getInt(10),
                    cursor.getInt(11),
                    cursor.getDouble(12),
                    0
            )
            bean.market = cursor.getString(0)
            bean.mainCurrency2 = cursor.getString(1)
            bean.change = cursor.getDouble(2)
            list.add(bean)
            cursor.moveToNext()
        }
        cursor.close()
        db.close()
        return list
    }

    override fun getRankBean(pair: String): SoketRankBean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}