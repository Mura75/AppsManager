package com.codebusters.appsmanager.ui.apksList

import android.support.v7.util.DiffUtil
import com.arellomobile.mvp.MvpView
import com.codebusters.appsmanager.models.ApkInfo


interface ApkListView : MvpView {

    fun showLoading()

    fun hideLoading()

    fun showResult(resultList: List<ApkInfo>, diffResult: DiffUtil.DiffResult)

    fun showEmptyList()
}