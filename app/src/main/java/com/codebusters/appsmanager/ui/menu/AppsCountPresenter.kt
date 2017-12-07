package com.codebusters.appsmanager.ui.menu

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.codebusters.appsmanager.utils.base.BasePresenter
import com.codebusters.appsmanager.utils.AppUtils
import com.codebusters.appsmanager.utils.Constants
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

@InjectViewState
class AppsCountPresenter() : BasePresenter<AppsCountView>() {

    private lateinit var activity: Activity

    companion object {
        val TAG = "Apps_count_pres"
    }

    constructor(activity: Activity) : this() {
        this.activity = activity
    }

    /**
     * Get apks and apps counts after application start for navigation drawer
     */
    fun getAppCount() {
        var systemAppCnt = 0
        var userAppCount = 0
        val map = HashMap<String, Int>()
        val disposable = Flowable.just(activity.packageManager)
                .flatMap {packageManger ->
                    val packages = packageManger.getInstalledPackages(PackageManager.GET_META_DATA)
                    packages?.let {
                        for (packageInfo in it) {
                            val isSystem = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0;
                            if (isSystem)
                                systemAppCnt++
                            else
                                userAppCount++
                        }
                    }
                    val filesList : Int = AppUtils.getAppDefaultFolder().listFiles()?.size ?: 0
                    Flowable.zip(Flowable.just(systemAppCnt),
                            Flowable.just(userAppCount),
                            Flowable.just(filesList),
                            Function3<Int, Int, Int, HashMap<String, Int>> { t1, t2, t3 ->
                                map.apply {
                                    put(Constants.SYSTEM_APP, t1)
                                    put(Constants.USER_APP, t2)
                                    put(Constants.APKS_COUNT, t3)
                                }
                            })
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{result -> viewState.appsCount(result)}
        addDisposable(disposable)
    }
}