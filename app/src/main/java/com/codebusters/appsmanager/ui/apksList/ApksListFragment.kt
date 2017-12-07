package com.codebusters.appsmanager.ui.apksList

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.event.UpdateEvent
import com.codebusters.appsmanager.models.ApkInfo
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.items_recycler_view.*
import org.greenrobot.eventbus.EventBus


class ApksListFragment : MvpAppCompatFragment(), ApkListView {

    companion object {

        const val TAG = "Apk_list_fragment"

        //Dafault fragment init
        fun newInstance() = ApksListFragment()
    }

    @InjectPresenter
    lateinit var presenter: ApkListPresenter

    @ProvidePresenter
    fun providePresenter() = ApkListPresenter(activity)

    //Adapter init
    var apkAdapter : ApksListAdapter? = null

    //RecyclerView init
    val apksRecyclerView by lazy {
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

    //Swipe refresh layout init
    val swipeRefreshListener = object : SwipeRefreshLayout.OnRefreshListener {
        override fun onRefresh() {
            apkAdapter?.clear()
            presenter.getApksListAsync()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.apps_list_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
        setData()
        setAdapter()
        updateData()
    }


    /**
     * Bind views with current fragment
     */
    private fun bindViews() {
        srlApps.setOnRefreshListener(swipeRefreshListener)
    }

    private fun setData() {

    }

    /**
     * Init apk list adapter
     */
    private fun setAdapter() {
        apkAdapter = ApksListAdapter()
        apksRecyclerView.adapter = apkAdapter
        fastScroller.setRecyclerView(apksRecyclerView)
    }

    /**
     * Update apk list data
     */
    private fun updateData() {
        srlApps.post({ swipeRefreshListener.onRefresh() })
    }


    /**
     * Create action bar maenu items
     */
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.let {
            it.add(Menu.NONE, 1, Menu.NONE, getString(R.string.delete_all))
            it.findItem(1)?.apply {
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                icon = resources.getDrawable(R.drawable.ic_delete_forever)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Delete all apks from memory
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            1 -> {
                presenter.getApksListAsync()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Show swipe layout loading
     */
    override fun showLoading() {
        srlApps.isRefreshing = true
    }

    /**
     * Hide swipe layout loading
     */
    override fun hideLoading() {
        srlApps.isRefreshing = false
    }

    override fun showResult(resultList: List<ApkInfo>, diffResult: DiffUtil.DiffResult) {
        Log.d(TAG, resultList.toString())
        apkAdapter?.addAll(resultList, diffResult)
        if (resultList.isEmpty()) {
            tvEmpty?.setText(R.string.folder_is_empty)
            tvEmpty?.visibility = View.VISIBLE
        }
        EventBus.getDefault().postSticky(UpdateEvent(UpdateEvent.UPDATE))
    }

    override fun showEmptyList() {
        apkAdapter?.apply {
            if (isEmpty()) {
                tvEmpty?.setText(R.string.folder_is_empty)
                tvEmpty?.visibility = View.VISIBLE
            }
        }
        EventBus.getDefault().postSticky(UpdateEvent(UpdateEvent.UPDATE))
    }
}