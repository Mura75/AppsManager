package com.codebusters.appsmanager.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.utils.AppUtils

data class ApkInfo(val name: String, val size: Long, val icon : Drawable) {

    fun apkSize(context : Context) : String = Formatter.formatFileSize(context, size)

    override fun toString(): String {
        return "ApkInfo(name='$name', size=$size)"
    }
}