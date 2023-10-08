package com.yyc.smas.viewmodel

import android.view.View
import androidx.databinding.ObservableInt
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 * @Author nike
 * @Date 2023/8/3 10:22
 * @Description
 */
class AssetUploadModel: BaseViewModel(){

    var status = StringObservableField()

    var remarks = StringObservableField()

}