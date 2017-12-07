package com.codebusters.appsmanager.event

data class UninstallEvent(val position : Int = -1) {

    companion object {
        const val UNINSTALL = "UNINSTALL"
    }

}
