package com.nze.nzeframework.tool

import android.widget.ImageView
import com.bumptech.glide.Glide
import java.lang.ref.SoftReference

/**
 * 使用Context不要使用Application
 * ImageView考虑使用软引用或者弱引用
 */
class NGlide {
    companion object {
        fun load(imageView: ImageView, path: String) {
            val iv:SoftReference<ImageView> = SoftReference(imageView)
            Glide.with(iv.get()!!)
                    .load(path)
                    .into(iv.get()!!)
        }
    }
}