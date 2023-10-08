package com.yyc.smas.mar

import android.R.id
import android.database.AbstractWindowedCursor
import android.database.Cursor
import android.database.CursorWindow
import com.tencent.mmkv.MMKV
import com.yyc.smas.event.AppViewModel
import com.yyc.smas.event.EventViewModel
import com.yyc.smas.service.InitializeService
import me.hgj.jetpackmvvm.base.BaseApp


//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appViewModel: AppViewModel by lazy { MyApplication.appViewModelInstance }

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy { MyApplication.eventViewModelInstance }

class MyApplication : BaseApp() {

    companion object {

        private lateinit var instance: MyApplication

        fun get(): MyApplication {
            return instance
        }

        lateinit var eventViewModelInstance: EventViewModel
        lateinit var appViewModelInstance: AppViewModel

    }

    override fun onCreate() {
        super.onCreate()
        //轻量级存储库
        MMKV.initialize(this)
        MMKV.defaultMMKV().sync()
        instance = this
        eventViewModelInstance = getAppViewModelProvider().get(EventViewModel::class.java)
        appViewModelInstance = getAppViewModelProvider().get(AppViewModel::class.java)
        InitializeService.start(this)

    }

}
