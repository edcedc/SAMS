package com.yyc.smas.api

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.Gson
import com.yyc.smas.MainActivity
import com.yyc.smas.R
import com.yyc.smas.bean.DataBean
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.ui.act.LoginAct
import com.yyc.smas.ui.act.ZxingAct
import me.hgj.jetpackmvvm.ext.navigateAction


/**
 * Created by Administrator on 2017/2/22.
 */

class UIHelper private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }


    companion object {

        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)  // 设置进入动画
            .setExitAnim(R.anim.slide_out_left)  // 设置退出动画
            .setPopEnterAnim(R.anim.slide_in_left)  // 设置返回动画
            .setPopExitAnim(R.anim.slide_out_right)  // 设置返回退出动画
            .build()

        fun startMainAct() {
            ActivityUtils.startActivity(MainActivity::class.java)
        }

        /**
         *  二维码
         */
        fun startZxingAct(type: Int) {
            val bundle = Bundle()
            bundle.putInt("type", type)
            ActivityUtils.startActivity(bundle, ZxingAct::class.java)
        }

        /**
         *  登录
         */
        fun startLoginAct() {
            ActivityUtils.startActivity(LoginAct::class.java)
        }

        /**
         *  设置
         */
        fun startSettingFrg(nav: NavController) {
            val bundle = Bundle()
            nav.navigateAction(R.id.action_loginfragment_to_settingFrg, bundle)
        }

        /**
         *  下载
         */
        fun startDownloadFrg(nav: NavController) {
            val bundle = Bundle()
            nav.navigateAction(R.id.action_orderFrg_to_downloadFrg, bundle)
        }

        /**
         *  RFID 列表
         */
        fun startAssetFrg(nav: NavController, orderId: String) {
            val bundle = Bundle()
            bundle.putString("orderId", orderId)
            nav.navigateAction(R.id.action_orderFrg_to_assetFrg, bundle)
        }

        /**
         *  RFID 详情
         */
        fun startAssetDetailsFrg(nav: NavController, bean: AssetBean) {
            val bundle = Bundle()
            bundle.putString("bean", Gson().toJson(bean))
            nav.navigateAction(R.id.action_assetFrg_to_assetDetailsFrg, bundle)
        }

        /**
         *  上传
         */
        fun starUploadFrg(nav: NavController) {
            val bundle = Bundle()
            nav.navigateAction(R.id.action_orderFrg_to_uploadFrg, bundle)
        }

        /**
         *  外部借出
         */
        fun starExternalBaorrowFrg(nav: NavController) {
            val bundle = Bundle()
            nav.navigateAction(R.id.action_orderFrg_to_externalBaorrowFrg, bundle)
        }

        /**
         *  内部借出
         */
        fun starInternalBaorrowFrg(nav: NavController) {
            val bundle = Bundle()
            nav.navigateAction(R.id.action_orderFrg_to_internalBaorrowFrg, bundle)
        }

        /**
         *  内部借出
         */
        fun starDisposalFrg(nav: NavController) {
            val bundle = Bundle()
            nav.navigateAction(R.id.action_orderFrg_to_disposalFrg, bundle)
        }

        /**
         *  注销2
         */
        fun starDisposalFrg2(nav: NavController, orderNo: String?, title: String) {
            val bundle = Bundle()
            bundle.putString("orderId", orderNo)
            bundle.putString("title", title)
            nav.navigateAction(R.id.action_disposalFrg_to_disposal2Frg, bundle)
        }

        /**
         *  内部 借出2
         */
        fun starExternalFrg2(nav: NavController, orderNo: String?, title: String) {
            val bundle = Bundle()
            bundle.putString("orderId", orderNo)
            bundle.putString("title", title)
            nav.navigateAction(R.id.action_externalFrg_to_externalFrg2, bundle)
        }

        /**
         *  注销订单详情
         */
        fun startDisposalDetailsFrg(nav: NavController, bean: DataBean, title: String?) {
            val bundle = Bundle()
            bundle.putString("bean", Gson().toJson(bean))
            bundle.putString("title", title)
            nav.navigate(R.id.disposalDetailsFrg, bundle, navOptions)
        }

    }
}

