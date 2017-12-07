package com.codebusters.appsmanager.ui.menu

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.codebusters.appsmanager.R
import com.codebusters.appsmanager.event.UpdateEvent
import com.codebusters.appsmanager.fragments.AboutFragment
import com.codebusters.appsmanager.fragments.SettingsFragment
import com.codebusters.appsmanager.ui.apksList.ApksListFragment
import com.codebusters.appsmanager.ui.appsList.AppsListFragment
import com.codebusters.appsmanager.utils.AppUtils
import com.codebusters.appsmanager.utils.Constants
import com.codebusters.appsmanager.utils.hideKeyboard
import com.codebusters.appsmanager.utils.log
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import kotlinx.android.synthetic.main.activity_menu.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
open class MenuActivity : MvpAppCompatActivity(), AppsCountView, FolderChooserDialog.FolderCallback {
    companion object {
        val TAG = "Menu_activity"
    }

    @InjectPresenter
    lateinit var presenter : AppsCountPresenter

    @ProvidePresenter
    fun providePresenter() = AppsCountPresenter(this)

    var drawer : Drawer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        initActionBar()
        initDrawer()
        storagePermissionGrantedWithPermissionCheck()
    }

    /**
     * Get apps count when user grand file read and write permissions
     */
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
    fun storagePermissionGranted() {
        presenter?.getAppCount()
    }

    /**
     * Show permission dialog when user deny permission dialog
     */
    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
    fun storagePermissionDenied() {
        storagePermissionGrantedWithPermissionCheck()
    }

    /**
     * Set toolbar as action bar
     */
    private fun initActionBar() {
        setSupportActionBar(toolbar)
    }

    /**
     * Init left drawer
     */
    private fun initDrawer() {
        val itemUserApp = PrimaryDrawerItem()
                .withIdentifier(1)
                .withName(R.string.user_app)
                .withIcon(R.drawable.ic_user_app)
                .withTintSelectedIcon(true)
                .withSelectedIconColorRes(R.color.colorPrimary)
                .withDisabledIconColorRes(android.R.color.darker_gray)
        val itemSystemApp = PrimaryDrawerItem()
                .withIdentifier(2)
                .withName(R.string.system_app)
                .withIcon(R.drawable.ic_android_apk)
                .withTintSelectedIcon(true)
                .withSelectedIconColorRes(R.color.colorPrimary)
                .withDisabledIconColorRes(android.R.color.darker_gray)
        val itemsExtracted = PrimaryDrawerItem()
                .withIdentifier(3)
                .withName(getString(R.string.extracted_apk))
                .withIcon(R.drawable.ic_archive)
                .withTintSelectedIcon(true)
                .withSelectedIconColorRes(R.color.colorPrimary)
                .withDisabledIconColorRes(android.R.color.darker_gray)
        val itemSettings = PrimaryDrawerItem()
                .withIdentifier(4)
                .withName(R.string.settings)
                .withIcon(R.drawable.ic_settings)
                .withTintSelectedIcon(true)
                .withSelectedIconColorRes(R.color.colorPrimary)
                .withDisabledIconColorRes(android.R.color.darker_gray)
        val itemAbout = PrimaryDrawerItem()
                .withIdentifier(5)
                .withName(R.string.about)
                .withIcon(R.drawable.ic_about)
                .withTintSelectedIcon(true)
                .withSelectedIconColorRes(R.color.colorPrimary)
                .withDisabledIconColorRes(android.R.color.darker_gray)

        val header = AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.ic_background_2)
                .build()

        val footerItem = PrimaryDrawerItem()
                .withIdentifier(99)
                .withName(getString(R.string.org_name).toUpperCase())
                .withSetSelected(false)
                .withEnabled(true)


        drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(header)
                .withHeaderDivider(true)
                .addDrawerItems(
                        itemUserApp,
                        itemSystemApp,
                        itemsExtracted,
                        itemSettings)
//                .addStickyDrawerItems(footerItem)
                .withOnDrawerItemClickListener {
                    view, position, drawerItem -> openFragments(drawerItem.identifier.toInt())
                }
                .withOnDrawerListener(object : Drawer.OnDrawerListener{
                    override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
                        hideKeyboard()
                    }

                    override fun onDrawerClosed(drawerView: View?) {
                        hideKeyboard()
                    }

                    override fun onDrawerOpened(drawerView: View?) {
                        hideKeyboard()
                    }
                })
                .build()
        drawer?.setSelection(itemUserApp, true)
    }

    /**
     * Open fragment by drawer item position
     */
    private fun openFragments(identifier: Int) : Boolean {
        when (identifier) {
            1 -> {
                createFragment(AppsListFragment.newInstance(false))
                supportActionBar?.title = getString(R.string.user_app)
            }
            2 -> {
                createFragment(AppsListFragment.newInstance(true))
                supportActionBar?.title = getString(R.string.system_app)
            }
            3 -> {
                createFragment(ApksListFragment.newInstance())
                supportActionBar?.title = getString(R.string.extracted_apk)
            }
            4 -> {
                createFragment(SettingsFragment.newInstance())
                supportActionBar?.title = getString(R.string.settings)
            }
            5 -> {
                createFragment(AboutFragment.newInstance())
                supportActionBar?.title = getString(R.string.about)
            }
        }
        return false
    }

    /**
     * Create fragment with delay
     */
    private fun createFragment(fragment : Fragment) {
        Handler().postDelayed({
            supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, fragment)
                    .commit()
        }, 350)
    }

    /**
     * Update drawer badge number when something change
     */
    @Subscribe(sticky = true, threadMode = ThreadMode.POSTING)
    fun updateListSticky(event : UpdateEvent) {
        log(TAG, event.data)
        storagePermissionGrantedWithPermissionCheck()
    }

    /**
     * Register Eventbus
     */
    override fun onResumeFragments() {
        super.onResumeFragments()
        EventBus.getDefault().register(this)
        val stickyEvent = EventBus.getDefault().getStickyEvent<UpdateEvent>(UpdateEvent::class.java)
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent)
        }
    }

    /**
     * Unregister Eventbus
     */
    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    /**
     * Handle permissions with permission dispatcher library
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    /**
     * Set value to badge
     */
    override fun appsCount(result: Map<String, Int>) {
        log("badge_number", result.toString())
        if (result.getValue(Constants.USER_APP) > 0)
                drawer?.updateBadge(1, StringHolder((result.getValue(Constants.USER_APP).toString())))
            else
                drawer?.updateBadge(1, StringHolder(""))

            if (result.getValue(Constants.SYSTEM_APP) > 0)
                drawer?.updateBadge(2, StringHolder((result.getValue(Constants.SYSTEM_APP).toString())))
            else
                drawer?.updateBadge(2, StringHolder(""))


            if (result.getValue(Constants.APKS_COUNT) > 0)
                drawer?.updateBadge(3, StringHolder((result.getValue(Constants.APKS_COUNT).toString())))
            else
                drawer?.updateBadge(3, StringHolder(""))
    }
    /**
     * Show permission dialog when user open folder chooser dialog in settings fragment
     */
    override fun onFolderSelection(dialog: FolderChooserDialog, folder: File) {
        AppUtils.setAppFolder(folder)
        storagePermissionGrantedWithPermissionCheck()
    }

    /**
     * Call when user deny permission dialog
     */
    override fun onFolderChooserDismissed(dialog: FolderChooserDialog) {
        //Do something
    }
}
