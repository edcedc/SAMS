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
import com.blankj.utilcode.util.TimeUtils
import com.kingja.loadsir.core.LoadService
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnInputConfirmListener
import com.yyc.smas.R
import com.yyc.smas.adapter.DisposalListAdapter
import com.yyc.smas.api.UIHelper
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.bean.DataBean
import com.yyc.smas.databinding.BNotTitleRecyclerBinding
import com.yyc.smas.ext.INTERNAL_BOOK_TYPE
import com.yyc.smas.ext.RFID_BOOK
import com.yyc.smas.ext.init
import com.yyc.smas.ext.loadListData
import com.yyc.smas.ext.loadServiceInit
import com.yyc.smas.ext.setNbOnItemClickListener
import com.yyc.smas.ext.showLoading
import com.yyc.smas.ext.showToast
import com.yyc.smas.mar.eventViewModel
import com.yyc.smas.util.CacheUtil
import com.yyc.smas.viewmodel.ExternalModel
import com.yyc.smas.viewmodel.InternalModel
import com.yyc.smas.weight.recyclerview.SpaceItemDecoration
import me.hgj.jetpackmvvm.ext.nav
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * @Author nike
 * @Date 2023/9/8 10:57
 * @Description 图书
 */
class InternalBookFrg: BaseFragment<InternalModel, BNotTitleRecyclerBinding>() {

    private val internalModel: InternalModel by activityViewModels()

    val adapter: DisposalListAdapter by lazy { DisposalListAdapter(
        arrayListOf(),
        INTERNAL_BOOK_TYPE
    ) }

    var searchText: String? = null

    var isVisibility: Boolean = false

    //界面状态管理者
    lateinit var loadsir: LoadService<Any>

    override fun initView(savedInstanceState: Bundle?) {
        //初始化recyclerView
        mDatabind.recyclerView.init(LinearLayoutManager(context), adapter).let {
            it.addItemDecoration(SpaceItemDecoration(ConvertUtils.dp2px(10f), ConvertUtils.dp2px(10f), true))
        }
        adapter.run {
            setNbOnItemClickListener { adapter, view, position ->
                val bean = mFilterList[position] as DataBean
                bean.type = if (bean.type == 1) 0 else 1
                setData(position, bean)
//                UIHelper.startDisposalDetailsFrg(nav(), bean, bean.AssetNo)
            }
        }

        //状态页配置
        loadsir = loadServiceInit(mDatabind.swipeRefresh) {
            //点击重试时触发的操作
            loadsir.showLoading()
            mViewModel.onRequestDetails(null, RFID_BOOK)
        }

        mDatabind.swipeRefresh.isEnabled = false
        //初始化 SwipeRefreshLayout  刷新
        mDatabind.swipeRefresh.init {
            mViewModel.onRequestDetails(null, RFID_BOOK)
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
        mViewModel.listInsideOrder.observe(viewLifecycleOwner, {
            var filter = it.filterIndexed { index, dataBean ->
                val first = dataBean.QRCode?.first()?.lowercase(Locale.getDefault())
                (first!!.contains("b"))
            }
            adapter.addData(filter)
            adapter.appendList(adapter.data)

            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })
        mViewModel.listBooKArchivesData.observe(viewLifecycleOwner, {
            loadListData(it, adapter, loadsir, mDatabind.recyclerView, mDatabind.swipeRefresh, it.pageSize)
            adapter.appendList(it.listData)
        })
        //搜索
        internalModel.searchText.observe(viewLifecycleOwner, {
            if (isVisibility){
                searchText = it
                adapter!!.filter.filter(searchText)
            }
        })
        //扫码
        eventViewModel.zkingType.observeInFragment(this, Observer {
            if (it.type == INTERNAL_BOOK_TYPE) {
                mViewModel.onGetInsideOrder(it.text)
                val indexList = mutableListOf<Int>()
                adapter.data.filterIndexed { index, bean ->
                    val shouldBeIncluded = (!StringUtils.isEmpty(bean.LabelTag) && bean.LabelTag.equals(it.text)) || bean.AssetNo.equals(it.text)
                    // 将满足条件的索引添加到 indexList
                    if (shouldBeIncluded) {
                        indexList.add(index)
                    }
                    shouldBeIncluded
                }
                if (indexList.size != 0) {
//                    showToast(getString(R.string.text5))
                    return@Observer
                }
                /*  //更新item状态
                  indexList.forEachIndexed() { index, i ->
                      val bean = adapter.data[i]
                      bean.type = if (bean.type == 1) 0 else 1
                      adapter.setData(i, bean)

  //                    adapter.appendList(adapter.data)
                      if (!StringUtils.isEmpty(searchText)){
                          adapter!!.filter.filter(searchText)
                      }
                  }*/
            }
        })
        //提交
        internalModel.submitType.observe(viewLifecycleOwner, {
            if (it == INTERNAL_BOOK_TYPE){
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
                            var ja = JSONArray()
                            adapter.data.filterIndexed { index, bean ->
                                val shouldBeIncluded = (bean.type == 1)
                                if (shouldBeIncluded){
                                    var jo = JSONObject()
                                    jo.put("RoNo", bean.RoNo)
                                    jo.put("userRoNo", CacheUtil.getUser()?.RoNo)
                                    jo.put("scandate", TimeUtils.getNowString())
                                    jo.put("Remarks", it)
                                    ja.put(jo)
                                }
                                shouldBeIncluded
                            }
                            mViewModel.UpdateDisposalAPP(ja)
                        },null,R.layout.p_center_dialog)
                    .show()
            }
        })

        mViewModel.listClearArray.observe(viewLifecycleOwner, {
            for (i in 0 until it.length()) {
                val obj = it.optJSONObject(i)
                val index= adapter.data.indexOfFirst  { it.RoNo.equals(obj.optString("RoNo")) }
                if (index != -1)adapter.removeAt(index)
            }
            if (!StringUtils.isEmpty(searchText)){
                adapter!!.filter.filter(searchText)
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
//        loadsir.showLoading()
//        mViewModel.onRequestDetails(null, RFID_BOOK)
    }
}