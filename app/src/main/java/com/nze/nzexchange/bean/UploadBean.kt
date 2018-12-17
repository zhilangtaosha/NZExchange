package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import okhttp3.MultipartBody
import retrofit2.http.Part

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/17
 */
class UploadBean {

    companion object {

        fun uploadFileNet(
                tokenUserId: String,
                tokenUserKey: String,
                tokenSystreeId: String,
                path: String,
                busId: String,
                coverOldBusFile: Boolean,
                file: MultipartBody.Part
        ) {
            CRetrofit.instance
                    .userService()
                    .uploadFile(tokenUserId, tokenUserKey, tokenSystreeId, path, busId, coverOldBusFile, file)
        }
    }
}