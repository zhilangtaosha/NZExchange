package com.nze.nzexchange.bean

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
        val checkpayAmt: Int,
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

    }
}

data class AmtBusicheckEntity(
        val busId: String,
        val busicheckBusType: String,
        val busicheckCreateTime: Long,
        val busicheckCreateUser: String,
        val busicheckId: String,
        val busicheckStatus: Any,
        val busicheckUpdateTime: Any,
        val busicheckUpdateUser: Any,
        val checkpayId: String
)