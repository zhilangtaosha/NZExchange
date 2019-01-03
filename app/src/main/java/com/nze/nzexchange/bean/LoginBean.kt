package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable
import java.io.Serializable

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/11
 */
data class LoginBean(
        val errorCode: String?,
        val errorMsg: String?,
        val loginErrorCount: Int,
        val token: Token
) : Serializable {

    fun cloneToUserBean(): UserBean {
        return token.run {
            UserBean(userEmail, userId, userName, userNick, userNo, userPhone, userShow, userTag, TokenReqVo(tokenReqVo.tokenSystreeId, tokenReqVo.tokenUserId, tokenReqVo.tokenUserKey),null)
        }
    }

    companion object {

        fun login(userTag: String, userPassworUcode: String): Flowable<Result<LoginBean>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .login(userTag, userPassworUcode, null, null)
            }
        }

    }
}

data class Token(
        val membId: String?,
        val systreeNode_id: String?,
        val systreeNode_name: String?,
        val tokenMenuVo: TokenMenuVo,
        val tokenPermissionVos: List<TokenPermissionVos>,
        val tokenReqVo: TokenReqVo,
        val topComAuthCode: String?,
        val topComAuthId: String?,
        val topComName: String?,
        val treeauthCode: String?,
        val treeauthId: String?,
        val treeauthName: String?,
        val userEmail: String?,
        val userId: String,
        val userName: String?,
        val userNick: String?,
        val userNo: String?,
        val userPhone: String?,
        val userShow: String?,
        val userTag: String?
) : Serializable

data class TokenReqVo(
        val tokenSystreeId: String,
        val tokenUserId: String,
        val tokenUserKey: String
) : Serializable

data class TokenMenuVo(
        val children: List<Children>,
        val ico: String?,
        val menuOpenmethod: String?,
        val text: String?,
        val trNodeId: String?,
        val trNodeParentId: String?,
        val url: String?
) : Serializable

data class Children(
        val children: List<Children>,
        val ico: String?,
        val menuOpenmethod: String?,
        val text: String?,
        val trNodeId: String?,
        val trNodeParentId: String?,
        val url: String?
) : Serializable

data class TokenPermissionVos(
        val permissionActionurl: String?,
        val permissionId: String?,
        val permissionName: String?,
        val permissionTag: String?
) : Serializable

