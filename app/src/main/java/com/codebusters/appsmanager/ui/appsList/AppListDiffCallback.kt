package com.codebusters.appsmanager.ui.appsList

import android.content.pm.PackageInfo
import android.support.v7.util.DiffUtil

open class AppListDiffCallback(var oldList: List<PackageInfo>, var newList: List<PackageInfo>) : DiffUtil.Callback() {

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
