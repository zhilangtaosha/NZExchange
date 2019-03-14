package com.nze.nzexchange.bean

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/13
 */
data class AuthenticationBean(
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
    val mereallyStep1Name: String,
    val mereallyStep2Fileurl: String,
    val mereallyStep2Name: String,
    val mereallyStep2Pars: String,
    val mereallyUpdateTime: Long,
    val mereallyUpdateTimeStr: String,
    val mereallyUpdateUser: String,
    val treeauthCode: String,
    val treeauthName: String,
    val userId: String
){
    
}