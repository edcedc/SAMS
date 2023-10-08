package com.yyc.smas.ui.frg

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.yyc.smas.R
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.databinding.FAssetSearchBinding
import com.yyc.smas.ext.showToast
import com.yyc.smas.viewmodel.AssetModel
import com.yyc.smas.viewmodel.AssetSearchmModel
import com.yyc.smas.viewmodel.RfidModel
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/8/2 14:17
 * @Description
 */
class AssetSearchFrg: BaseFragment<AssetSearchmModel, FAssetSearchBinding>() {

    val rfidModel: RfidModel by activityViewModels()

    val assetSearchmModel: AssetSearchmModel by activityViewModels()

    var LabelTag: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            val jsonObject = JSONObject(it.getString("bean"))
            LabelTag = jsonObject.optString("LabelTag")
        }

        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()

        mDatabind.circularProgressBar.onProgressChangeListener = { progress ->
            runOnUiThread {
                mViewModel.rssi.set("${progress}")
            }
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                stop()
                mViewModel.isOpen.set(false)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                stop()
                mViewModel.isOpen.set(false)
                assetSearchmModel.epcData.value = null
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        assetSearchmModel.epcData.observe(viewLifecycleOwner, Observer {
            if (it == null)return@Observer
            val rssi = it.rssi?.replace("-", "")
            mDatabind.circularProgressBar.progress = rssi!!.toFloat()
            runOnUiThread {
                mDatabind.circularProgressBar.setProgressWithAnimation(rssi!!.toFloat(), 1000)
            }
        })
    }

    inner class ProxyClick() {

        fun start(){
            if (StringUtils.isEmpty(LabelTag) || LabelTag.equals("null")){
                showToast(getString(R.string.no_labelTag))
                return
            }
            if (mViewModel.isOpen.get()){
                stop()
            }else{
                rfidModel.isSearchOpen.value = LabelTag
                mViewModel.openStatus.set(getString(R.string.stop))
            }
            mViewModel.isOpen.set(!mViewModel.isOpen.get())
        }

    }

    private fun stop() {
        rfidModel.isSearchOpen.value = null
        mViewModel.openStatus.set(getString(R.string.start))
    }

}