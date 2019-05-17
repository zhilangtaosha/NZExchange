package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明: 公司收款方式
 * @创建时间：2019/5/17
 */
data class CompanyPaymentBean(
        val contContent: String,
        val contId: String,
        val contShow1: String,
        val contShow2: String,
        val contShow3: String,
        val contShow4: String,
        val contShow5: String,
        val contShow6: String,
        val contTitle1: String,
        val contTitle2: String,
        val contTitle3: String,
        val contTitle4: String,
        val contTitle5: String,
        val contTitle6: String,
        val positionCode: String,
        val positionName: String
) {

    fun getCompanyPaymethod(parentPositionId: String = "mod_inidata_position_20190421_01"): Flowable<Result<MutableList<CompanyPaymentBean>>> {
        return Flowable.defer {
            CRetrofit.instance
                    .userService()
                    .getCompanyPaymethod(parentPositionId)
        }
    }
}


/**
 * [
{
"contId": "40281f816a4e2366016a4e24d1cb0001",
"contContent": "",
"contTitle1": "企业收款银行",
"contTitle2": "企业收款具体银行",
"contTitle3": "\u007f企业收款银行账户号",
"contTitle4": "\u007f企业收款银行账户号",
"contTitle5": "内容5",
"contTitle6": "内容6",
"contShow1": "OSKO",
"contShow2": "OSKO账号",
"contShow3": "OSKO123456",
"contShow4": "张立东",
"contShow5": "",
"contShow6": "",
"positionCode": "0-001-006-003-",
"positionName": "企业收款账户3"
},
{
"contId": "40288ee86a3e1667016a3ecbbb450002",
"contContent": "epay",
"contTitle1": "企业收款银行",
"contTitle2": "企业收款具体银行",
"contTitle3": "\u007f企业收款银行账户号",
"contTitle4": "\u007f企业收款银行账户名",
"contTitle5": "内容5",
"contTitle6": "内容6",
"contShow1": "BPAY",
"contShow2": "epay账户",
"contShow3": "534125125121321",
"contShow4": "张立东",
"contShow5": "",
"contShow6": "",
"positionCode": "0-001-006-001-",
"positionName": "企业收款账户1"
},
{
"contId": "40288ee86a3e1667016a3edcc0a30004",
"contContent": "",
"contTitle1": "企业收款银行",
"contTitle2": "企业收款具体银行",
"contTitle3": "\u007f企业收款银行账户号",
"contTitle4": "\u007f企业收款银行账户名",
"contTitle5": "内容5",
"contTitle6": "内容6",
"contShow1": "澳新银行",
"contShow2": "澳新银行悉尼分行",
"contShow3": "123213123123",
"contShow4": "张立东",
"contShow5": "",
"contShow6": "",
"positionCode": "0-001-006-002-",
"positionName": "企业收款账户2"
}
]
 */

