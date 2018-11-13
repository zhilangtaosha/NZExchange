package com.nze.nzexchange.controller.home.carousel

import android.os.Build.VERSION_CODES.N
import android.os.Bundle
import android.view.View
import com.nze.nzeframework.utils.NGlide
import com.nze.nzexchange.R
import com.nze.nzexchange.controller.base.SimpleFragment
import kotlinx.android.synthetic.main.fragment_carousel.view.*


private const val ARG_PARAM1 = "imageUrl"
private const val ARG_PARAM2 = "position"

class CarouselFragment : SimpleFragment() {
    override fun getRootView(): Int = R.layout.fragment_carousel
    private var imageUrl: String = ""
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString(ARG_PARAM1)
            position = it.getInt(ARG_PARAM2)
        }
    }


    override fun initView(rootView: View) {
        NGlide.load(rootView.imageView, imageUrl)

    }

    companion object {

        @JvmStatic
        fun newInstance(imageUrl: String, position: Int) =
                CarouselFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, imageUrl)
                        putInt(ARG_PARAM2, position)
                    }
                }
    }
}