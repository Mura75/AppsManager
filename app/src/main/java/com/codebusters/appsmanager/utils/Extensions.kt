package com.codebusters.appsmanager.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.utils.base.BasePresenter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity's logcat extension
 */
fun Activity.log(tag : String, text : String) {
    Log.d(tag, text)
}

/**
 * Activity's keyboard extension to hide keyboard
 */
fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view = getCurrentFocus()
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
}

/**
 * Activity's snackbar extension
 */
fun Activity.showSnackbar(layout : CoordinatorLayout, text : String) {
    Snackbar.make(layout, text, Toast.LENGTH_LONG)
            .setActionTextColor(resources.getColor(R.color.colorAccent))
            .setDuration(3000)
            .show()
}

/**
 * Fragments's logcat extension
 */
fun Fragment.log(tag : String, text : String) {
    Log.d(tag, text)
}

/**
 * Fragment's toast extension
 */
fun Fragment.showToast(text : String) {
    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
}

/**
 * Fragment's keyboard extension to hide keyboard
 */
fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}

/**
 * Fragment's snackbar extension
 */
fun Fragment.showSnackbar(layout : CoordinatorLayout, text : String) {
    Snackbar.make(layout, text, Toast.LENGTH_LONG)
            .setActionTextColor(resources.getColor(R.color.colorAccent))
            .setDuration(3000)
            .show()
}




fun PackageInfo.getName(packageManager: PackageManager?) : String
        = packageManager?.getApplicationLabel(applicationInfo).toString()

fun PackageInfo.getIcon(context: Context, drawableId: Int) : Drawable
        = context.packageManager?.getApplicationIcon(applicationInfo)
        ?: context.resources.getDrawable(drawableId)

fun PackageInfo.getSize() : Long = File(applicationInfo.publicSourceDir).length()

fun PackageInfo.formattedSize(context: Context) : String
        = Formatter.formatFileSize(context, getSize())


fun PackageInfo.getData() : String = applicationInfo.dataDir

fun PackageInfo.apkPath() : String = applicationInfo.sourceDir

fun PackageInfo.getGrantedPermissions(packageManager: PackageManager?) : ArrayList<String> {
    val list = ArrayList<String>()
    val packageInfo = packageManager?.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
    packageInfo?.requestedPermissions?.forEach {
        list.add(it)
    }
    return list
}

fun PackageInfo.getInstallDate() : String {
    val date = Date(firstInstallTime as Long)
    val formatter = SimpleDateFormat(Constants.DD_MM_YYYY)
    return formatter.format(date)
}

fun PackageInfo.getUpdatedDate() : String {
    val date = Date(lastUpdateTime as Long)
    val formatter = SimpleDateFormat(Constants.DD_MM_YYYY)
    return formatter.format(date)
}

fun PackageInfo.isSystem() = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0








