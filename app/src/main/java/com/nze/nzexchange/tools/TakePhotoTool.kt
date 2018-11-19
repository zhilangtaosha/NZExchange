package com.nze.nzexchange.tools

import com.nze.nzeframework.widget.takephoto.app.TakePhoto
import com.nze.nzeframework.widget.takephoto.compress.CompressConfig
import com.nze.nzeframework.widget.takephoto.model.CropOptions
import com.nze.nzeframework.widget.takephoto.model.TakePhotoOptions

/**
 * @author: zwy
 * @email: zhouweiyong55@163.com
 * @类 说 明:
 * @创建时间：2018/11/19
 */
class TakePhotoTool {

    companion object {

        //设置压缩参数
        fun configCompress(takePhoto: TakePhoto) {
            val maxSize = 102400
            val width = 800
            val height = 800
            //是否显示压缩的进度条
            val showProgressBar = true
            //压缩后是否保留原文件
            val enableRawFile = false
            val config: CompressConfig
            config = CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(if (width >= height) width else height)
                    .enableReserveRaw(enableRawFile)
                    .create()
            takePhoto.onEnableCompress(config, showProgressBar)
        }

        //设置裁剪参数
        fun getCropOptions(): CropOptions {
            val builder = CropOptions.Builder()
            builder.setAspectX(400).setAspectY(400)
            builder.setWithOwnCrop(false)
            return builder.create()
        }

        fun commonSet(takePhoto: TakePhoto) {
            val builder = TakePhotoOptions.Builder()
            //使用自带的相册
            builder.setWithOwnGallery(false)
            //纠正拍照角度
            builder.setCorrectImage(true)
            takePhoto.setTakePhotoOptions(builder.create())
        }
    }
}