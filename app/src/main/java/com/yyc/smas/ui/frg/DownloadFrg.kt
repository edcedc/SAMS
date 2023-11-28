package com.yyc.smas.ui.frg

import android.animation.TimeInterpolator
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.kingja.loadsir.core.LoadService
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.skydoves.progressview.progressView
import com.yyc.smas.R
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.bean.AppRoomDataBase
import com.yyc.smas.bean.db.OrderBean
import com.yyc.smas.databinding.FDownloadBinding
import com.yyc.smas.ext.initClose
import com.yyc.smas.ext.loadServiceInit
import com.yyc.smas.ext.showEmpty
import com.yyc.smas.ext.showLoading
import com.yyc.smas.ext.showToast
import com.yyc.smas.mar.eventViewModel
import com.yyc.smas.util.CacheUtil
import com.yyc.smas.viewmodel.DownloadModel
import com.yyc.smas.viewmodel.OrderModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

import me.hgj.jetpackmvvm.ext.nav
import java.text.NumberFormat
import java.util.Random
import java.util.concurrent.TimeUnit


/**
 * @Author nike
 * @Date 2023/7/7 16:51
 * @Description  警报日记
 */
class DownloadFrg : BaseFragment<DownloadModel, FDownloadBinding>() {

    //界面状态管理者
    lateinit var loadsir: LoadService<Any>

    var compositeDisposable: Disposable? = null

    val numberFormat = NumberFormat.getInstance()

    var number: Int = 1

    val roNo = CacheUtil.getUser()!!.RoNo

    val companyID = CacheUtil.getCompanyID()

    override fun initView(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        mDatabind.viewmodel = mViewModel
        mDatabind.includeToolbar.toolbar.initClose(getString(R.string.download)) {
            nav().navigateUp()
        }
        //状态页配置
        loadsir = loadServiceInit(mDatabind.layout) {
            //点击重试时触发的操作
            loadsir.showLoading()
            mViewModel.onRequest()
        }

        numberFormat.setMaximumFractionDigits(2)

//        mDatabind.progressView.setOnProgressChangeListener{
//            mDatabind.progressView.labelText = "Download ${it.toInt()}%"
//        }
        mDatabind.circularProgressBar.onProgressChangeListener = { progress ->
            mViewModel.progressText.set("${progress.toInt()}%")
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                compositeDisposable?.dispose()
                mActivity.setSupportActionBar(null)
                mViewModel.onCleared()
            }
        })
    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.listBean.observe(viewLifecycleOwner, Observer {
            if (it.isSuccess) {
                eventViewModel.mainListEvent.postValue(true)
                if (it.pageSize != 0) {
                    val assetDao = AppRoomDataBase.get().getAssetDao()
                    mViewModel.viewModelScope.launch {
                        assetDao.findAll(roNo, companyID)
                            .flowOn(Dispatchers.IO)
                            .collect { data ->
                                number = data.size
                            }
                    }

                    compositeDisposable = Observable.interval(1, TimeUnit.SECONDS)
                        .observeOn(Schedulers.io()) // 切换到IO线程执行操作
                        .map { tick ->
                            number.toFloat() / it.pageSize.toFloat() * 100 // 执行计算操作，这里可能有异常
                        }
                        .onErrorReturn { e ->
                            // 处理发生异常的情况，返回一个默认值作为结果
                            LogUtils.e("Observable", "Error occurred: ${e.message}")
                            0f // 返回一个默认值，表示操作失败
                        }
                        .subscribe { result ->
                            // 在这里处理正常的操作逻辑
                            mDatabind.circularProgressBar.progress = result
                            runOnUiThread {
                                mDatabind.circularProgressBar.setProgressWithAnimation(result, 1000)
                            }
                            LogUtils.i(number, result)

                            if (number >= it.pageSize) {
                                compositeDisposable?.dispose()
                                runOnUiThread {
                                    Handler().postDelayed({
                                        mDatabind.tvText.text = "Sync Success"
                                    }, 1200) // 延迟1秒（即1000毫秒）
                                }
                            }
                        }
                }
            } else {
                loadsir.showEmpty()
            }
        })
    }

    override fun lazyLoadData() {
        //设置界面 加载中
        mViewModel.onRequest()
    }


}