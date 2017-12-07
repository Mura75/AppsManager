package com.codebusters.appsmanager.ui.appsList

import android.content.pm.PackageInfo
import android.support.v7.util.DiffUtil
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.codebusters.appsmanager.models.ApkInfo
import java.text.FieldPosition

@StateStrategyType(AddToEndSingleStrategy::class)
interface AppsListView : MvpView {

    fun showLoading()

    fun hideLoading()

    fun showResult(result : List<PackageInfo>)

    fun showResult(resultList: List<PackageInfo>, diffResult: DiffUtil.DiffResult)
}