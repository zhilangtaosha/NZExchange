package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/12/17
 */
data class UploadBean(
        val allFileHttpUrl: String,
        val busId: String,
        val fileds: List<Filed>,
        val msg: String
) {

    companion object {

        fun uploadFileNet(
                tokenUserId: String,
                tokenUserKey: String,
                tokenSystreeId: String,
                path: String,
                busId: String,
                coverOldBusFile: Boolean,
                file: MultipartBody.Part
        ): Flowable<Result<UploadBean>> {

            return Flowable.defer {
                CRetrofit.instance
                        .uploadService()
                        .uploadFile(tokenUserId, tokenUserKey, tokenSystreeId, path, busId, coverOldBusFile, file)
            }
        }
    }
}


data class Filed(
        val busId: String,
        val diskPath: String,
        val downUrl: String,
        val fileBusName: String,
        val fileBusPath: String,
        val fileContent: String,
        val fileCreateTime: Long,
        val fileCreateTimeStr: String,
        val fileCreateUser: String,
        val fileId: String,
        val fileName: String,
        val filePath: String,
        val fileSize: Int,
        val fileStatus: Int,
        val fileStatusStr: String,
        val fileUpdateTime: Long,
        val fileUpdateUser: Any,
        val httpUrl: String,
        val systreeId: String,
        val treeauthCode: String,
        val treeauthId: String,
        val treeauthName: String
)