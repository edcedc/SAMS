package com.yyc.smas.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.smas.bean.AppRoomDataBase
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.bean.db.OrderBean
import com.yyc.smas.ext.RFID_ARCHIVES
import com.yyc.smas.ext.RFID_BOOK
import com.yyc.smas.network.REQUEST_SUCCESS
import com.yyc.smas.network.apiService
import com.yyc.smas.network.stateCallback.ListDataUiState
import com.yyc.smas.util.CacheUtil
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.FloatObservableField
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.ext.requestNoCheck
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/7/7 16:51
 * @Description
 */
class DownloadModel: BaseViewModel() {

    var progressText = StringObservableField("0%")

    var progress = FloatObservableField()

    var listBean: MutableLiveData<ListDataUiState<OrderBean>> = MutableLiveData()
//    var listBean: MutableLiveData<BaseListBean<ArrayList<DataBean>>> = MutableLiveData()

    private var compositeDisposable: Disposable? = null

    private var compositeDisposable2: Disposable? = null

    fun onRequest() {

        val roNo = CacheUtil.getUser()!!.RoNo
        val companyID = CacheUtil.getCompanyID()

        requestNoCheck({ apiService.stockTakeList() }, {

            if (it.code == REQUEST_SUCCESS){
                val data = it.data
                if (data != null && data.size != 0){
                    //删除当前用户的数据库
                    val appRoomDataBase = AppRoomDataBase.get()
                    val orderDao = appRoomDataBase.getOrderDao()
                    val assetDao = appRoomDataBase.getAssetDao()
                    val uploadOrderDao = appRoomDataBase.getUploadOrderDao()
                    val uploadOrderListDao = appRoomDataBase.getUploadOrderListDao()
                    viewModelScope.launch(Dispatchers.IO) {
                        orderDao.deleteById(roNo, companyID)
                        assetDao.deleteById(roNo, companyID)
                        uploadOrderDao.deleteById(roNo, companyID)
                        uploadOrderListDao.deleteById(roNo, companyID)

                        data.forEachIndexed(){index, bean ->
                            bean.RoNo = roNo
                            bean.companyId = companyID
                            //存储数据库
                            orderDao.add(bean)
                            stockTakeListAsset(bean, roNo, companyID)
                        }
                        withContext(Dispatchers.Main) {
                            listBean.value = ListDataUiState(
                                isSuccess = true,
                                listData = it.data,
                                pageSize = it.count
                            )
                        }
                    }
                }else{
                    listBean.value = ListDataUiState(
                        isSuccess = false
                    )
                }
            }

        }, {
            //请求失败
            listBean.value = ListDataUiState(
                isSuccess = false,
                errMessage = it.errorMsg,
                listData = arrayListOf()
            )
        })
    }

