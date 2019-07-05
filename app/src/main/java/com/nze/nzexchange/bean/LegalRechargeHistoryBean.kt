package com.nze.nzexchange.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/20
 */
@Parcelize
data class LegalRechargeHistoryBean(
        val amtBusicheckEntity: AmtBusicheckEntity,
        val auditAuditdataDataVo: AuditAuditdataDataVo,
        val auditDataFlowDataVos: List<AuditDataFlowDataVos>,
        val checkpayAbleamount: Double,
        val checkpayAccount: String,
        val checkpayAmt: Double,
        val checkpayAuditUsetag: String,
        val checkpayBillno: String,
        val checkpayCode: String,
        val checkpayCreateTime: Long,
        val checkpayCreateTimeStr: String,
        val checkpayCreateUser: String,
        val checkpayDoTime: Long,
        val checkpayFronzenamount: Double,
        val checkpayId: String,
        val checkpayMemo: String,
        val checkpayName: String,
        val checkpayOrd: Int,
        val checkpayPlat: String,
        val checkpayReallyamt: String,
        //状态码 100 处理中 101 第一次审核中 102 第二次审核中 990审核完成 9901审失败
        val checkpayStatus: Int,
        val checkpayStatusStr: String,
        val checkpayType: String,
        val checkpayUpdateTime: Long,
        val checkpayUpdateTimeStr: String,
        val checkpayUpdateUser: String,
        val systreeId: String,
        val treeauthCode: String,
        val treeauthId: String,
        val treeauthName: String,
        val userIdApply: String,
        val userIdAudit: String
) : Parcelable {

    fun getStatus(): String =
            when (checkpayStatus) {
                100 -> {
                    "待审核"
                }
                101, 102 -> {
                    "审核中"
                }
                990 -> {
                    "已完成"
                }
                9901 -> {
                    "审核失败"
                }
                else -> {
                    "有问题"
                }
            }
}

@Parcelize
data class AuditDataFlowDataVos(
        val flowTime: Long,
        val flowEvent: String,
        val flowOrd: Int
) : Parcelable

