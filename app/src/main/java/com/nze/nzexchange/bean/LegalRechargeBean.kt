package com.nze.nzexchange.bean

import android.os.Parcelable
import com.nze.nzexchange.http.CRetrofit
import com.nze.nzexchange.http.NRetrofit
import io.reactivex.Flowable
import kotlinx.android.parcel.Parcelize
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:法币充值实体
 * @创建时间：2019/5/16
 */
data class LegalRechargeBean(
        val amtBusicheckEntity: AmtBusicheckEntity,
        val auditAuditdataDataVo: Any,
        val checkpayAccount: String,
        val checkpayAmt: Double,
        val checkpayAuditUsetag: Any,
        val checkpayBillno: String,
        val checkpayCode: String,
        val checkpayCreateTime: Long,
        val checkpayCreateTimeStr: String,
        val checkpayCreateUser: String,
        val checkpayDoTime: Long,
        val checkpayId: String,
        val checkpayMemo: Any,
        val checkpayName: String,
        val checkpayOrd: Int,
        val checkpayPlat: String,
        val checkpayReallyamt: Any,
        val checkpayStatus: String,
        val checkpayStatusStr: String,
        val checkpayType: String,
        val checkpayUpdateTime: Long,
        val checkpayUpdateTimeStr: String,
        val checkpayUpdateUser: Any,
        val systreeId: String,
        val treeauthCode: String,
        val treeauthId: String,
        val treeauthName: String,
        val userIdApply: String,
        val userIdAudit: Any
) {
    companion object {
        //        val TYPE_BPAY = "bpay转账"
//        val TYPE_BANK = "银行卡转账"
//        val TYPE_OSKO = "澳洲银行卡 osko 转账"
        val TYPE_BPAY = "bpay"
        val TYPE_BANK = "bank"
        val TYPE_OSKO = "osko"


        fun getLegalCode(): Flowable<Result<String>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .getLegalCode(" PD")
            }
        }


        fun legalRecharge(
                userBean: UserBean,
                checkpayAmt: String,
                checkpayPlat: String?,
                checkpayDoTime: String?,
                checkpayAccount: String?,
                checkpayBillno: String?,
                checkpayType: String,
                checkpayCode: String
        ): Flowable<Result<LegalRechargeBean>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .legalRecharge(userBean.tokenReqVo.tokenUserId, userBean.tokenReqVo.tokenUserKey, "accPay", checkpayAmt, checkpayPlat, checkpayDoTime, checkpayAccount, checkpayBillno, checkpayType, checkpayCode)

            }
        }
    }
}

@Parcelize
data class AmtBusicheckEntity(
        val busId: String?,
        val busicheckBusType: String?,
        val busicheckCreateTime: Long,
        val busicheckCreateUser: String?,
        val busicheckId: String?,
        val busicheckStatus: String?,
        val busicheckUpdateTime: String?,
        val busicheckUpdateUser: String?,
        val checkpayId: String?
) : Parcelable