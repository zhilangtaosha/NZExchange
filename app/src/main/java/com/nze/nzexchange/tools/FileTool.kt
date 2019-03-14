package com.nze.nzexchange.tools

import android.content.Context
import com.nze.nzeframework.tool.FileUtils
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
        const val CACHE_IMAGE = "$CACHE_DIR/images"
        const val CACHE_FILE = "$CACHE_DIR/files"

        /**
         * 持久化保存文件夹
         *
         * @return
         * @throws NoSdCardException
         */
        @Throws(NoSdCardException::class)
        fun getFileCachePath(): String {
            return if (FileUtils.checkSDcard()) {
                FileUtils.getSavePath(CACHE_DIR)
            } else {
                throw NoSdCardException("没有找到SD卡")
            }
        }

        @Throws(NoSdCardException::class)
        fun getImageCachePath(): String {
            return if (FileUtils.checkSDcard()) {
                FileUtils.getSavePath(CACHE_IMAGE)
            } else {
                throw NoSdCardException("没有找到SD卡")
            }
        }

        fun getTempImageName(): String {
            return String.format("%s%s", System.currentTimeMillis().toString(), ".jpg")
        }

        /**
         * 缓存文件夹
         *
         * @return
         * @throws NoSdCardException
         */
        @Throws(NoSdCardException::class)
        fun getCachePath(context: Context): String {
            return if (FileUtils.checkSDcard()) {
                context.externalCacheDir!!.absolutePath
            } else {
                throw NoSdCardException("没有找到SD卡")
            }
        }

    }
}