package com.yyc.smas.weight.loadCallBack


import com.yyc.smas.R
import com.kingja.loadsir.callback.Callback


class EmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

}