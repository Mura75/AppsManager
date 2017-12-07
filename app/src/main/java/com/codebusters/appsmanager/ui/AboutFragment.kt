package com.codebusters.appsmanager.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.utils.Constants
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment : Fragment() {

    companion object {
        val TAG = "About_fragment"
        fun newInstance() = AboutFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }

    private fun bindViews() {
        cvInfo.setOnClickListener {
            Intent(Intent.ACTION_VIEW).let {
                it.setData(Uri.parse(Constants.URL))
                startActivity(it)
            }
        }
    }
}