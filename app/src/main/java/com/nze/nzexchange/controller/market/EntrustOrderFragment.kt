package com.nze.nzexchange.controller.market


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.nze.nzexchange.R


/**
 * A simple [Fragment] subclass.
 *委托订单
 */
class EntrustOrderFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrust_order, container, false)
    }


}
