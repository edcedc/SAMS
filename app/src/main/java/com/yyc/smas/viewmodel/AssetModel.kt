package com.yyc.smas.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.yyc.smas.R
import com.yyc.smas.bean.AppRoomDataBase
import com.yyc.smas.bean.RfidStateBean
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.bean.db.UploadOrderBean
import com.yyc.smas.bean.db.UploadOrderListBean
import com.yyc.smas.ext.DISPOSAL_ARCHIVES_TYPE
import com.yyc.smas.ext.DISPOSAL_BOOK_TYPE
import com.yyc.smas.ext.EXTERNAL_ARCHIVES_TYPE
import com.yyc.smas.ext.EXTERNAL_BOOK_TYPE
import com.yyc.smas.ext.INTERNAL_ARCHIVES_TYPE
import com.yyc.smas.ext.INTERNAL_BOOK_TYPE
import com.yyc.smas.ext.INVENTORY_FAIL
import com.yyc.smas.ext.RFID_ARCHIVES
import com.yyc.smas.ext.RFID_BOOK
import com.yyc.smas.ext.UPLOAD_IMAGE_SPLIT
import com.yyc.smas.ext.showLoadingExt
import com.yyc.smas.ui.frg.AssetFrg
import com.yyc.smas.util.CacheUtil
import com.yyc.smas.util.ImageUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField
import org.json.JSONArray
import org.json.JSONObject

/**
 * @Author nike
 * @Date 2023/7/27 11:45
 * @Description
 */
open class  AssetModel : BaseViewModel() {

    var assetTitle: MutableLiveData<String> = MutableLiveData()

    var assetSerch: MutableLiveData<String> = MutableLiveData()

    var epcData: MutableLiveData<RfidStateBean> = MutableLiveData()

    var epcUploadData: MutableLiveData<AssetBean> = MutableLiveData()
//    val epcUploadData = SingleLiveEvent<AssetBean>()
//    val epcUploadData:MutableLiveData<AssetBean> = SingleLiveEvent()
//    val epcUploadData:MutableLiveData<AssetBean> = LockedLiveEvent()
//    val epcUploadData = MutableStateFlow<AssetBean>()

    var listBean: MutableLiveData<ArrayList<AssetBean>> = MutableLiveData()

    var listJsonArray: MutableLiveData<JSONArray> = MutableLiveData()

    var state = StringObservableField()

    var save = StringObservableField()

    var isShowDialog: MutableLiveData<Boolean> = MutableLiveData()

    val roNo = CacheUtil.getUser()?.RoNo

    val companyId = CacheUtil.getCompanyID()

    fun onRequest(orderId: String?, status: Int) {
        val assetDao = AppRoomDataBase.get().getAssetDao()
        var list: ArrayList<AssetBean>
        viewModelScope.launch(Dispatchers.IO) {
            if (status == -1){
                list = assetDao.findByIdNoFail(roNo, orderId, companyId) as ArrayList<AssetBean>
            }else{
                list = assetDao.findById(roNo, orderId, status, companyId) as ArrayList<AssetBean>
            }
            listBean.postValue(list)
        }
    }

    fun onRequestText(bean: String?) {
        var asstBean = Gson().fromJson(bean, AssetBean::class.java)
        val bean = JSONObject(bean)
        LogUtils.e(bean)
        Flowable.fromCallable {
            val list = JSONArray()
            val oneArray = JSONArray()
            val oneData = JSONObject()
            oneData.put("title", appContext.resources.getString(R.string.detailed))
            val headerkeys: Iterator<String> = bean.keys()
            while (headerkeys.hasNext()) {
                val headerkey = headerkeys.next()
                val headerValue: String = bean.getString(headerkey)
                var threeData = JSONObject()
                threeData.put("title", headerkey)
                threeData.put("text", headerValue)


                if (headerkey.equals("RoNo") || headerkey.equals("AssetNo")
                    || headerkey.equals("ArchivesNo") || headerkey.equals("ids")
                    || headerkey.equals("uid") || headerkey.equals("type")
                    || headerkey.equals("imageList") || headerkey.equals("OrderRoNo")
                    || headerkey.equals("LibraryCallNo")|| headerkey.equals("status")){

                }else{
                    if (headerkey.equals("Remarks")){
                        threeData.put("text", asstBean.Remarks)
                    }else if (headerkey.equals("FoundStatus") || headerkey.equals("scanStatus")){
                        threeData.put("text", asstBean.scanStatus)
                    }else if (headerkey.equals("scanTime")){
                        threeData.put("text", asstBean.scanTime)
                    }
                    oneArray.put(threeData)
                }



            }
            oneData.put("list", oneArray)
            list.put(oneData)
            list
        }
            .subscribeOn(Schedulers.io()) //给上面分配了异步线程
            .observeOn(AndroidSchedulers.mainThread()) //给下面分配了UI线程
            .subscribe({ list ->
                listJsonArray.value = list
            }, { error ->
                // 处理错误
                LogUtils.e(error)
            })
    }

