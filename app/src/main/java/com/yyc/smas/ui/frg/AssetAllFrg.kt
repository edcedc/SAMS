package com.yyc.smas.ui.frg

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.yyc.smas.R
import com.yyc.smas.adapter.AssetAdapter
import com.yyc.smas.api.UIHelper
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.databinding.BNotTitleRecyclerBinding
import com.yyc.smas.ext.INVENTORY_FAIL
import com.yyc.smas.ext.INVENTORY_STOCK
import com.yyc.smas.ext.init
import com.yyc.smas.ext.setNbOnItemClickListener
import com.yyc.smas.viewmodel.AssetModel
import com.yyc.smas.viewmodel.RfidModel
import com.yyc.smas.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/27 16:18
 * @Description 全部
 */
class AssetAllFrg: BaseFragment<AssetModel, BNotTitleRecyclerBinding>(){

    private val assetModel: AssetModel by activityViewModels()

    private val reidModel: RfidModel by activityViewModels()

    val adapter: AssetAdapter by lazy { AssetAdapter(arrayListOf()) }

    var orderId: String? = null

    var searchText: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
        }
        mDatabind.swipeRefresh.isEnabled = false
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            setNbOnItemClickListener{adapter, view, position ->
                val bean = mFilterList[position]
                UIHelper.startAssetDetailsFrg(nav(), bean)
//                LogUtils.e(bean.LabelTag)
            }
            setSearchCallback(object :AssetAdapter.SearchCallback{
                override fun onSearchResults(filteredData: ArrayList<AssetBean>) {
//                    assetModel.assetTitle.value = getString(R.string.full_list) + "(" + filteredData.size + ")"
                }
            })
        }

        mViewModel.onRequest(orderId, -1)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                assetModel.assetTitle.value = getString(R.string.full_list) + "(" + adapter.mFilterList.size + ")"
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            adapter.setList(it)
            adapter!!.appendList(it)
            assetModel.assetTitle.value = getString(R.string.full_list) + "(" + it.size + ")"
        })
        //搜索
        assetModel.assetSerch.observe(viewLifecycleOwner, {
            searchText = it
            adapter!!.filter.filter(searchText)
        })
        //识别RFID
        assetModel.epcUploadData.observe(viewLifecycleOwner, {
            if (it == null || it.InventoryStatus == INVENTORY_FAIL)return@observe
            val index = adapter.data.indexOfFirst {bean ->
                (!StringUtils.isEmpty(bean.LabelTag) && !bean.LabelTag.equals("null", ignoreCase = true) && bean.LabelTag.equals(it.LabelTag, ignoreCase = true))
                        || (!StringUtils.isEmpty(bean.AssetNo) && !bean.AssetNo.equals("null", ignoreCase = true) && bean.AssetNo.equals(it.AssetNo, ignoreCase = true))
            }
            if (index != -1){
                val bean = adapter.data[index]
                bean.InventoryStatus = it.InventoryStatus
//                bean.status = if (bean.status == INVENTORY_STOCK) INVENTORY_NOT else INVENTORY_STOCK
                bean.scanStatus = it.scanStatus
                adapter.setData(index, bean)
            }
            //更新搜索页面item状态
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })
    }

}