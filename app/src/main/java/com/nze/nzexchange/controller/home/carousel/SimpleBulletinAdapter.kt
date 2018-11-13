package com.nze.nzexchange.controller.home.carousel

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.nze.nzexchange.R
import com.nze.nzexchange.R.id.imageView
import com.nze.nzexchange.widget.bulletin.BulletinAdapter
import kotlinx.android.synthetic.main.bulletin_simple_item.view.*
import kotlinx.android.synthetic.main.fragment_carousel.view.*

class SimpleBulletinAdapter(context: Context, dataList: List<String>) : BulletinAdapter<String>(context, dataList) {

    private var mImageDrawableID = R.drawable.shape_bulletin_tip



    override fun getView(position: Int): View {

        val view = getRootView(R.layout.bulletin_simple_item)

//        val imageView = view.findViewById(R.id.iv_image) as ImageView
//        val textView = view.findViewById(R.id.tv_content) as TextView

        val data = mData.get(position)
//        view.iv_image.setImageResource(mImageDrawableID)
        view.tv_content.setText(data)
        return view
    }

}