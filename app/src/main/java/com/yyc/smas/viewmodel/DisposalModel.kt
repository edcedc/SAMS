package com.yyc.smas.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.smas.R
import com.yyc.smas.bean.DataBean
import com.yyc.smas.ext.DISPOSAL_ARCHIVES_TYPE
import com.yyc.smas.ext.DISPOSAL_BOOK_TYPE
import com.yyc.smas.ext.EXTERNAL_ARCHIVES_TYPE
import com.yyc.smas.ext.EXTERNAL_BOOK_TYPE
import com.yyc.smas.ext.INTERNAL_ARCHIVES_TYPE
import com.yyc.smas.ext.INTERNAL_BOOK_TYPE
import com.yyc.smas.network.REQUEST_SUCCESS
import com.yyc.smas.network.apiService
import com.yyc.smas.network.stateCallback.ListDataUiState
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.ext.requestNoCheck
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/8/8 17:01
 * @Description
 */
class DisposalModel: BaseViewModel() {

    var state = StringObservableField()

    var save = StringObservableField()

    var searchText: MutableLiveData<String> = MutableLiveData()

    var listJsonArray: MutableLiveData<JSONArray> = MutableLiveData()

    var listClearArray: MutableLiveData<String> = MutableLiveData()

    var submitType: MutableLiveData<Int> = MutableLiveData()
//    val submitType:SingleLiveEvent<Int> = SingleLiveEvent()

    var listData: MutableLiveData<ListDataUiState<DataBean>> = MutableLiveData()

    var listBooKArchivesData: MutableLiveData<ListDataUiState<DataBean>> = MutableLiveData()

    val pagerNumber: Int = 1

    fun onRequest() {
        request({ apiService.GetDisposalorder("") }, {
            val isRefresh = true
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isEmpty = it!!.isEmpty(),
                    isRefresh = isRefresh,
                    isFirstEmpty = pagerNumber == 1 && it.isEmpty(),
                    listData = it
                )
            listData.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DataBean>()
                )
            listData.value = listDataUiState
        })
    }

    fun onRequestDetails(orderId: String?, i: Int) {
        request({ apiService.GetDisposalorderDetails(i, orderId, "") }, {
            val isRefresh = true
            val listDataUiState =
                ListDataUiState(
                    isSuccess = true,
                    isEmpty = it!!.isEmpty(),
                    isRefresh = isRefresh,
                    isFirstEmpty = pagerNumber == 1 && it.isEmpty(),
                    listData = it
                )
            listBooKArchivesData.value = listDataUiState
        }, {
            //请求失败
            val listDataUiState =
                ListDataUiState(
                    isSuccess = false,
                    errMessage = it.errorMsg,
                    isRefresh = true,
                    listData = arrayListOf<DataBean>()
                )
            listBooKArchivesData.value = listDataUiState
        })
    }

    fun onGetwhetherBorrowingExistApp(orderId: String?, str: String) {
        request({apiService.GetwhetherBorrowingExistApp(str)},{

        },{
            //请求失败 网络异常回调在这里
            loadingChange.dismissDialog
            ToastUtils.showShort(it.throwable!!.message)
            LogUtils.e(it.throwable, it.throwable!!.message)
        }, true)
    }

    fun UpdateDisposalAPP(orderId: String?, str: String, s: String) {
        requestNoCheck({apiService.UpdateDisposalAPP(orderId!!, str, s)},{
            if (it.code == REQUEST_SUCCESS){
                listClearArray.value = str
                ToastUtils.showShort(appContext.getText(R.string.text4))
            }else{
                ToastUtils.showShort(appContext.getText(R.string.text9))
            }
        },{
            //请求失败 网络异常回调在这里
            loadingChange.dismissDialog
            ToastUtils.showShort(it.throwable!!.message)
            LogUtils.e(it.throwable, it.throwable!!.message)
        }, true, appContext.getString(R.string.loading))
    }

    fun onRequestText(title: String?, bean: String?, type: Int) {
        val jo = JSONObject(bean)
        val ja = JSONArray()
        val headerkeys: Iterator<String> = jo.keys()
        while (headerkeys.hasNext()) {
            val headerkey = headerkeys.next()
            val headerValue: String = jo.getString(headerkey)
            var threeData = JSONObject()
            threeData.put("title", headerkey)
            threeData.put("text", headerValue)
            ja.put(threeData)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        var newJa = JSONArray()
        when(type){
            EXTERNAL_BOOK_TYPE, INTERNAL_BOOK_TYPE, DISPOSAL_BOOK_TYPE ->{
                val keysOrder = listOf(
                    "AssetNo",
                    "LibraryCallNo",
                    "Type",
                    "Title",
                    "Author",
                    "Editions_Year",
                    "Location",
                    "Borrowstatus",
                    if (type == DISPOSAL_BOOK_TYPE) "StatusId" else "StatusID",
                    "Img"
                )

                val keyObjectMap = LinkedHashMap<String, JSONObject>()

                for (i in 0 until ja.length()) {
                    val jsonObject = ja.optJSONObject(i)
                    val jo = jsonObject.optString("title")
                    val text = jsonObject.optString("text")

                    if (jo in keysOrder) {
                        var obj = keyObjectMap[jo]
                        if (obj == null) {
                            obj = JSONObject()
                            obj.put("title", jo)
                            keyObjectMap[jo] = obj
                        }
                        obj.put("text", text)
                    }
                }

                for (key in keysOrder) {
                    val obj = keyObjectMap[key]
                    if (obj != null) {
                        newJa.put(obj)
                    }
                }
        }
            EXTERNAL_ARCHIVES_TYPE, INTERNAL_ARCHIVES_TYPE, DISPOSAL_ARCHIVES_TYPE->{
                val keysOrder = listOf(
                    "AssetNo",
                    "ArchivesNo",
                    "LevelType",
                    "Type",
                    "Title",
                    "BishopName",
                    "Location",
                    "Borrowstatus",
                    if (type == DISPOSAL_ARCHIVES_TYPE) "StatusId" else "StatusID",
                )

                val keyObjectMap = LinkedHashMap<String, JSONObject>()

                for (i in 0 until ja.length()) {
                    val jsonObject = ja.optJSONObject(i)
                    val jo = jsonObject.optString("title")
                    val text = jsonObject.optString("text")

                    if (jo in keysOrder) {
                        var obj = keyObjectMap[jo]
                        if (obj == null) {
                            obj = JSONObject()
                            obj.put("title", jo)
                            keyObjectMap[jo] = obj
                        }
                        obj.put("text", text)
                    }
                }

                for (key in keysOrder) {
                    val obj = keyObjectMap[key]
                    if (obj != null) {
                        newJa.put(obj)
                    }
                }
            }
            else ->{
                newJa.put(ja)
            }
        }

        jsonObject.put("title", "备用")
        jsonObject.put("list", newJa)
        jsonArray.put(jsonObject)
        listJsonArray.value = jsonArray

    }

}