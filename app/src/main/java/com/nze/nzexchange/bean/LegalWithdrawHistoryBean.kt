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
data class LegalWithdrawHistoryBean(
        val accId: String,
        val auditAuditdataDataVo: AuditAuditdataDataVo?,
        val auditAuditdataDataVos: List<AuditAuditdataDataVo>?,
        val auditDataFlowDataVos: List<AuditDataFlowDataVo>,
        val membId: String,
        val pickfundApplyamt: Double,
        val pickfundCode: String,
        val pickfundCreateTime: Long,
        val pickfundCreateTimeStr: String,
        val pickfundCreateUser: String,
        val pickfundId: String,
        val pickfundMemo: String,
        val pickfundName: String,
        val pickfundOrd: Int,
        val pickfundReallyamt: Double,
        ////状态码 100 处理中 101 第一次审核中 102 第二次审核中 990审核完成 9901审失败
        val pickfundStatus: Int,
        val pickfundStatusStr: String,
        val pickfundToinfo1: String?,
        val pickfundToinfo2: String?,
        val pickfundToinfo3: String?,
        val pickfundUpdateTime: Long,
        val pickfundUpdateTimeStr: String,
        val pickfundUpdateUser: String?,
        val systreeId: String,
        val treeauthCode: String,
        val treeauthId: String,
        val treeauthName: String,
        val userId: String,
        val userIdApply: String?,
        val userIdAudit: String?,
        val pickfundAbleamount: Double,
        val pickfundFronzenamount: Double
) : Parcelable {
    fun getStatus(): String =
            when (pickfundStatus) {
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
data class AuditDataFlowDataVo(
        val flowEvent: String?,
        val flowOrd: Int?,
        val flowTime: Long
) : Parcelable
