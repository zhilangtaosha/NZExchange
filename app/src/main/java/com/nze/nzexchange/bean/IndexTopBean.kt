package com.nze.nzexchange.bean

import com.nze.nzexchange.http.CRetrofit
import io.reactivex.Flowable
import retrofit2.http.Field

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/20
 */
class IndexTopBean(
        val notices: List<IndexNotices>,
        val imgs: List<IndexImgs>,
        val conts: List<IndexConts>
) {
    companion object {

        fun indexAllData(
                containNotice: Boolean,
                containTempImg: Boolean,
                containTempCont: Boolean
        ): Flowable<Result<IndexTopBean>> {
            return Flowable.defer {
                CRetrofit.instance
                        .userService()
                        .indexAllData(containNotice, containTempImg, containTempCont)
            }
        }
    }
}

data class IndexNotices(
        val noticeBetimeStr: String,
        val noticeContent: String,
        val noticeCreateTimeStr: String,
        val noticeEndtimeStr: String,
        val noticeId: String,
        val noticeTitle: String,
        val noticeUrl: String,
        val noticeBashmtlUrl: String
)

data class IndexImgs(
        val imgId: String,
        val imgUrl: String,
        val positionCode: String,
        val positionName: String
)

data class IndexConts(
        val contContent: String,
        val contId: String,
        val contShow1: String,
        val contShow2: String,
        val contShow3: String,
        val contShow4: String,
        val contShow5: String,
        val contShow6: String,
        val contTitle1: String,
        val contTitle2: String,
        val contTitle3: String,
        val contTitle4: String,
        val contTitle5: String,
        val contTitle6: String,
        val positionCode: String,
        val positionName: String
)