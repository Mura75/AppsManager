package com.codebusters.appsmanager.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog
import com.codebusters.appsmanager.BuildConfig
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.ui.menu.MenuActivity
import com.codebusters.appsmanager.utils.AppUtils
import com.codebusters.appsmanager.utils.PrefUtils
import de.psdev.licensesdialog.LicensesDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.licenses.MITLicense
import de.psdev.licensesdialog.model.Notice
import de.psdev.licensesdialog.model.Notices


class SettingsFragment : PreferenceFragmentCompat() {

    private val TAG = "Settings_fragment"

    private var prefCustomPath : Preference? = null
    private var prefCustomName : Preference? = null
    private var prefLicense : Preference? = null
    private var prefAppVersion : Preference? = null

    //lListener use to detect changes in preference file
    var preferenceListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(sharedPref: SharedPreferences?, key: String?) {
            val pref = findPreference(key)
            Log.d(TAG, key)
            when (pref) {
                prefCustomPath -> setCustomPath()
                prefCustomName -> {
                    val pos = PrefUtils.getIntData(PrefUtils.APK_NAME_TYPE)
                    prefCustomName?.setSummary(resources.getStringArray(R.array.filenames_array)[pos])
                }
            }
        }
    }

    companion object {
        //Fragment default init
        fun newInstance() = SettingsFragment()
    }

    /**
     * Set action bar to current fragment
     * and init preference
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        PrefUtils.registerListener(preferenceListener)
    }

    /**
     * Get preference settings from xml file
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindPrefs()
    }

    /**
     * Bind preference data with current file
     */
    private fun bindPrefs() {
        prefCustomPath = findPreference(PrefUtils.FOLDER_PATH)
        prefCustomPath?.apply {
            setCustomPath()
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val folderChooserDialog = FolderChooserDialog
                        .Builder(activity as MenuActivity)
                        .allowNewFolder(true, R.string.new_folder)
                        .cancelButton(R.string.cancel)
                        .chooseButton(R.string.ok)
                        .initialPath(AppUtils.getAppDefaultFolder().absolutePath)
                        .goUpLabel("...")
                        .build()
                folderChooserDialog.show(activity)
                false
            }
        }

        prefCustomName = findPreference(PrefUtils.APK_NAME_TYPE)
        val position = PrefUtils.getIntData(PrefUtils.APK_NAME_TYPE)
        val name = resources.getStringArray(R.array.filenames_array)[position]
        context?.let {
            val fileNameChooserDialog = MaterialDialog.Builder(it)
                    .items(R.array.filenames_array)
                    .itemsCallbackSingleChoice(position)
                    { dialog, itemView, which, text ->
                        AppUtils.setApkFileNameType(which)
                        true
                    }
                    .build()
            prefCustomName?.apply {
                summary = name
                onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    fileNameChooserDialog.show()
                    false
                }
            }

            prefLicense = findPreference("prefLicense")
            prefLicense?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                showLicenseDialog()
                false
            }

            prefAppVersion = findPreference("prefAppVersion")
            prefAppVersion?.title = "App version: " + BuildConfig.VERSION_NAME
        }
    }

    /**
     * Set custom path to extracted apk files
     */
    private fun setCustomPath() {
        val path = PrefUtils.getStringData(PrefUtils.FOLDER_PATH)
        if (path.isEmpty()) {
            prefCustomPath?.setSummary("Default: " + AppUtils.getAppDefaultFolder().path)
        }
        else {
            prefCustomPath?.setSummary(path)
        }
    }

    /**
     * Init license dialog and show it
     */
    private fun showLicenseDialog() {
        val notices = Notices()
        notices.apply {
            addNotice(Notice("Moxy",
                    "https://github.com/Arello-Mobile/Moxy/wiki",
                    "Copyright (c) 2016 Arello Mobile",
                    MITLicense()))
            addNotice(Notice("Eventbus",
                    "http://greenrobot.org/eventbus/",
                    "Copyright (C) 2012-2016 Markus Junginger",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("RxJava",
                    "https://github.com/ReactiveX/RxJava",
                    "Copyright (c) 2016-present, RxJava Contributors",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("RxAndroid",
                    "https://github.com/ReactiveX/RxAndroid",
                    "Copyright 2015 The RxAndroid authors",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("PermissionsDispatcher",
                    "https://permissions-dispatcher.github.io/PermissionsDispatcher",
                    "Copyright 2016 Shintaro Katafuchi, Marcel Schnelle, Yoshinori Isogai",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("material-dialogs",
                    "https://github.com/afollestad/material-dialogs",
                    "Copyright (c) 2014-2016 Aidan Michael Follestad",
                    MITLicense()))
            addNotice(Notice("MaterialDrawer",
                    "http://mikepenz.github.io/MaterialDrawer",
                    "Copyright 2016 Mike Penz",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("LicensesDialog",
                    "https://psdev.de/LicensesDialog",
                    "Copyright 2013-2017 Philip Schiffer",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("Commons IO",
                    "https://commons.apache.org/proper/commons-io",
                    "Apache Commons IO",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("RecyclerView-FlexibleDivider",
                    "https://github.com/yqritc/RecyclerView-FlexibleDivider",
                    "Copyright 2016 yqritc",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("AppCompat-v7",
                    "https://developer.android.com/topic/libraries/support-library/packages.html",
                    "Example Person",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("Support-v4",
                    "https://developer.android.com/topic/libraries/support-library/features.html#4",
                    "Copyright (C) 2011 The Android Open Source Project",
                    ApacheSoftwareLicense20()))
            addNotice(Notice("RecyclerView-v7",
                    "https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html",
                    "Copyright (C) 2013 The Android Open Source Project",
                    ApacheSoftwareLicense20()))
        }
        LicensesDialog.Builder(activity)
                .setNotices(notices)
                .build()
                .showAppCompat()
    }
}