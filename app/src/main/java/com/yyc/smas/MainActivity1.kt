package com.yyc.smas

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.navigation.NavigationView
import com.yyc.smas.api.UIHelper
import com.yyc.smas.base.BaseActivity
import com.yyc.smas.databinding.AMain1Binding
import com.yyc.smas.databinding.AMainBinding
import com.yyc.smas.ext.init
import com.yyc.smas.ext.initClose
import com.yyc.smas.util.SettingUtil
import com.yyc.smas.viewmodel.RfidModel
import me.hgj.jetpackmvvm.base.activity.BaseVmDbActivity
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.navigateAction

/**
 * @Author nike
 * @Date 2023/8/8 16:39
 * @Description
 */
class MainActivity1 : BaseActivity<RfidModel, AMain1Binding>(), NavigationView.OnNavigationItemSelectedListener {

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mDatabind.drawerLayout.postDelayed({
            val navController = findNavController(R.id.nav_host_fragment)

            when (item.itemId) {
                R.id.nav_load -> {

                }

                R.id.nav_upload -> {

                }

                R.id.nav_external_borrow -> {
//                    toolbar?.run {
//                        init(getString(R.string.external_borrow))
//                    }
                    navController.popBackStack()
                    navController.navigate(R.id.externalFrg)
                }

                R.id.nav_internal_borrow -> {
//                    toolbar?.run {
//                        init(getString(R.string.internal_borrow))
//                    }
                    navController.popBackStack()
                    navController.navigate(R.id.internalBaorrowFrg)
                }

                R.id.nav_disposal -> {
//                    toolbar?.run {
//                        init(getString(R.string.detailed))
//                    }
                    navController.popBackStack()
                    navController.navigate(R.id.disposalFrg)
                }

                R.id.nav_login -> {
                    UIHelper.startLoginAct()
                }
            }
            mDatabind.drawerLayout.closeDrawer(GravityCompat.END)
        }, 300)
        return true
    }

    //region  抽屉布局
    fun onOpenDrawer() {
        if (!mDatabind.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDatabind.drawerLayout.openDrawer(GravityCompat.END)
        }else{
            mDatabind.drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

}