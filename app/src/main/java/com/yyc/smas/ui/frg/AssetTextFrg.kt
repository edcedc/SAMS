package com.yyc.smas.ui.frg

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.yyc.smas.adapter.AssetDetailsAdapter
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.databinding.BNotTitleRecyclerBinding
import com.yyc.smas.ext.init
import com.yyc.smas.viewmodel.AssetModel
import com.yyc.smas.weight.recyclerview.SpaceItemDecoration
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/8/2 14:18
 * @Description rfid 展示详情
 */
class AssetTextFrg: BaseFragment<AssetModel, BNotTitleRecyclerBinding>() {

    var jsonArray = JSONArray()

    val adapter by lazy { activity?.let { AssetDetailsAdapter(it, jsonArray) } }

    var bean: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            bean = it.getString("bean")
        }
        mDatabind.swipeRefresh.isEnabled = false
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter!!).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(0f), ConvertUtils.dp2px(0f), true))
        }
        adapter.run {

        }
        mViewModel.onRequestText1(bean)
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listJsonArray.observe(viewLifecycleOwner, {
            var list = it as JSONArray
            for (i in list.length() - 1 downTo 0) {
                val obj = list.optJSONObject(i)
                jsonArray.put(obj)
            }
            adapter!!.notifyDataSetChanged()
        })
    }

}