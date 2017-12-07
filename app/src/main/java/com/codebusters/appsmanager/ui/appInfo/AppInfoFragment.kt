package com.codebusters.appsmanager.ui.appInfo


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.codebusters.appsmanager.R



class AppInfoFragment : MvpAppCompatFragment() {

    companion object {
        fun newInstance() = AppInfoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_info, container, false)
    }

}
