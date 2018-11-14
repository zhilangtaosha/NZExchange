package com.nze.nzexchange.controller.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.nze.nzeframework.ui.BaseFragment

abstract class NBaseFragment : BaseFragment() {
    private var listener: OnFragmentInteractionListener? = null


    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    fun skipActivity(cls: Class<*>) {
        startActivity(Intent(activity, cls))
    }

}