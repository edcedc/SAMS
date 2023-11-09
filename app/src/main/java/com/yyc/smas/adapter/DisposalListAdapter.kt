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
class DisposalListAdapter(data: ArrayList<DataBean>, mType: Int) :BaseQuickAdapter<DataBean, BaseViewHolder>(R.layout.i_asset1, data), Filterable {

    var mmType: Int = 0

    init {
        setAdapterAnimation(SettingUtil.getListMode())
        this.mmType = mType
    }

    override fun convert(viewHolder: BaseViewHolder, bean: DataBean) {
        //赋值
        bean.run {
            val bean = mFilterList[viewHolder.layoutPosition]
            bean.mType = mmType

            viewHolder.setText(R.id.tv_text, bean.AssetNo)

            viewHolder.setImageResource(R.id.iv_image, if (bean.type == 0) R.mipmap.icon_31 else R.mipmap.icon_30)
            when(mType){
                EXTERNAL_BOOK_TYPE, INTERNAL_BOOK_TYPE, DISPOSAL_BOOK_TYPE ->{
                    val split = bean.Title?.split("｜")
                    val sb = StringBuffer()
                    split?.forEachIndexed { index, it ->
                        sb.append(it)
                        if (index < split.size - 1) {
                            sb.append("\n")
                        }
                    }
                    viewHolder.setText(R.id.tv_title1,  sb)
                    viewHolder.setText(R.id.tv_location, context.getText(R.string.author))
                    viewHolder.setText(R.id.tv_location1, bean.Author)
                }
                EXTERNAL_ARCHIVES_TYPE, INTERNAL_ARCHIVES_TYPE, DISPOSAL_ARCHIVES_TYPE ->{
                    viewHolder.setText(R.id.tv_title1,  bean.Title)
                    val split = bean.ArchivesType?.split("|")
                    val sb = StringBuffer()
                    split?.forEachIndexed { index, it ->
                        sb.append(it)
                        if (index < split.size - 1) {
                            sb.append("\n")
                        }
                    }
                    viewHolder.setText(R.id.tv_location, context.getText(R.string.archives_type))
                    viewHolder.setText(R.id.tv_location1, sb.toString())
                }

                else -> {

                }
            }
            /*when(mType){
                EXTERNAL_BOOK_TYPE, INTERNAL_BOOK_TYPE, DISPOSAL_BOOK_TYPE ->{
                    viewHolder.setGone(R.id.tv_epc, true)
                    viewHolder.setGone(R.id.tv_epc1, true)
                    viewHolder.setGone(R.id.tv_fen2, true)
                    viewHolder.setGone(R.id.tv_type, true)
                    viewHolder.setGone(R.id.tv_type1, true)
                    viewHolder.setGone(R.id.tv_fen3, true)
                }
                EXTERNAL_ARCHIVES_TYPE, INTERNAL_ARCHIVES_TYPE, DISPOSAL_ARCHIVES_TYPE ->{
                    viewHolder.setGone(R.id.tv_epc, false)
                    viewHolder.setText(R.id.tv_epc, context.getText(R.string.level))
                    viewHolder.setGone(R.id.tv_epc1, false)
                    viewHolder.setText(R.id.tv_epc1,  bean.LevelType)
                    viewHolder.setGone(R.id.tv_fen2, false)
                    viewHolder.setGone(R.id.tv_type, false)
                    viewHolder.setText(R.id.tv_type, context.getText(R.string.type))
                    viewHolder.setGone(R.id.tv_type1, false)
                    viewHolder.setText(R.id.tv_type1, bean.ArchivesNo)
                    viewHolder.setGone(R.id.tv_fen3, false)
                }

                else -> {

                }
            }*/
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
                        val libraryCallNo = bean.Author
                        if (assetNo!!.contains(charString) || title!!.contains(charString) || libraryCallNo!!.contains(charString)) {
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

    private fun  getFilterList(): List<DataBean>{
        return mFilterList
    }

    fun isMatchWithPattern(input: String, pattern: String): Boolean {
        val regex = Regex(pattern, RegexOption.IGNORE_CASE)
        return regex.matches(input)
    }

}