    private fun stockTakeListAsset(bean: OrderBean, roNo: String, companyID: String) {
        var stocktakeno = bean.OrderNo
        val assetDao = AppRoomDataBase.get().getAssetDao()

        requestNoCheck({ apiService.stockTakeListAsset(stocktakeno) }, {
            val json = JSONObject(it.string())
            if (json.optInt("code") == REQUEST_SUCCESS) {
                val data = json.optJSONObject("data")
                if (data != null) {
                    val book = data.optJSONArray("Book")
                    compositeDisposable = Flowable.range(0, book.length())
                        .onBackpressureBuffer()
                        .flatMap({ index ->
                            val jsonObject = book.optJSONObject(index)
                            val just = Flowable.just(jsonObject)
                            just.flatMap({ jsonObject ->
                                val bean = AssetBean()
                                val pair = Pair(jsonObject, bean)
                                Flowable.fromCallable {
                                    val jsonObject = pair.first as JSONObject
                                    val bean = pair.second as AssetBean
                                    bean.OrderRoNo = stocktakeno
                                    bean.AssetNo = jsonObject.optString("AssetNo")
                                    bean.LibraryCallNo = jsonObject.optString("LibraryCallNo")
                                    bean.LabelTag = jsonObject.optString("LabelTag")
                                    bean.Title = jsonObject.optString("Title")
                                    bean.Language = jsonObject.optString("Language")
                                    bean.Location = jsonObject.optString("Location")
                                    bean.FoundStatus = jsonObject.optInt("FoundStatus")
                                    bean.scanStatus = jsonObject.optInt("scanStatus")
                                    bean.ids = jsonObject.optString("RoNo")
                                    bean.type = RFID_BOOK
                                    bean.RoNo = roNo
                                    bean.companyId = companyID
                                    bean.InventoryStatus = jsonObject.optInt("InventoryStatus")
                                    bean
                                }.subscribeOn(Schedulers.io())
                            }, true, 1)
                        })
                        .subscribe { bean ->
                            viewModelScope.launch {
                                assetDao.add(bean)
                            }
                        }

                    val archives = data.optJSONArray("Archives")
                    compositeDisposable2 =  Flowable.range(0, archives.length())
                        .onBackpressureBuffer()
                        .flatMap({ index ->
                            val jsonObject = archives.optJSONObject(index)
                            val just = Flowable.just(jsonObject)
                            just.flatMap({ jsonObject ->
                                val bean = AssetBean()
                                val pair = Pair(jsonObject, bean)
                                Flowable.fromCallable {
                                    val jsonObject = pair.first as JSONObject
                                    val bean = pair.second as AssetBean
                                    bean.OrderRoNo = stocktakeno
                                    bean.AssetNo = jsonObject.optString("AssetNo")
                                    bean.ArchivesNo = jsonObject.optString("ArchivesNo")
                                    bean.LabelTag = jsonObject.optString("LabelTag")
                                    bean.Title = jsonObject.optString("Title")
                                    bean.Language = jsonObject.optString("Language")
                                    bean.Location = jsonObject.optString("Location")
                                    bean.FoundStatus = jsonObject.optInt("FoundStatus")
                                    bean.ids = jsonObject.optString("RoNo")
                                    bean.type = RFID_ARCHIVES
                                    bean.RoNo = roNo
                                    bean.companyId = companyID
                                    bean.InventoryStatus = jsonObject.optInt("InventoryStatus")
                                    bean
                                }.subscribeOn(Schedulers.io())
                            }, true, 1)
                        })
                        .subscribe { bean ->
                            viewModelScope.launch {
                                assetDao.add(bean)
                            }
                        }


                    /*val range = Flowable.range(0, data.length())
                    compositeDisposable = range
                        .onBackpressureBuffer()
                        .flatMap({ index ->
                            val jsonObject = data.optJSONObject(index)
                            val just = Flowable.just(jsonObject)
                            just.flatMap({ jsonObject ->

                                val tag = jsonObject.optJSONObject("Tag")

                                val bean = AssetBean()

                                val pair = Pair(jsonObject, bean)
                                Flowable.fromCallable {
                                    val jsonObject = pair.first as JSONObject
                                    val bean = pair.second as AssetBean

                                    bean.ids = stocktakeno + tag.optString("AssetNo")
                                    bean.OrderNo = stocktakeno
                                    bean.AssetNo = tag.optString("AssetNo")
                                    bean.LabelTag = tag.optString("LabelTag")
                                    bean.Remarks = tag.optString("Remarks")
                                    bean.InventoryStatus = tag.optInt("InventoryStatus")
                                    bean.RoNo = roNo
                                    bean.companyid = companyID

                                    bean.data = jsonObject.toString()
                                    bean
                                }.subscribeOn(Schedulers.io())
                            }, true, 1)
                        })
                        .subscribe { bean ->
                            viewModelScope.launch {
                                assetDao.add(bean)
                            }
                        }*/
                }
            }
        }, {
            //请求失败
            listBean.value = ListDataUiState(
                isSuccess = false,
                errMessage = it.errorMsg,
                listData = arrayListOf()
            )
            ToastUtils.showShort(it.message)
        })
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable?.dispose()
        compositeDisposable2?.dispose()
    }

}