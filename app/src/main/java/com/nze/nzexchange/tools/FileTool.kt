package com.nze.nzexchange.tools

import android.content.Context
import com.nze.nzeframework.tool.FileUtils
import com.nze.nzeframework.tool.NFileTool
import com.nze.nzexchange.NzeApp
import com.nze.nzexchange.exception.NoSdCardException

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/19
 */
class FileTool {

    companion object {
        const val CACHE_DIR = "NZExchange"
        const val CACHE_IMAGE = "images"
        const val CACHE_FILE = "files"
        const val CACHE_MOVIE = "movies"
        const val APP = "auscoin"

        /**
         * 图片缓存地址
         * 应用删除后一起删除
         */
        fun getImageCachePath(): String {
            return NFileTool.getCachePath(NzeApp.instance, CACHE_IMAGE)
        }

        fun getMovieCachePath(): String {
            return NFileTool.getFilePath(NzeApp.instance, CACHE_MOVIE)
        }

        /**
         * 公用图片存储地址
         * 应用删除后不会一起删除，需手动删除
         */
        fun getPicturePath(): String {
            return NFileTool.getExternalImagePath(APP)
        }

        /**
         * 照片目录
         */
        fun getPotoPath(): String {
            return NFileTool.getExternalPotoPath(APP)
        }


        fun getTempName(type: String): String {
            return "${System.currentTimeMillis()}.$type"
        }

    }
}