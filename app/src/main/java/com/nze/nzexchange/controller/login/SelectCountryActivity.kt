package com.nze.nzexchange.controller.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.nze.nzeframework.netstatus.NetUtils
import com.nze.nzeframework.tool.EventCenter
import com.nze.nzexchange.R
import com.nze.nzexchange.config.IntentConstant
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.login.selectcountry.*
import com.nze.nzexchange.controller.main.MainActivity
import kotlinx.android.synthetic.main.activity_select_country.*
import java.util.*

class SelectCountryActivity : NBaseActivity() {

    private val mAllCountryList by lazy { mutableListOf<CountrySortModel>() }
    private val mSearchEt by lazy { cet_search_asc.apply { clearFocus() } }
    private val mCancelTv by lazy { tv_cancel_asc }
    private val mListView by lazy { lv_asc }
    private val mSideBar by lazy { sb_asc }
    private val mDialogTv by lazy { tv_dialog_asc }
    private val mComparator by lazy { CountryComparator() }
    private val mNameSort by lazy { GetCountryNameSort() }
    private val mParserUtil by lazy { CharacterParserUtil() }
    private val mSortAdapter by lazy { CountrySortAdapter(this, mAllCountryList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
    }

    override fun getRootView(): Int = R.layout.activity_select_country

    override fun initView() {
        mSideBar.setTextView(mDialogTv)

        Collections.sort(mAllCountryList, mComparator)
        mListView.adapter = mSortAdapter

        RxTextView.textChanges(mSearchEt)
                .subscribe {
                    val searchContent = it.toString()

                    if (searchContent.length > 0) {
                        // 按照输入内容进行匹配
                        val fileterList = mNameSort.search(searchContent, mAllCountryList) as ArrayList<CountrySortModel>

                        mSortAdapter.updateListView(fileterList)
                    } else {
                        mSortAdapter.updateListView(mAllCountryList)
                    }
                    mListView.setSelection(0)
                }

        mSideBar.setOnTouchingLetterChangedListener {
            val position = mSortAdapter.getPositionForSection(it[0].toInt())
            if (position != -1)
                mListView.setSelection(position)

        }

        mListView.setOnItemClickListener { parent, view, position, id ->
            var countryName: String? = null
            var countryNumber: String? = null
            val searchContent = mSearchEt.getText().toString()

            if (searchContent.length > 0) {
                // 按照输入内容进行匹配
                val fileterList = mNameSort.search(searchContent, mAllCountryList) as ArrayList<CountrySortModel>
                countryName = fileterList[position].countryName
                countryNumber = fileterList[position].countryNumber
            } else {
                // 点击后返回
                countryName = mAllCountryList[position].countryName
                countryNumber = mAllCountryList[position].countryNumber
            }

            val intent = Intent()
            intent.setClass(this@SelectCountryActivity, MainActivity::class.java!!)
            intent.putExtra(IntentConstant.PARAM_COUNTRY_NAME, countryName)
            intent.putExtra(IntentConstant.PARAM_COUNTRY_NUMBER, countryNumber)
            setResult(Activity.RESULT_OK, intent)
            this@SelectCountryActivity.finish()
        }

        getCountryList()
    }

    fun getCountryList() {
        val countryList = resources.getStringArray(R.array.country_code_list)

        countryList.forEach {
            val split = it.split("*")
            val countryName = split[0]
            val countryNumber = split[1]
            val countrySortKey = mParserUtil.getSelling(countryName)
            val countrySortModel = CountrySortModel(countryName, countryNumber, countrySortKey)
            var sortLetter = mNameSort.getSortLetterBySortKey(countrySortKey)
            if (sortLetter == null) {
                sortLetter = mNameSort.getSortLetterBySortKey(countryName)
            }
            countrySortModel.sortLetters = sortLetter
            mAllCountryList.add(countrySortModel)
        }

        Collections.sort(mAllCountryList, mComparator)
        mSortAdapter.updateListView(mAllCountryList)


    }

    override fun <T> onEventComming(eventCenter: EventCenter<T>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOverridePendingTransitionMode(): TransitionMode = TransitionMode.DEFAULT

    override fun isBindEventBusHere(): Boolean = false

    override fun isBindNetworkListener(): Boolean = false

    override fun onNetworkConnected(type: NetUtils.NetType) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkDisConnected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContainerTargetView(): View? = null


}
