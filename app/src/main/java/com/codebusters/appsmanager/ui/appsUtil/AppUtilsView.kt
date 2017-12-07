package com.codebusters.appsmanager.mvp

import android.content.pm.PackageInfo
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import java.text.FieldPosition


interface AppUtilsView : MvpView {

    fun showLoading()

    fun hideLoading()

    @StateStrategyType(SkipStrategy::class)
    fun showExtractLoading()

    @StateStrategyType(SkipStrategy::class)
    fun extractResult(result : String)

//    @StateStrategyType(SkipStrategy::class)
    fun deletedResult(position: Int, item: PackageInfo)
}