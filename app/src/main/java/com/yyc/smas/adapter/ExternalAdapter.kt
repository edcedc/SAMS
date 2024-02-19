package com.yyc.smas.adapter

import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.smas.R
import com.yyc.smas.bean.DataBean
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.ext.setAdapterAnimation
import com.yyc.smas.util.SettingUtil

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class ExternalAdapter (data: ArrayList<DataBean>) :
    BaseQuickAdapter<DataBean, BaseViewHolder>(
        R.layout.i_external, data), Filterable {

    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(viewHolder: BaseViewHolder, bean: DataBean) {
        val bean = mFilterList[viewHolder.layoutPosition]
        viewHolder.setText(R.id.tv_text, bean.OrderNo + " | " + bean.us)
        bean.Title = viewHolder.getView<AppCompatTextView>(R.id.tv_text).text.toString()
        viewHolder.setText(R.id.tv_title1, "：" + bean.org)
        var BorrowDate = bean.BorrowDate
        if (BorrowDate!!.contains("00:00:00")){
            BorrowDate = BorrowDate.substring(0, 10)
        }
        viewHolder.setText(R.id.tv_location1, "：" + BorrowDate)
        viewHolder.setText(R.id.tv_epc1, "：" + bean.Phone)
        viewHolder.setText(R.id.tv_progress1, "：" + bean.Progress)
    }

    var mFilterList = ArrayList<DataBean>()

    fun appendList(list: List<DataBean>) {
        data = list as MutableList<DataBean>
        //这里需要初始化filterList
        mFilterList = list as ArrayList<DataBean>
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = data as ArrayList<DataBean>
                } else {
                    val filteredList: MutableList<DataBean> = ArrayList()
                    for (i in data.indices) {
                        val bean = data[i]
                        val orderNo = bean.OrderNo
                        val us = bean.us
                        val phone = bean.Phone
                        if (orderNo?.contains(charString, ignoreCase = true) == true
                            || us?.contains(charString, ignoreCase = true) == true
                            || phone?.contains(charString, ignoreCase = true) == true
                        ) {
                            filteredList.add(bean)
                        }
                    }
                    mFilterList = filteredList as ArrayList<DataBean>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<DataBean>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return mFilterList.size
    }

    override fun hashCode(): Int {
        return mFilterList.hashCode()
    }

}