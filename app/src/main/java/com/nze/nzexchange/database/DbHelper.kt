package com.nze.nzexchange.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/4/12
 */
class DbHelper private constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    companion object {
        val DB_VERSION = 1
        val DB_NAME = "aus.db"
        val TAB_TRANSACTION_PAIR = "transaction_pair"
        val TAB_SEARCH_HISTORY = "search_history"
        val TAB_MAIN_CURRENCY = "main_currency"
        val TAB_RANK = "rank"

        @Volatile
        var instance: DbHelper? = null

        fun getInstance(c: Context): DbHelper {
            if (instance == null) {
                synchronized(DbHelper::class) {
                    if (instance == null) {
                        instance = DbHelper(c)
                    }
                }
            }
            return instance!!
        }
    }

    private constructor(context: Context) : this(context, DB_NAME, null, DB_VERSION) {
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val searchHistorySql = "create table $TAB_SEARCH_HISTORY(id integer primary key autoincrement,tid text ,createTime integer,currency text,exchangeRate real,mainCurrency text,status integer,transactionPair text,gain real,optional integer,volume real,popular integer,deal real)"
        db?.execSQL(searchHistorySql)
        val transactionPairSql = "create table $TAB_TRANSACTION_PAIR(" +
                "id integer primary key autoincrement," +
                "tid text ," +
                "createTime integer," +
                "currency text," +
                "exchangeRate real," +
                "mainCurrency text," +
                "status integer," +
                "transactionPair text," +
                "gain real," +
                "optional integer," +
                "volume real," +
                "popular integer," +
                "deal real," +
                "feePrec integer," +
                "stockPrec integer," +
                "moneyPrec integer," +
                "minAmount real," +
                "statusInitTime integer," +
                "statusUseTime integer)"
        db?.execSQL(transactionPairSql)

        val mainCurrencySql = "create table $TAB_MAIN_CURRENCY(" +
                "currency text primary key)"
        db?.execSQL(mainCurrencySql)


        val rankSql = "create table $TAB_RANK(" +
                "market text primary key," +
                "mainCurrency text," +
                "change real," +
                "deal real," +
                "high real," +
                "last real," +
                "low real," +
                "open real," +
                "volume real," +
                "cny real," +
                "stock_prec integer," +
                "money_prec integer," +
                "min_amount real)"
        db?.execSQL(rankSql)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}