    fun onUpload(orderId: String?, assetFrg: AssetFrg) {
        val RoNo = CacheUtil.getUser()?.RoNo
        val companyId = CacheUtil.getUser()?.companyId
        val assetDao = AppRoomDataBase.get().getAssetDao()
        val uploadOrderDao = AppRoomDataBase.get().getUploadOrderDao()
        val uploadOrderListDao = AppRoomDataBase.get().getUploadOrderListDao()
        val time = TimeUtils.getNowString()

        var ja = JSONArray()
        viewModelScope.launch(Dispatchers.IO) {
            val list = assetDao.findById(orderId, RoNo, companyId)
            if (list == null || list.size == 0){
                ToastUtils.showShort(appContext.getString(R.string.no_found1))
                return@launch
            }
            isShowDialog.postValue(true)

            var isAdd = false
            var uploadOrderBean = uploadOrderDao.findOrderId(orderId, RoNo, companyId)
            if (uploadOrderBean == null){
                uploadOrderBean = UploadOrderBean()
                isAdd = true
            }
            uploadOrderBean.status = 0
            uploadOrderBean.title = "Stock Take：" + orderId + " | " + time
            uploadOrderBean.RoNo = RoNo!!
            uploadOrderBean.companyId = companyId!!
            uploadOrderBean.orderId = orderId!!
            if (isAdd){
                uploadOrderDao.add(uploadOrderBean)
            }else{
                uploadOrderDao.update(uploadOrderBean)
            }
            list.forEachIndexed { index, assetBean ->
                val bean = UploadOrderListBean()
                bean.loginID = RoNo
                bean.orderNo = orderId
                bean.AssetNo = assetBean.ids
                bean.ScanDate = assetBean.scanTime
                bean.EPC = assetBean.LabelTag
                bean.Remarks = assetBean.Remarks
                bean.statusID = if (assetBean.InventoryStatus == INVENTORY_FAIL) 2 else assetBean.InventoryStatus
                bean.FoundStatus = assetBean.scanStatus
                bean.Last_ScanTime = time
                bean.companyId = companyId

                val imageList = assetBean.imageList
                val split = imageList.split(UPLOAD_IMAGE_SPLIT)
                val sb = StringBuffer()
                split.forEachIndexed(){index, s ->
                    if (!StringUtils.isEmpty(s)){
                        val imageToBase64 = ImageUtils.imageToBase64(s)
                        sb.append(imageToBase64).append(UPLOAD_IMAGE_SPLIT)
                    }
                }
                if (!StringUtils.isEmpty(sb)){
//                        FileToByte(bean, URLEncoder.encode(sb.toString(), "UTF-8"))
                }
                bean.img = sb.toString()

                uploadOrderListDao.add(bean)
            }
            withContext(Dispatchers.Main) {
                runBlocking {
                    delay(1000) // 延迟1秒钟
                    isShowDialog.value = false
                }
                ToastUtils.showShort(appContext.getText(R.string.release_success))
            }
        }
    }

    fun onRequestText1(bean: String?) {
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
        when(jo.optInt("status")){
            RFID_BOOK ->{
                val keysOrder = listOf(
                    "AssetNo",
                    "LibraryCallNo",
                    "Type",
                    "Title",
                    "Author",
                    "Editions_Year",
                    "Location",
                    "BorrowStatus",
                    "StatusId",
                    "Language",
                    "LabelTag",
                    "InventoryStatus",
                    "scanStatus",
                    "scanTime",
                    "Remarks",
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
            RFID_ARCHIVES ->{
                val keysOrder = listOf(
                    "AssetNo",
                    "ArchivesNo",
                    "LevelType",
                    "Type",
                    "Title",
                    "BishopName",
                    "Location",
                    "BorrowStatus",
                    "StatusId",
                    "LabelTag",
                    "InventoryStatus",
                    "scanStatus",
                    "scanTime",
                    "Remarks",
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