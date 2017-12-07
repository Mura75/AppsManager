package com.codebusters.appsmanager.ui.appsList

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.util.Log
import android.util.Pair
import com.arellomobile.mvp.InjectViewState
import com.codebusters.appsmanager.models.ApkInfo
import com.codebusters.appsmanager.ui.apksList.ApkListDiffCallback
import com.codebusters.appsmanager.ui.apksList.ApkListPresenter
import com.codebusters.appsmanager.utils.Constants
import com.codebusters.appsmanager.utils.base.BasePresenter
import com.codebusters.appsmanager.utils.getName
import com.codebusters.appsmanager.utils.getSize
import com.codebusters.appsmanager.utils.isSystem
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


@InjectViewState
class AppsListPresenter() : BasePresenter<AppsListView>() {

    companion object {
        private val TAG = "Apps_presenter";
    }

    private var fragment : Fragment? = null
    private var packageManager: PackageManager? = null

    private var userAppList: ArrayList<PackageInfo> = ArrayList()
    private var systemAppList : ArrayList<PackageInfo> = ArrayList()

    constructor(fragment: Fragment): this() {
        this.fragment = fragment
        this.packageManager = fragment.activity?.packageManager
    }

    /**
     * Get apps list from device and sort list by order and type
     */
    fun getAppsAsync2(system : Boolean, sortBy : Int = Constants.SORT_BY_NAME,
                     orderType : Int = Constants.ASCENDING) {
        viewState.showLoading()
        userAppList.clear()
        systemAppList.clear()
        val initPair: Pair<List<PackageInfo>, DiffUtil.DiffResult> = Pair.create(emptyList(), null)
        val disposable = Flowable.just(packageManager)
                .flatMap { packageManager ->
                    val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
                    packages?.apply {
                        for (packageInfo in packages) {
                            if (packageInfo.isSystem())
                                systemAppList.add(packageInfo)
                            else userAppList.add(packageInfo)
                        }
                    }
                    if (system) {
                        sortBy(systemAppList, sortBy, orderType)
                        Flowable.just(systemAppList)
                    }
                    else {
                        sortBy(userAppList, sortBy, orderType)
                        Flowable.just(userAppList)
                    }
                }
                .scan(initPair, { pair, next ->
                    Log.d(TAG, "Next:   " + next)
                    val callback = AppListDiffCallback(pair.first, next)
                    val result = DiffUtil.calculateDiff(callback)
                    Pair.create(next, result)
                })
                .skip(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result -> run {
                    Log.d(TAG, result.toString())
                    viewState.showResult(result.first, result.second)
                    viewState.hideLoading()
                }
                }, { error -> run {
                    viewState.hideLoading()
                    Log.w(TAG, error.message)
                }
                })
        addDisposable(disposable)
    }


    /**
     * Sort apps list
     */
    private fun sortBy(appInfos : List<PackageInfo>,
                       sortBy: Int,
                       orderType: Int = Constants.ASCENDING) : List<PackageInfo> {
        when(sortBy) {
            Constants.SORT_BY_NAME -> {
                Collections.sort(appInfos) { p1, p2 ->
                    if (orderType.equals(Constants.DESCENDING))
                        p2.getName(packageManager).toLowerCase()
                                .compareTo(p1.getName(packageManager).toLowerCase())
                    else
                        p1.getName(packageManager).toLowerCase()
                                .compareTo(p2.getName(packageManager).toLowerCase())
                }
            }
            Constants.SORT_BY_SIZE -> {
                Collections.sort(appInfos) { p1, p2 ->
                    if (orderType.equals(Constants.DESCENDING))
                        p2.getSize().compareTo(p1.getSize())
                    else
                        p1.getSize().compareTo(p2.getSize())
                }
            }
            Constants.SORT_BY_INSTALLATION_DATE -> {
                Collections.sort(appInfos) { p1, p2 ->
                    if (orderType.equals(Constants.DESCENDING))
                        p2.firstInstallTime.compareTo(p1.firstInstallTime)
                    else
                        p1.firstInstallTime.compareTo(p2.firstInstallTime)
                }
            }
            Constants.SORT_BY_UPDATE -> {
                Collections.sort(appInfos) { p1, p2 ->
                    if (orderType.equals(Constants.DESCENDING))
                        p2.lastUpdateTime.compareTo(p1.lastUpdateTime)
                    else
                        p1.lastUpdateTime.compareTo(p2.lastUpdateTime)
                }
            }
        }
        return appInfos
    }
}