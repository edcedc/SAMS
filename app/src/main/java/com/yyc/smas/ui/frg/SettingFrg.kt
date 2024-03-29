package com.yyc.smas.ui.frg

import android.os.Bundle
import androidx.navigation.NavController
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.LogUtils
import com.yc.tea.api.ApiService
import com.yyc.smas.R
import com.yyc.smas.base.BaseFragment
import com.yyc.smas.databinding.FSettingBinding
import com.yyc.smas.ext.showToast
import com.yyc.smas.util.CacheUtil
import com.yyc.smas.viewmodel.SettingModel
import com.yyc.smas.weight.PopupWindowTool
import me.hgj.jetpackmvvm.ext.nav
import java.util.Locale

/**
 * @Author nike
 * @Date 2023/7/6 18:02
 * @Description  设置
 */
class SettingFrg: BaseFragment<SettingModel, FSettingBinding>() {

    var languagePosition: Int = 0

    var languageChoosePosition: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.viewmodel = mViewModel

        mDatabind.click = ProxyClick()
        val locale: Locale = Locale.getDefault()
        val language1: String = locale.language // 获取当前设备的语言代码
        val country: String = locale.country // 获取当前设备的国家/地区代码

        val language = CacheUtil.getLanguage()
        mViewModel.language.set(language)
        when(language){
            getString(R.string.s_chinese) -> {
                languagePosition = 1
            }
            getString(R.string.t_chinese) -> {
                languagePosition = 0
            }

            getString(R.string.e_english) -> {
                languagePosition = 2
            }
        }
        languageChoosePosition = languagePosition
        mViewModel.host.set(CacheUtil.getUrl())
        mViewModel.companyId.set(CacheUtil.getCompanyID())
    }

    inner class ProxyClick(){

        fun close(){
            nav().navigateUp()
        }

        fun setlanguage(){
            PopupWindowTool.showListDialog(activity)
                .asCenterList(getString(R.string.please_language),
                    arrayOf(
                        getString(R.string.t_chinese),
                        getString(R.string.s_chinese),
                        getString(R.string.e_english),
                    ),{ position, text ->
                        when(position){
                            0 -> mViewModel.language.set(requireActivity().getString(R.string.t_chinese))
                            1 -> mViewModel.language.set(requireActivity().getString(R.string.s_chinese ))
                            2 -> mViewModel.language.set(requireActivity().getString(R.string.e_english))
                        }
                        languagePosition = position
                    }).show()
        }

        fun setSave(){
            if (languageChoosePosition != languagePosition){
                when(languagePosition){
                    0 -> {
                        LanguageUtils.applyLanguage(Locale.TRADITIONAL_CHINESE)
                    }
                    1 ->{
                        LanguageUtils.applyLanguage(Locale.SIMPLIFIED_CHINESE)
                    }
                    2 -> {
                        LanguageUtils.applyLanguage(Locale.ENGLISH)
                    }
                }
                CacheUtil.setLanguage(mViewModel.language.get())
            }
            if ((mViewModel.host.get().isEmpty() && mViewModel.companyId.get().isEmpty())){
                showToast(getString(R.string.hiht_))
                return
            }
            CacheUtil.setUrl(mViewModel.host.get())
            CacheUtil.setCompanyID(mViewModel.companyId.get())
            showToast(getString(R.string.release_success))
        }
    }

}