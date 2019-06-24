package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/5/12
 */
data class RealAuthenticationBean(
        val auditAuditdataDataVo: AuditAuditdataDataVo,
        val membCountry: String,
        val membId: String,
        val membIdentitycard: String,
        val membName: String,
        val membSn: String,
        val membTruename: String,
        val mereallyCreateTime: Long,
        val mereallyCreateTimeStr: String,
        val mereallyCreateUser: String,
        val mereallyId: String,
        val mereallyStatus: Int,
        val mereallyStatusStr: String,
        val mereallyStep1Fileurl: String,
        val mereallyStep1Name: Any,
        val mereallyStep2Fileurl: String,
        val mereallyStep2Name: Any,
        val mereallyStep2Pars: Any,
        val mereallyUpdateTime: Long,
        val mereallyUpdateTimeStr: String,
        val mereallyUpdateUser: String,
        val treeauthCode: String,
        val treeauthName: String,
        val userId: String
){
    companion object {
        fun saveOneEntity(
                tokenUserId: String,
                tokenUserKey: String,
                mereallyStep1Fileurl: String,
                mereallyStep2Fileurl: String?
        ): Flowable<Result<RealAuthenticationBean>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .saveOneEntity(tokenUserId, tokenUserKey, mereallyStep1Fileurl, mereallyStep2Fileurl)
            }
        }
    }
}

data class AuditAuditdataDataVo(
        val auditUsers: List<Any>?,
        val auditdataContent: Any?,
        val auditdataCreateTime: Long?,
        val auditdataCreateTimeStr: String?,
        val auditdataCreateUser: String?,
        val auditdataId: String?,
        val auditdataPass: Any?,
        val auditdataPassStr: String?,
        val auditdataStatus: Int?,
        val auditdataStatusStr: String?,
        val auditdataTitle: Any?,
        val auditdataUpdateTime: Long?,
        val auditdataUpdateTimeStr: String?,
        val auditdataUpdateUser: Any?,
        val auditflowId: String?,
        val auditflowName: String?,
        val auditflowTag: String?,
        val auditusers_all: String?,
        val busId: Any?,
        val jobdataBusNo: String?,
        val jobdataBusType: String?,
        val jobdataContent: Any?,
        val jobdataId: String?,
        val jobdataName: String?,
        val permissionId: String?,
        val permissionName: String?,
        val permissionTag: String?,
        val permissionflowApplydeal: Any?,
        val permissionflowBackNo: String?,
        val permissionflowId: String?,
        val permissionflowLogic: Any?,
        val permissionflowName: String?,
        val permissionflowNo: String?,
        val permissionflowPassNo: String?,
        val systreeCode: String?,
        val systreeId: String?,
        val systreeName: String?,
        val treeauthCode: String?,
        val treeauthId: String?,
        val treeauthName: String?,
        val userIdApply: String?,
        val userIdAudit: Any?
)