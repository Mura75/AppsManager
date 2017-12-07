package com.codebusters.appsmanager.ui.apksList

import android.content.pm.PackageInfo
import android.support.v7.util.DiffUtil
import com.codebusters.appsmanager.models.ApkInfo


open class ApkListDiffCallback(var oldList: List<ApkInfo>, var newList: List<ApkInfo>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList.get(oldItemPosition).hashCode() ?: -1
        val newItem = newList.get(newItemPosition).hashCode() ?: -1
        return oldItem.equals(newItem)
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList.get(oldItemPosition)
        val newItem = newList.get(newItemPosition)
        return oldItem.equals(newItem) ?: false
    }
}