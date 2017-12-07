package com.codebusters.appsmanager.mvp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.Fragment
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.utils.AppUtils
import com.codebusters.appsmanager.utils.Constants
import com.codebusters.appsmanager.utils.base.BasePresenter
import com.codebusters.appsmanager.utils.getName
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@InjectViewState
class AppUtilsPresenter() : BasePresenter<AppUtilsView>() {

    companion object {
        const val TAG = "App_utils_pres"
    }

    private var packageManager: PackageManager? = null

    private var activity: Activity? = null

    private var fragment: Fragment? = null

    private var uninstallApp: PackageInfo? = null
    private var uninstalledAppPosition = -1

    constructor(activity: Activity? = null, fragment: Fragment? = null) : this() {
        this.activity = activity
        this.fragment = fragment
        this.activity?.let {
            this.packageManager = it.packageManager
        }
        this.fragment?.let {
            this.packageManager = it.activity?.packageManager
        }
    }

    fun extractApk(packageInfo: PackageInfo?) {
        viewState.showExtractLoading()

        val disposable = Flowable.defer<Boolean> {
                    val extracted = AppUtils.extractApkFile(packageInfo, packageManager)
                    Flowable.just<Boolean>(extracted)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result -> run {
                    viewState.hideLoading()
                    if (result)
                        viewState.extractResult(packageInfo?.getName(packageManager) + " extracted.")
                    else
                        viewState.extractResult("Error of extraction")
                }
                }, {
                    error -> run {
                    viewState.hideLoading()
                    Log.d(TAG, "Extracting error: " + error.message)
                }
                })
        addDisposable(disposable)
    }


    /**
     * Uninstall app from device
     */
    fun uninstallApp(position: Int = -1, packageInfo: PackageInfo?)
            = with(Intent(Intent.ACTION_UNINSTALL_PACKAGE)) {
        uninstallApp = packageInfo
        uninstalledAppPosition = position
        data = Uri.parse("package:" + packageInfo?.packageName)
        putExtra(Intent.EXTRA_RETURN_RESULT, true)
        activity?.startActivityForResult(this, Constants.UNINSTALL_REQUEST_CODE)
                ?: fragment?.startActivityForResult(this, Constants.UNINSTALL_REQUEST_CODE)
    }

    /**
     * Share extracted apk files
     */
    fun shareApp(packageInfo: PackageInfo?) {
        Log.d(TAG, "Share app")
        viewState.showExtractLoading()

        val disposable = Flowable.defer<Boolean> {
                    val extracted = AppUtils.extractApkFile(packageInfo, packageManager)
                    Flowable.just<Boolean>(extracted)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result -> run {
                        viewState.hideLoading()
                        if (result) {
                            Intent().let {
                                it.action = Intent.ACTION_SEND
                                it.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(AppUtils.getOutputApkFile(packageInfo, packageManager)))
                                it.type = "application/vnd.android.package-archive"
                                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                fragment?.apply {
                                    Log.d(TAG, "Share vie fragment")
                                    val title = "${getString(R.string.share_with)} ${packageInfo?.getName(packageManager)}"
                                    startActivity(Intent.createChooser(it, title))
                                } ?: run {
                                    Log.d(TAG, "Share vie activity")
                                    val title = "${activity?.getString(R.string.share_with)} ${packageInfo?.getName(packageManager)}"
                                    activity?.startActivity(Intent.createChooser(it, title))
                                }
                            }
                        }
                    }
                }, { error -> run {
                        viewState.hideLoading()
                        Log.d(TAG, "Extracting error: " + error?.message)
                    }
                })
        addDisposable(disposable)
    }

    /**
     * Open settings app info
     */
    fun openAppInfo(packageInfo: PackageInfo??) = with(Intent()) {
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.parse("package:" + packageInfo?.packageName)
        fragment?.startActivity(this) ?: run { activity?.startActivity(this) }
    }

    fun handleOnActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        Log.d(TAG, resultCode.toString() + " ---- " + requestCode)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.UNINSTALL_REQUEST_CODE) {
                uninstallApp?.let {
                    Log.d(TAG, "uninstall exist: " + uninstalledAppPosition)
                    viewState.deletedResult(uninstalledAppPosition, it)
                    Log.d(TAG, "uninstall app not exist anymore:    " + it.packageName)
                } ?: run {
                    Log.d(TAG, "uninstall app not exist")
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED &&
                requestCode == Constants.UNINSTALL_REQUEST_CODE) {
            //Log.d(TAG, "uninstall cancel")
            //viewState.deletedResult(-1, uninstallApp!!)
        }
    }

}