package com.yyc.smas.viewmodel.requet

import androidx.lifecycle.MutableLiveData
import com.yyc.smas.bean.BaseResponseBean
import com.yyc.smas.bean.DataBean
import com.yyc.smas.network.apiService
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.ext.requestNoCheck
import me.hgj.jetpackmvvm.state.ResultState

/**
 * @Author nike
 * @Date 2023/7/6 09:28
 * @Description
 */
class RequestLoginViewModel:BaseViewModel() {


    //方式1  自动脱壳过滤处理请求结果，判断结果是否成功
    var loginResult = MutableLiveData<ResultState<BaseResponseBean<DataBean?>>>()
//
    //方式2  不用框架帮脱壳，判断结果是否成功
//    var loginResult = MutableLiveData<ResultState<ApiResponse<UserInfo>>>()


    fun login(username: String, password: String) {

    }

}