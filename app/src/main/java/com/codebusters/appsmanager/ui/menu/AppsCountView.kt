package com.codebusters.appsmanager.ui.menu

import com.arellomobile.mvp.MvpView


interface AppsCountView : MvpView {

    fun appsCount(result: Map<String, Int>)
}