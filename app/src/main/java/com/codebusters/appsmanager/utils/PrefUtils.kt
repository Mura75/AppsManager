package com.codebusters.appsmanager.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


open class PrefUtils {

    companion object {

        // Folder path key
        val FOLDER_PATH = "prefFolderPath"

        //App name type key
        val APK_NAME_TYPE = "prefApkNameType"

        val SORT_BY = "sort_by"

        val ORDER_TYPE = "order_type"

        private lateinit var  preference: SharedPreferences
        private lateinit var  editor: SharedPreferences.Editor

        /**
         * Default constructor
         */
        fun init(context: Context) {
            preference = PreferenceManager.getDefaultSharedPreferences(context)
            editor = preference.edit()
        }

        /**
         * Save string data to preference by key
         */
        fun saveData(key :String, value : String) {
            editor.putString(key, value)?.apply()
        }

        /**
         * Save int data to preference by key
         */
        fun saveData(key: String, value: Int) {
            editor.putInt(key, value)?.apply()
        }

        /**
         * Get string data from preference by key
         */
        fun getStringData(key : String) : String = preference.getString(key, "")

        /**
         * Get int data from preference by key
         */
        fun getIntData(key: String) : Int = preference.getInt(key, 0)

        /**
         * Get int data with default value from preference
         */
        fun getIntData(key: String, defaultValue: Int) = preference.getInt(key, defaultValue)

        /**
         * Set preference listener when data change
         */
        fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            preference.registerOnSharedPreferenceChangeListener(listener)
        }
    }
}