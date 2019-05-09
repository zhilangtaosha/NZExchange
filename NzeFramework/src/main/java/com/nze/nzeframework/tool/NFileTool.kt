package com.nze.nzeframework.tool

import android.app.Application
import android.graphics.Bitmap
import android.os.Environment
import java.io.*

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2019/3/15
 */
class NFileTool {

    companion object {
        //检测是否有sd卡
        fun checkSDcard(): Boolean {
            return Environment.MEDIA_MOUNTED == Environment
                    .getExternalStorageState()
        }

        /**
         * 获取SD卡的存放目录
         * 不会跟应用一起删除，只能手动删除
         */
        fun getExternalStoragePath(folderName: String): String {
            return getFolder(Environment.getExternalStorageDirectory().getAbsolutePath(), folderName).absolutePath
        }

        /**
         * 获取持久化保存的目录地址，放一些长时间保存的数据
         * 应用删除后一起删除
         * 有sd卡，保存在sd卡上：this.getExternalFilesDir("test").getAbsolutePath()
         * 无sd卡，保存在应用存储目录中：this.getFilesDir().getAbsolutePath())
         */
        fun getFilePath(context: Application, folderName: String): String {
            if (checkSDcard()) {
                return getFolder(context.getExternalFilesDir(null).absolutePath, folderName).absolutePath
            } else {
                return getFolder(context.filesDir.absolutePath, folderName).absolutePath
            }
        }

        /**
         * 获取缓存目录，放一些短时间保存的数据，请缓存的时候会清掉
         * 应用删除后一起删除
         * 有sd卡：this.getExternalCacheDir().getAbsolutePath())
         * 无sd卡：this.getCacheDir().getAbsolutePath()
         */
        fun getCachePath(context: Application, folderName: String): String {
            if (checkSDcard()) {
                return getFolder(context.externalCacheDir.absolutePath, folderName).absolutePath
            } else {
                return getFolder(context.cacheDir.absolutePath, folderName).absolutePath
            }
        }

        /**
         * 获取手机公用视频目录
         * 位于SD卡的movies文件夹
         * 不会跟应用一起删除，只能手动删除
         */
        fun getExternalMoviePath(folderName: String): String {
            return getFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath, folderName).absolutePath
        }

        /**
         * 获取手机公用图片目录
         * 位于SD卡的pictures文件夹
         * 不会跟应用一起删除，只能手动删除
         */
        fun getExternalImagePath(folderName: String): String {
            return getFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath, folderName).absolutePath
        }

        /**
         * 获取照片目录
         */
        fun getExternalPotoPath(folderName: String): String {
            return getFolder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath, folderName).absolutePath
        }

        fun getFolder(rootFolder: String, folderName: String): File {
            val file = File("$rootFolder/$folderName/")
            if (!file.exists())
                file.mkdirs()
            return file
        }


        /**
         * 图片写入文件
         *
         * @param bitmap   图片
         * @param filePath 文件路径
         * @return 是否写入成功
         */
        fun bitmapToFile(bitmap: Bitmap?, filePath: String): Boolean {
            var isSuccess = false
            if (bitmap == null) {
                return isSuccess
            }
            val file = File(filePath.substring(0,
                    filePath.lastIndexOf(File.separator)))
            if (!file.exists()) {
                file.mkdirs()
            }

            var out: OutputStream? = null
            try {
                out = BufferedOutputStream(FileOutputStream(filePath),
                        8 * 1024)
                isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                closeIO(out!!)
            }
            return isSuccess
        }

        /**
         * 关闭流
         *
         * @param closeables
         */
        fun closeIO(vararg closeables: Closeable) {
            if (null == closeables || closeables.size <= 0) {
                return
            }
            for (cb in closeables) {
                try {
                    if (null == cb) {
                        continue
                    }
                    cb.close()
                } catch (e: IOException) {
                    throw RuntimeException(
                            FileUtils::class.java.javaClass.name, e)
                }

            }
        }
    }
}