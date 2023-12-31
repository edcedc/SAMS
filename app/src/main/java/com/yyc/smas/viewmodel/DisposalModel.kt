package com.yyc.smas.viewmodel

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.yyc.smas.R
import com.yyc.smas.bean.DataBean
import com.yyc.smas.ext.EXTERNAL_BOOK_TYPE
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
        }, true)
    }

    fun onRequestText(title: String?, bean: String?) {
        val jo = JSONObject(bean)
        val ja = JSONArray()
        val headerkeys: Iterator<String> = jo.keys()
        while (headerkeys.hasNext()) {
            val headerkey = headerkeys.next()
            val headerValue: String = jo.getString(headerkey)
            var threeData = JSONObject()
            threeData.put("title", headerkey)
            threeData.put("text", headerValue)

            if (headerkey.equals("Location") || headerkey.equals("Time")
                || headerkey.equals("LibraryCallNo")|| headerkey.equals("LabelTag")
                || headerkey.equals("Title") || headerkey.equals("Borrowstatus")
                || headerkey.equals("Author") || headerkey.equals("Editions_Year")
                || headerkey.equals("Language") || headerkey.equals("Img")
                || headerkey.equals("ArchivesNo") || headerkey.equals("LevelType")
                || headerkey.equals("ArchivesYear") || headerkey.equals("ArchivesType")){
                ja.put(threeData)
            }
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        var imgObj = JSONObject();
        for (i in 0 until ja.length()) {
            val obj = ja.optJSONObject(i)
            if (obj.optString("title").equals("Img")){
                imgObj = obj
                ja.remove(i)
                break
            }
        }
        if (imgObj.length() != 0){
            ja.put(imgObj)
        }
        jsonObject.put("title", title)
        jsonObject.put("list", ja)
        jsonArray.put(jsonObject)
        listJsonArray.value = jsonArray
    }

}