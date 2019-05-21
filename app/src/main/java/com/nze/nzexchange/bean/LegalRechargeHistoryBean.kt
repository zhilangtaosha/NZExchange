package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/20
 */
data class LegalRechargeHistoryBean(
        val amtBusicheckEntity: AmtBusicheckEntity,
        val auditAuditdataDataVo: AuditAuditdataDataVo,
        val checkpayAbleamount: Int,
        val checkpayAccount: Any,
        val checkpayAmt: Double,
        val checkpayAuditUsetag: Any,
        val checkpayBillno: Any,
        val checkpayCode: String,
        val checkpayCreateTime: Long,
        val checkpayCreateTimeStr: String,
        val checkpayCreateUser: String,
        val checkpayDoTime: Long,
        val checkpayFronzenamount: Int,
        val checkpayId: String,
        val checkpayMemo: Any,
        val checkpayName: String,
        val checkpayOrd: Int,
        val checkpayPlat: Any,
        val checkpayReallyamt: Any,
        //状态码 100 处理中 101 第一次审核中 102 第二次审核中 990审核完成 9901审失败
        val checkpayStatus: Int,
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

