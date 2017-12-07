package com.codebusters.appsmanager.event


data class UpdateEvent(val data : String) {

    companion object {
        const val UPDATE = "UPDATE"
    }

}