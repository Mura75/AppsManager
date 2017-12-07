package com.codebusters.appsmanager.utils


interface RecyclerViewItemClickListener {

    /**
     * Uninstall app by position in list
     */
    fun uninstallApp(position : Int)

    /**
     * Extract apk file by position in list
     */
    fun extractApp(position: Int)

    /**
     * Share app by position in list
     */
    fun shareApp(position: Int)

    /**
     * Open app detailed info by position
     */
    fun detailedAppInfo(position: Int)

    /**
     * Open detailed activity screen
     */
    fun navigateToAppInfo(position: Int)
}