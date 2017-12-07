package com.codebusters.appsmanager.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Environment
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException


object AppUtils {

    /**
     * Default extract folder path
     */
    private var defaultFolder = File(Environment.getExternalStorageDirectory().absolutePath +
            "/" + Constants.FOLDER_NAME)

    /**
     * Create folder on App first start
     */
    fun createFolderOnStart() = defaultFolder

    /**
     * Get extract file from dafault folder or from custom folder
     */
    fun getAppDefaultFolder() : File {
        if (PrefUtils.getStringData(PrefUtils.FOLDER_PATH).isEmpty()) {
            return defaultFolder
        }
        else {
            return File(PrefUtils.getStringData(PrefUtils.FOLDER_PATH))
        }
    }

    fun setAppFolder(url : String) {
        defaultFolder = File(url)
    }

    /**
     * Set custom folder and save folder path in preference
     */
    fun setAppFolder(file : File) {
        PrefUtils.saveData(PrefUtils.FOLDER_PATH, file.absolutePath)
        defaultFolder = file
    }

    /**
     * User set extracted apk file name type
     */
    fun setApkFileNameType(type: Int) {
        PrefUtils.saveData(PrefUtils.APK_NAME_TYPE, type)
    }

    /**
     * Get extracted apk file name by selected type
     */
    fun getApkFileName(packageInfo: PackageInfo?, packageManager: PackageManager?) : String {
        when(getApkFileNameType()) {
            0 -> return packageInfo?.getName(packageManager) + "_v_" + packageInfo?.versionName
            1 -> return packageInfo?.packageName + "_v_" + packageInfo?.versionName
            2 -> return packageInfo?.packageName ?: ""
            3 -> return  packageInfo?.getName(packageManager) ?: ""
            else  -> return ""
        }
    }

    /**
     * Get extracted apk file type from preference
     */
    fun getApkFileNameType(): Int = PrefUtils.getIntData(PrefUtils.APK_NAME_TYPE)

    /**
     * Return output app file with .apk extension
     */
    fun getOutputApkFile(packageInfo: PackageInfo?, packageManager: PackageManager?) : File =
            File(getAppDefaultFolder().path + "/" + getApkFileName(packageInfo, packageManager) + ".apk")

    /**
     * Check if app extracted correctly
     */
    fun extractApkFile(packageInfo: PackageInfo?, packageManager: PackageManager?) : Boolean {
        var isExtracted = false
        val initFile = File(packageInfo?.applicationInfo?.sourceDir)
        val targetFile = getOutputApkFile(packageInfo, packageManager)
        try {
            FileUtils.copyFile(initFile, targetFile)
            isExtracted = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return isExtracted
    }
}