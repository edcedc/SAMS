package com.yyc.smas.ui.frg

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yyc.smas.R
import com.yyc.smas.api.UIHelper
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.databinding.FExternalBaorrow2Binding
import com.yyc.smas.ext.EXTERNAL_ARCHIVES_TYPE
import com.yyc.smas.ext.EXTERNAL_BOOK_TYPE
import com.yyc.smas.ext.bindViewPager2
import com.yyc.smas.ext.init
import com.yyc.smas.ext.initClose
import com.yyc.smas.viewmodel.ExternalModel
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/27 11:43
 * @Description RFID 列表
 */
class ExternalFrg2: BaseFragment<ExternalModel, FExternalBaorrow2Binding>() {

    private val externalModel: ExternalModel by activityViewModels()

    var orderId: String? = ""

    var title: String? = ""

    //fragment集合
    var fragments: ArrayList<Fragment> = arrayListOf()

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
            title = it.getString("title")
            mDatabind.includeToolbar.toolbar.initClose(title!!) {nav().navigateUp()}
        }
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()
        mViewModel.state.set(getString(R.string.qRCode))
        mViewModel.save.set(getString(R.string.submit))

        //初始化viewpager2
        var bundle = Bundle()
        bundle.putString("orderId", orderId)
        bundle.putString("title", title)

        val bookFrg = ExternalBookFrg()
        bookFrg.arguments = bundle
        fragments.add(bookFrg)

        val archivesFrg = ExternalArchivesFrg()
        archivesFrg.arguments = bundle
        fragments.add(archivesFrg)

        mDatabind.includeViewpager.viewPager.init(this, fragments)
        var mTitles =
            arrayListOf(
            getString(R.string.book),
            getString(R.string.archives),
        )

        mDatabind.includeViewpager.magicIndicator.bindViewPager2(mDatabind.includeViewpager.viewPager, mTitles)
        mDatabind.includeViewpager.viewPager.offscreenPageLimit = mTitles.size


        mDatabind.includeSearch.ivQr.visibility = View.GONE

        mDatabind.includeSearch.etText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                externalModel.searchText.value = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                externalModel.submitType.value = -1
            }
        })
    }

    inner class ProxyClick() {

        fun state(){
            UIHelper.startZxingAct(if (mDatabind.includeViewpager.viewPager.currentItem == 0) EXTERNAL_BOOK_TYPE else EXTERNAL_ARCHIVES_TYPE)
        }

        fun save(){
            externalModel.submitType.value = if (mDatabind.includeViewpager.viewPager.currentItem == 0) EXTERNAL_BOOK_TYPE else EXTERNAL_ARCHIVES_TYPE
        }

    }

}