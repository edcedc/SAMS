package com.yyc.smas.weight.loadCallBack

import com.yyc.smas.R
import com.kingja.loadsir.callback.Callback


class ErrorCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

}