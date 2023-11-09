package com.yyc.smas.ui.frg

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.StringUtils
import com.kingja.loadsir.core.LoadService
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnInputConfirmListener
import com.yyc.smas.R
import com.yyc.smas.adapter.DisposalListAdapter
import com.yyc.smas.api.UIHelper
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.bean.DataBean
import com.yyc.smas.databinding.BNotTitleRecyclerBinding
import com.yyc.smas.ext.DISPOSAL_ARCHIVES_TYPE
import com.yyc.smas.ext.RFID_ARCHIVES
import com.yyc.smas.ext.init
import com.yyc.smas.ext.loadListData
import com.yyc.smas.ext.loadServiceInit
import com.yyc.smas.ext.setNbOnItemClickListener
import com.yyc.smas.ext.showLoading
import com.yyc.smas.ext.showToast
import com.yyc.smas.mar.eventViewModel
import com.yyc.smas.viewmodel.DisposalModel
import com.yyc.smas.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/9/8 10:57
 * @Description 图书
 */
class DisposalArchivesFrg: BaseFragment<DisposalModel, BNotTitleRecyclerBinding>() {

    private val disposalModel: DisposalModel by activityViewModels()

    val adapter: DisposalListAdapter by lazy { DisposalListAdapter(
        arrayListOf(),
        DISPOSAL_ARCHIVES_TYPE
    ) }

    var orderId: String? = null

    var title: String? = null

    var searchText: String? = null

    var isVisibility: Boolean = false

    //界面状态管理者
    lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            orderId = it.getString("orderId")
            title = it.getString("title")
        }

        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            setNbOnItemClickListener{adapter, view, position ->
                val bean = mFilterList[position] as DataBean
//                bean.type = if (bean.type == 1) 0 else 1
//                setData(position, bean)
                UIHelper.startDisposalDetailsFrg(nav(), bean, bean.AssetNo)
            }
        }

        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {
            //点击重试时触发的操作
            loadsir.showLoading()
            mViewModel.onRequestDetails(orderId, RFID_ARCHIVES)
        }

        //初始化 SwipeRefreshLayout  刷新
        mDatabind.swipeRefresh.init {
            mViewModel.onRequestDetails(orderId, RFID_ARCHIVES)
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                isVisibility = false
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                isVisibility = true
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBooKArchivesData.observe(viewLifecycleOwner, {
            loadListData(it, adapter, loadsir, mDatabind.recyclerView, mDatabind.swipeRefresh, it.pageSize)
            adapter.appendList(it.listData)
        })
        //搜索
        disposalModel.searchText.observe(viewLifecycleOwner, {
            if (isVisibility){
                searchText = it
                adapter!!.filter.filter(searchText)
            }
        })
        //扫码
        eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == DISPOSAL_ARCHIVES_TYPE) {
                val indexList = mutableListOf<Int>()
                val split = it.text?.split(",")
                split?.forEach {it
                    adapter.data.filterIndexed { index, bean ->
                        val shouldBeIncluded = (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it)) || bean.AssetNo.equals(it)
                        // 将满足条件的索引添加到 indexList
                        if (shouldBeIncluded) {
                            indexList.add(index)
                        }
                        shouldBeIncluded
                    }
                }
                if (indexList.size == 0) {
                    showToast(getString(R.string.text5))
                    return@Observer
                }
                //更新item状态
                indexList.forEachIndexed() { index, i ->
                    val bean = adapter.data[i]
                    bean.type = if (bean.type == 1) 0 else 1
                    adapter.setData(i, bean)
                }
                if (!StringUtils.isEmpty(searchText)){
                    adapter!!.filter.filter(searchText)
                }
            }
        })
        //提交
        disposalModel.submitType.observe(viewLifecycleOwner, {
            if (it == DISPOSAL_ARCHIVES_TYPE){
                if (adapter.data.size == 0){
                    showToast(getString(R.string.text6))
                    return@observe
                }
                val predicate = adapter.data.all { it.type == 0}
                if (predicate){
                    showToast(getString(R.string.text5))
                    return@observe
                }
                XPopup.Builder(context)
                    .hasStatusBarShadow(true)
                    .hasNavigationBar(false) //.dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true) //对于只使用一次的弹窗对象，推荐设置这个
                    .autoOpenSoftInput(true)
                    .isDarkTheme(false) //                        .isViewMode(true)
                    //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                    .asInputConfirm(getText(R.string.remarks), null, null, null,
                        OnInputConfirmListener {
                            var sb = StringBuffer()
                            adapter.data.filterIndexed { index, bean ->
                                val shouldBeIncluded = (bean.type == 1)
                                if (shouldBeIncluded){
                                    sb.append(bean.RoNo).append(",")
                                }
                                shouldBeIncluded
                            }
                            mViewModel.UpdateDisposalAPP(orderId, sb.toString(), it)
                        },null,R.layout.p_center_dialog)
                    .show()
            }
        })

        mViewModel.listClearArray.observe(viewLifecycleOwner, {
            val split = it.split(",")
            for (i in split.indices) {
                val s = split[i]
                val index = adapter.data.indexOfFirst { it.RoNo.equals(s)}
                if (index != -1)adapter.removeAt(index)
            }
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
        loadsir.showLoading()
        mViewModel.onRequestDetails(orderId, RFID_ARCHIVES)
    }
}