package com.yyc.smas.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.smas.R
import com.yyc.smas.bean.DataBean
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.bean.db.UploadOrderBean

/**
 * @Author nike
 * @Date 2023/8/7 16:29
 * @Description
 */
class UploadAdapter(data: ArrayList<UploadOrderBean>) :

    BaseQuickAdapter<UploadOrderBean, BaseViewHolder>(R.layout.i_upload, data) {

    override fun convert(holder: BaseViewHolder, item: UploadOrderBean) {
        item.run{
            holder.setText(R.id.tv_text, item.title)
            holder.setText(R.id.btn_commit, if (item.status == 0) context.getText(R.string.text2) else context.getText(R.string.text3))
        }
    }

}