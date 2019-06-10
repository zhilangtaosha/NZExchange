package com.nze.nzexchange.tools

import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.NBaseActivity
import com.nze.nzexchange.controller.login.selectcountry.CharacterParserUtil
import com.nze.nzexchange.controller.login.selectcountry.CountrySortModel
import com.nze.nzexchange.controller.login.selectcountry.GetCountryNameSort

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/6/10
 */
class CountryTool(act: NBaseActivity) {
    private val mAllCountryList by lazy { mutableListOf<CountrySortModel>() }
    private val mParserUtil by lazy { CharacterParserUtil() }
    private val mNameSort by lazy { GetCountryNameSort() }

    init {
        val countryList = act.resources.getStringArray(R.array.country_code_list)

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

    }

    companion object {
        fun getInstance(act: NBaseActivity): CountryTool {
            return CountryTool(act)
        }
    }

    fun getCountryName(countryNum: String): String {
        val num = "+${countryNum}"
        for (model in mAllCountryList) {
            if (model.countryNumber == num || model.countryNumber.replace("+", "+0") == num) {
                return model.countryName
                break
            }
        }
        return ""
    }
}