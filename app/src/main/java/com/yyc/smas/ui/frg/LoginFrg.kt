package com.yyc.smas.ui.frg

import android.os.Bundle
import android.widget.CompoundButton
import com.yyc.smas.R
import com.yyc.smas.api.UIHelper
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.databinding.FLoginBinding
import com.yyc.smas.ext.showMessage
import com.yyc.smas.util.CacheUtil
import com.yyc.smas.viewmodel.LoginModel
import me.hgj.jetpackmvvm.ext.nav

/**
 * @Author nike
 * @Date 2023/7/5 14:53
 * @Description
 */
class LoginFrg: BaseFragment<LoginModel, FLoginBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel
        mDatabind.click = ProxyClick()

        val user = CacheUtil.getUser()
        if (user != null && user.LoginID != null){
            mViewModel.username.set(user.LoginID)
        }
        if (user != null && user.Password != null){
            mViewModel.password.set(user.Password)
        }

//        mViewModel.username.set("SAMS")
    }

    override fun createObserver() {

    }

    inner class ProxyClick() {
        fun clear(){
            mViewModel.username.set("")
        }

        var onCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                mViewModel.isShowPwd.set(isChecked)
            }
        fun login(){
            when {
                mViewModel.username.get().isEmpty() -> showMessage(getString(R.string.error_phone))
                mViewModel.password.get().isEmpty() -> showMessage(getString(R.string.error_phone))
                else -> mViewModel.login(
                    mViewModel.username.get(),
                    mViewModel.password.get()
                )
            }
        }

        fun toSet(){
            UIHelper.startSettingFrg(nav())
        }

    }

}
