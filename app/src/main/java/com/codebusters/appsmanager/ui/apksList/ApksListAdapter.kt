package com.codebusters.appsmanager.ui.apksList

import android.content.Context
import android.content.pm.PackageInfo
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.models.ApkInfo
import com.codebusters.appsmanager.ui.appsList.AppListDiffCallback
import com.codebusters.appsmanager.utils.getName
import kotlinx.android.synthetic.main.item_row_apk_layout.view.*

open class ApksListAdapter() : RecyclerView.Adapter<ApksListAdapter.ApksViewHolder>() {

    private var apksList: ArrayList<ApkInfo>? = null

    init {
        apksList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ApksViewHolder {
        val row = LayoutInflater.from(parent?.context).inflate(R.layout.item_row_apk_layout, parent, false)
        return ApksViewHolder(row)
    }

    override fun onBindViewHolder(holder: ApksViewHolder?, position: Int) {
        val item = apksList?.get(position)
        holder?.bind(item)
    }

    override fun getItemCount(): Int = apksList?.size ?: 0

    fun addAll(list: List<ApkInfo>, diffResult: DiffUtil.DiffResult) {
        apksList?.let {
            diffResult.dispatchUpdatesTo(this@ApksListAdapter)
            it.clear()
            it.addAll(list)
        }
    }

    fun isEmpty() = apksList?.isEmpty() ?: true

    fun clear() {
        apksList?.clear()
        notifyDataSetChanged()
    }

    open class ApksViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind view with data
         */
        fun bind(item: ApkInfo?) = with(itemView) {
            item?.let {
                tvApkName.text = it.name
                tvApkSize.text = it.apkSize(context)
                ivApk.setImageDrawable(it.icon)
            }
        }
    }
}