package com.yyc.smas.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.yyc.smas.R
import com.yyc.smas.bean.RfidStateBean
import me.hgj.jetpackmvvm.base.appContext
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.BooleanObservableField
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 * @Author nike
 * @Date 2023/8/23 11:11
 * @Description
 */
class AssetSearchmModel: BaseViewModel() {

    var rssi = StringObservableField("0")

    val isOpen = ObservableBoolean()

    val openStatus = StringObservableField(appContext.getString(R.string.start))

    var epcData: MutableLiveData<RfidStateBean> = MutableLiveData()

}