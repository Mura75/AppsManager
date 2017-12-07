package com.codebusters.appsmanager.ui.appsList

import android.content.Context
import android.content.pm.PackageInfo
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.PopupMenu
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.mvp.AppUtilsPresenter
import com.codebusters.appsmanager.utils.*
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import kotlinx.android.synthetic.main.item_row_app_layout.view.*
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.codebusters.appsmanager.mvp.AppUtilsView


class AppsListAdapter() : RecyclerView.Adapter<AppsListAdapter.AppsViewHolder>(),
        Filterable, SectionTitleProvider {

    private val TAG = "Apps_adapter"

    private var context: Context? = null

    private var fragment: Fragment? = null

    private var appSortedList: ArrayList<PackageInfo>? = null

    //Apps filtered list after search
    private var appListSearch : ArrayList<PackageInfo>? = null

    //Apps search listener
    private var searchListener : SearchListener? = null

    //Apps item click listener
    private var listener : RecyclerViewItemClickListener? = null

    init {
        appSortedList = ArrayList()
    }

    constructor(context: Context?,
                listener: RecyclerViewItemClickListener,
                searchListener: SearchListener) : this() {
        this.context = context
        this.listener = listener
        this.searchListener = searchListener
    }

    constructor(fragment: Fragment?): this() {
        this.fragment = fragment
        this.context = fragment?.context
    }


    fun addAll(list: List<PackageInfo>) {
        appSortedList?.let {
            val diffResult = DiffUtil.calculateDiff(AppListDiffCallback(it, list), true)
            diffResult.dispatchUpdatesTo(this@AppsListAdapter)
            it.clear()
            it.addAll(list)
        }
    }


    fun addAll(list: List<PackageInfo>, diffResult: DiffUtil.DiffResult) {
        appSortedList?.let {
            diffResult.dispatchUpdatesTo(this@AppsListAdapter)
            it.clear()
            it.addAll(list)
        }
    }

    fun add(item: PackageInfo) {
        appSortedList?.add(item)
    }

    fun get(position: Int): PackageInfo? = appSortedList?.get(position)

    fun updateItemAt(position: Int, item: PackageInfo) {
        appSortedList?.set(position, item)
        notifyItemChanged(position, item)
    }

    fun remove(position: Int, item: PackageInfo?) {
        appSortedList?.remove(item)
        notifyItemRemoved(position)
    }

    fun removeItemAt(position: Int) {
        appSortedList?.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        appSortedList?.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = appSortedList?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AppsViewHolder {
        val row = LayoutInflater.from(parent?.context).inflate(R.layout.item_row_app_layout, parent, false)
        return AppsViewHolder(row)
    }

    override fun onBindViewHolder(holder: AppsViewHolder?, position: Int) {
        val item = appSortedList?.get(position)
        holder?.bind(item, listener)
    }


    /**
     * Filter list with searchView
     */
    override fun getFilter() : Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val oReturn = Filter.FilterResults()
                val results = ArrayList<PackageInfo>()
                if (appListSearch == null) {
                    appListSearch = appSortedList
                }
                if (charSequence != null) {
                    if (appListSearch?.size!! > 0) {
                        for (appInfo in appListSearch!!) {
                            if (appInfo.getName(context?.packageManager)
                                    .toLowerCase().contains(charSequence.toString())) {
                                results.add(appInfo)
                            }
                        }
                    }
                    oReturn.values = results
                    oReturn.count = results.size
                }
                return oReturn
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                val result : Boolean
                if (filterResults.count > 0) {
                    Log.d(TAG, "size more than 0")
                    result = false
                } else {
                    Log.d(TAG, "size less than 0")
                    result = true
                }
                searchListener?.resultMessage(result)
                appSortedList = filterResults.values as ArrayList<PackageInfo>
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Get first letter of app name for fast scroll
     */
    override fun getSectionTitle(position: Int): String =
            if (position > -1)
                appSortedList?.get(position)?.getName(context?.packageManager)?.substring(0, 1) ?: ""
            else ""



    open class AppsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind views with data
         */
        fun bind(packageInfo: PackageInfo?, listener: RecyclerViewItemClickListener?) = with(itemView) {
            ivApp.setImageDrawable(packageInfo?.getIcon(itemView.context, R.mipmap.ic_launcher))
            tvAppName.text = packageInfo?.getName(itemView?.context?.packageManager) + ""
            tvPackageSize.text = packageInfo?.formattedSize(context) + ""
            tvPackageName.text = packageInfo?.packageName + ""
            tvPackageInsatalledDate.text = "${context?.getString(R.string.installed_date)} ${packageInfo?.getInstallDate()}"
            tvPackageUpdateDate.text = "${context?.getString(R.string.updated_date)} ${packageInfo?.getUpdatedDate()}"

            itemView.setOnClickListener {
                listener?.navigateToAppInfo(adapterPosition)
            }

            ivPopupMenu.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, ivPopupMenu)
                popupMenu.run {
                    menu.run {
                        add(Menu.NONE, 1, Menu.NONE, R.string.share)
                        add(Menu.NONE, 2, Menu.NONE, R.string.extract)
                        add(Menu.NONE, 3, Menu.NONE, R.string.info)
                        packageInfo?.apply {
                            if (!isSystem()) add(Menu.NONE, 4, Menu.NONE, R.string.delete)
                        }
                    }
                    setOnMenuItemClickListener { item ->
                        listener?.run {
                            when(item?.itemId) {
                                1 -> shareApp(adapterPosition)
                                2 -> extractApp(adapterPosition)
                                3 -> detailedAppInfo(adapterPosition)
                                4 -> uninstallApp(adapterPosition)
                            }
                        }
                        true
                    }
                }
                popupMenu.show()
            }
        }
    }
}