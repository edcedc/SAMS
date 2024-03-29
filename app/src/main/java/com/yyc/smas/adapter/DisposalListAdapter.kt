package com.yyc.smas.adapter

import android.widget.Filter
import android.widget.Filterable
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.smas.R
import com.yyc.smas.bean.DataBean
import com.yyc.smas.ext.DISPOSAL_ARCHIVES_TYPE
import com.yyc.smas.ext.DISPOSAL_BOOK_TYPE
import com.yyc.smas.ext.EXTERNAL_ARCHIVES_TYPE
import com.yyc.smas.ext.EXTERNAL_BOOK_TYPE
import com.yyc.smas.ext.INTERNAL_ARCHIVES_TYPE
import com.yyc.smas.ext.INTERNAL_BOOK_TYPE
import com.yyc.smas.ext.setAdapterAnimation
import com.yyc.smas.util.SettingUtil

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class DisposalListAdapter(data: ArrayList<DataBean>, mType: Int) :
    BaseQuickAdapter<DataBean, BaseViewHolder>(R.layout.i_asset1, data), Filterable {

    var mmType: Int = 0

    init {
        setAdapterAnimation(SettingUtil.getListMode())
        this.mmType = mType
    }

    override fun convert(viewHolder: BaseViewHolder, bean: DataBean) {
        val bean = mFilterList[viewHolder.layoutPosition]
        bean.mType = mmType

        viewHolder.setText(R.id.tv_text, bean.AssetNo)

        viewHolder.setImageResource(R.id.iv_image, if (bean.type == 0) R.mipmap.icon_31 else R.mipmap.icon_30)
        when(mmType){
            EXTERNAL_BOOK_TYPE, INTERNAL_BOOK_TYPE, DISPOSAL_BOOK_TYPE ->{
                val split = bean.Title?.split("｜")
                val sb = StringBuffer()
                split?.forEachIndexed { index, it ->
                    sb.append(it)
                    if (index < split.size - 1) {
                        sb.append("\n")
                    }
                }
                viewHolder.setText(R.id.tv_title,  sb)
                viewHolder.setText(R.id.tv_epc1, context.getText(R.string.author))
                viewHolder.setText(R.id.tv_epc, bean.Author)
            }
            EXTERNAL_ARCHIVES_TYPE, INTERNAL_ARCHIVES_TYPE, DISPOSAL_ARCHIVES_TYPE ->{
                viewHolder.setText(R.id.tv_title,  bean.Title)
                val split = bean.Type?.split("｜")
                val sb = StringBuffer()
                split?.forEachIndexed { index, it ->
                    sb.append(it)
                    if (index < split.size - 1) {
                        sb.append("\n")
                    }
                }
                viewHolder.setText(R.id.tv_epc1, context.getText(R.string.archives_type))
                viewHolder.setText(R.id.tv_epc, sb.toString())
            }

            else -> {

            }
        }
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
                        val assetNo = bean.AssetNo
                        val title = bean.Title
                        val author = bean.Author
                        val archivesTypev = bean.ArchivesType
                        val Img = bean.Img
                        if (assetNo?.contains(charString, ignoreCase = true) == true
                            || title?.contains(charString, ignoreCase = true) == true
                            || author?.contains(charString, ignoreCase = true) == true
                            || archivesTypev?.contains(charString, ignoreCase = true) == true
                            || Img?.contains(charString, ignoreCase = true) == true
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