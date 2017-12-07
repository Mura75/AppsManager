package com.codebusters.appsmanager.ui.apksList

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.util.DiffUtil
import android.util.Log
import android.util.Pair
import com.arellomobile.mvp.InjectViewState
import com.codebusters.appsmanager.models.ApkInfo
import com.codebusters.appsmanager.utils.AppUtils
import com.codebusters.appsmanager.utils.base.BasePresenter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import java.io.File
import java.util.*

@InjectViewState
open class ApkListPresenter(val context: Context?) : BasePresenter<ApkListView>() {

    companion object {
        private const val  TAG = "Apks_presenter"
    }

    /**
     * Get apks list async from extracted directory
     */
    fun getApksListAsync() {
        viewState.showLoading()
        val initPair: Pair<List<ApkInfo>, DiffUtil.DiffResult> = Pair.create(emptyList(), null)
        val disposable = Flowable.defer<List<ApkInfo>> {
                    val files : Array<File> = AppUtils.getAppDefaultFolder().listFiles()
            Log.d(TAG, "Files:  " + files.size)
                    val apkFileList : ArrayList<ApkInfo> = ArrayList<ApkInfo>()
                    files.forEach {
                        val packageInfo = context?.packageManager?.getPackageArchiveInfo(it.path, PackageManager.GET_ACTIVITIES)
                        packageInfo?.apply {
                            val icon = applicationInfo.loadIcon(context?.packageManager)
                            val apkInfo = ApkInfo(it.name, it.length(), icon)
                            apkFileList.add(apkInfo)
                        }
                    }
                    Collections.sort(apkFileList) { p1, p2 -> p1.name.compareTo(p2.name)}
                    Flowable.just(apkFileList)
                }
                .scan(initPair, { pair, next ->
                    Log.d(TAG, "Next:   " + next)
                    val callback = ApkListDiffCallback(pair.first, next)
                    val result = DiffUtil.calculateDiff(callback)
                    Pair.create(next, result)
                })
                .skip(1)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                        result -> run {
                        Log.d(TAG, result.first.toString())
                        viewState.hideLoading()
                        viewState.showResult(result.first, result.second)
                    }
                }, {
                        error -> run {
                        viewState.hideLoading()
                        Log.d(TAG, "Error " + error?.toString())
                    }
                })
        addDisposable(disposable)
    }
}