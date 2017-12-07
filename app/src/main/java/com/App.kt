package com

import android.app.Application
import android.support.v7.app.AppCompatDelegate
import com.codebusters.appsmanager.utils.PrefUtils

/**
 * Created by Murager on 11/11/17.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        PrefUtils.init(this)
    }
}
