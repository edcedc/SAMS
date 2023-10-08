package com.yyc.smas.bean

import com.yyc.smas.network.REQUEST_SUCCESS
import me.hgj.jetpackmvvm.network.BaseResponse
import java.io.Serializable

/**
 * Created by yc on 2017/8/17.
 */

class BaseListBean<T>(val code: Int, val count: Int, val msg: String, val data: T?) : BaseResponse<T?>() {

    // 这里是示例，wanandroid 网站返回的 错误码为 0 就代表请求成功，请你根据自己的业务需求来改变
    override fun isSucces() = code == REQUEST_SUCCESS

    override fun getResponseCode() = code

    override fun getResponseData(): T? = data

    override fun getResponseMsg() = msg

}
