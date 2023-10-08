package com.yyc.smas.ui.frg

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.kingja.loadsir.core.LoadService
import com.yyc.smas.R
import com.yyc.smas.adapter.AssetAdapter
import com.yyc.smas.adapter.UploadAdapter
import com.yyc.smas.api.UIHelper
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.bean.db.UploadOrderBean
import com.yyc.smas.databinding.BTitleRecyclerBinding
import com.yyc.smas.ext.init
import com.yyc.smas.ext.initClose
import com.yyc.smas.ext.loadServiceInit
import com.yyc.smas.ext.setNbOnItemClickListener
import com.yyc.smas.ext.showEmpty
import com.yyc.smas.ext.showLoading
import com.yyc.smas.viewmodel.UploadModel
import com.yyc.smas.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav
import me.hgj.jetpackmvvm.ext.navigateAction

/**
 * @Author nike
 * @Date 2023/8/7 15:56
 * @Description 上传
 */
class UploadFrg: BaseFragment<UploadModel,BTitleRecyclerBinding>(){

    val adapter: UploadAdapter by lazy { UploadAdapter(arrayListOf()) }

    lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.includeToolbar.toolbar.initClose(getString(R.string.upload)) {nav().navigateUp()}
        mDatabind.swipeRefresh.isEnabled = false
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            addChildClickViewIds(R.id.btn_commit)
            setOnItemChildClickListener { adapter, view, position ->
                val bean = adapter.data[position] as UploadOrderBean
                if (bean.status == 1)return@setOnItemChildClickListener
                mViewModel.UploadStockTake(bean)
            }
        }
        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {

        }
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess){
                adapter.setList(it.listData.reversed())
                loadsir.showSuccess()
            }else{
                loadsir.showEmpty()
            }
        })
        mViewModel.uploadBean.observe(viewLifecycleOwner, {
            adapter.data.forEachIndexed { index, uploadOrderBean ->
                if (uploadOrderBean.uid.equals(it.uid)){
                    adapter.setData(index, it)
                }
            }
        })
        mViewModel.isShowDialog.observe(viewLifecycleOwner, {
            if (it){
                showLoading()
            }else{
                dismissLoading()
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
        loadsir.showLoading()
        mViewModel.onRequest()
    }

}