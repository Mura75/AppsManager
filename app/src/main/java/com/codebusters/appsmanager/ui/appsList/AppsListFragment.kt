package com.codebusters.appsmanager.ui.appsList


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.afollestad.materialdialogs.MaterialDialog

import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.event.UpdateEvent
import com.codebusters.appsmanager.mvp.AppUtilsPresenter
import com.codebusters.appsmanager.mvp.AppUtilsView
import com.codebusters.appsmanager.utils.*
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.apps_list_fragment.*
import kotlinx.android.synthetic.main.items_recycler_view.*
import kotlinx.android.synthetic.main.items_recycler_view.view.*
import org.greenrobot.eventbus.EventBus
import java.text.FieldPosition


class AppsListFragment : MvpAppCompatFragment(), AppsListView, AppUtilsView {
    companion object {

        const val TAG = "Apps_fragment"

        fun newInstance(system : Boolean) : AppsListFragment {
            val fragment = AppsListFragment()
            val args = Bundle()
            args.putBoolean(Constants.SYSTEM_APP, system)
            fragment.arguments = args
            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: AppsListPresenter

    @ProvidePresenter
    fun providePresenter(): AppsListPresenter = AppsListPresenter(this)

    @InjectPresenter
    lateinit var utilsPresenter: AppUtilsPresenter

    @ProvidePresenter
    fun provideUtilsPresenter(): AppUtilsPresenter = AppUtilsPresenter(null, this)

    private var sortBy: Int = Constants.SORT_BY_NAME
    private var orderType: Int = Constants.ASCENDING

    private var appsAdapter: AppsListAdapter? = null

    //RecyclerView initialization
    private val appsRecyclerView by lazy {
        rvApps.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            addItemDecoration(
                    HorizontalDividerItemDecoration.Builder(activity)
                            .size(3)
                            .colorResId(android.R.color.darker_gray)
                            .build())
        }
    }

    //Swipe layout refresh listener
    private val swipeRefreshListener = object : SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            request(sortBy, orderType)
        }
    }

    //Apps recyclerView items click listener
    private var listener = object : RecyclerViewItemClickListener {

        override fun uninstallApp(position: Int) {
            log(TAG, "Uninstall")
            utilsPresenter.uninstallApp(position, appsAdapter?.get(position))
        }

        override fun extractApp(position: Int) {
            utilsPresenter.extractApk(appsAdapter?.get(position))
            log(TAG, "Extract")
        }

        override fun shareApp(position: Int) {
            utilsPresenter.shareApp(appsAdapter?.get(position))
            log(TAG, "Share")
        }

        override fun detailedAppInfo(position: Int) {
            utilsPresenter.openAppInfo(appsAdapter?.get(position))
        }

        override fun navigateToAppInfo(position: Int) {

        }
    }

    private var searchResultListener = object : SearchListener {
        override fun resultMessage(result: Boolean) {

        }
    }

    //SearchView listener
    private var searchListener = object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(p0: String?): Boolean = false

        override fun onQueryTextChange(search: String?): Boolean {
            Log.d(TAG, search)
            search?.apply {
                if (search.isEmpty())
                    (appsRecyclerView.adapter as AppsListAdapter).getFilter().filter("")
                else
                    (appsRecyclerView.adapter as AppsListAdapter).getFilter().filter(search.toLowerCase())
            }
            return false
        }
    }

    //Progress dialog initialization
    private var progressDialog : MaterialDialog ? = null

    /**
     * Get sort and order type values from preference
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sortBy = PrefUtils.getIntData(PrefUtils.SORT_BY, Constants.SORT_BY_NAME)
        orderType = PrefUtils.getIntData(PrefUtils.ORDER_TYPE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.apps_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        setAdapter()
        updateData()
    }

    /**
     * Bind views with current fragment
     */
    private fun bindViews() {
        srlApps.setOnRefreshListener(swipeRefreshListener)
        activity?.let {
            progressDialog = MaterialDialog.Builder(it)
                .content(R.string.extracting)
                .progress(true, 0)
                .build()
        }
    }

    private fun setAdapter() {
        appsAdapter = AppsListAdapter(context, listener, searchResultListener)
        appsRecyclerView.adapter = appsAdapter
        fastScroller.setRecyclerView(appsRecyclerView.rvApps)
    }

    /**
     * Update recycler view data
     */
    private fun updateData() {
        srlApps.post({ swipeRefreshListener.onRefresh() })
    }

    /**
     * Handle activity result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log(TAG, resultCode.toString() + " ---- " + requestCode)
        utilsPresenter.handleOnActivityResult(requestCode, resultCode, data)
    }


    /**
     * Create searchView item for searching item from list
     * and item for sorting apps list by some properties
     */
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search, menu)
        menu?.let {
            it.findItem(R.id.actions_search).let {
                val searchView = MenuItemCompat.getActionView(it) as? SearchView
                searchView?.let {
                    it.setOnQueryTextListener(searchListener)
                    val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as? SearchManager
                    it.setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))
                }
            }
            it.add(Menu.NONE, Constants.SORT_BY_NAME, 1, R.string.sort_apps_by)?.run {
                setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = activity?.resources?.getDrawable(R.drawable.ic_sort_by)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Open sort dialog on sort item click
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == 1) showAppsSortDialog()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Create single choice list dialog for apps sorting
     */
    private fun showAppsSortDialog() {
        activity?.let {
            val materialDialog = MaterialDialog.Builder(it)
                    .title(getString(R.string.sort_apps_by))
                    .items(getString(R.string.sort_by_name),
                            getString(R.string.sort_by_size),
                            getString(R.string.sort_by_installation_date),
                            getString(R.string.sort_by_update_date)
                    )
                    .alwaysCallSingleChoiceCallback()
                    .itemsCallbackSingleChoice(sortBy - 1,
                            { dialog, itemView, which, text ->
                                log(TAG, "chosen pos: $which")
                                when(which) {
                                    0 -> {
                                        sortBy = Constants.SORT_BY_NAME}
                                    1 -> {
                                        sortBy = Constants.SORT_BY_SIZE}
                                    2 -> {
                                        sortBy = Constants.SORT_BY_INSTALLATION_DATE}
                                    3 -> {
                                        sortBy = Constants.SORT_BY_UPDATE}
                                }
                                log(TAG, "sort value: $sortBy")
                                PrefUtils.saveData(PrefUtils.SORT_BY, sortBy)
                                true
                            })
                    .positiveText(getString(R.string.ascending))
                    .onPositive { dialog, which ->
                        PrefUtils.saveData(PrefUtils.ORDER_TYPE, Constants.ASCENDING)
                        orderType = Constants.ASCENDING
                        request(sortBy, orderType)
                    }
                    .negativeText(getString(R.string.descending))
                    .onNegative { dialog, which ->
                        PrefUtils.saveData(PrefUtils.ORDER_TYPE, Constants.DESCENDING)
                        orderType = Constants.DESCENDING
                        request(sortBy, orderType)
                    }
                    .build()
            materialDialog.show()
        }
    }

    private fun request(sortBy : Int = Constants.SORT_BY_NAME, orderType: Int = Constants.ASCENDING) {
        appsAdapter?.clear()
        arguments?.let {
            presenter.getAppsAsync2(it.getBoolean(Constants.SYSTEM_APP), sortBy, orderType)
        }
    }

    /**
     * Show loading
     */
    override fun showLoading() {
        srlApps.isRefreshing = true
    }

    /**
     * Hide loading
     */
    override fun hideLoading() {
        srlApps.isRefreshing = false
        progressDialog?.hide()
    }

    override fun showResult(result: List<PackageInfo>) {
        appsAdapter?.addAll(result)
    }

    override fun showResult(resultList: List<PackageInfo>, diffResult: DiffUtil.DiffResult) {
        appsAdapter?.addAll(resultList, diffResult)
    }


    override fun showExtractLoading() {
        progressDialog?.show()
    }

    /**
     * Update drawer after app extraction
     */
    override fun extractResult(result: String) {
        showSnackbar(coordinatorLayout, result)
        EventBus.getDefault().postSticky(UpdateEvent("updated"))
    }

    /**
     * Update list after app remove from system
     */
    override fun deletedResult(position: Int, item: PackageInfo) {
        log(TAG, "app delete from phone: " + position)
        EventBus.getDefault().postSticky(UpdateEvent(UpdateEvent.UPDATE))
        showSnackbar(coordinatorLayout, item?.getName(activity?.packageManager) + " is deleted")
        (appsRecyclerView.adapter as AppsListAdapter).remove(position, item)
    }
}
