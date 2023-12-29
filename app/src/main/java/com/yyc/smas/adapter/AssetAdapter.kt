package com.yyc.smas.adapter

import android.widget.Filter
import android.widget.Filterable
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.yyc.smas.R
import com.yyc.smas.bean.db.AssetBean
import com.yyc.smas.ext.INVENTORY_FAIL
import com.yyc.smas.ext.INVENTORY_STOCK
import com.yyc.smas.ext.RFID_BOOK
import com.yyc.smas.ext.setAdapterAnimation
import com.yyc.smas.util.SettingUtil

/**
 * @Author nike
 * @Date 2023/7/7 17:05
 * @Description
 */
class AssetAdapter (data: ArrayList<AssetBean>) :BaseQuickAdapter<AssetBean, BaseViewHolder>(R.layout.i_asset1, data), Filterable {


    init {
        setAdapterAnimation(SettingUtil.getListMode())
    }

    override fun convert(viewHolder: BaseViewHolder, bean: AssetBean) {
        val bean = mFilterList[viewHolder.layoutPosition]
        viewHolder.setText(R.id.tv_text, bean.AssetNo + " | " + if (bean.status == RFID_BOOK) bean.LibraryCallNo else bean.ArchivesNo)
        viewHolder.setText(R.id.tv_title, bean.Title)
        viewHolder.setText(R.id.tv_epc, if (StringUtils.isEmpty(bean.LabelTag)) "" else bean.LabelTag)
        if (bean.InventoryStatus != INVENTORY_FAIL){
            viewHolder.setGone(R.id.ly_title, false)
            viewHolder.setGone(R.id.tv_text, false)
            viewHolder.setGone(R.id.iv_image, false)
            viewHolder.setImageResource(R.id.iv_image, if (bean.InventoryStatus == INVENTORY_STOCK) R.mipmap.icon_30 else R.mipmap.icon_31)
        }else{
            viewHolder.setGone(R.id.tv_text, true)
            viewHolder.setGone(R.id.ly_title, true)
            viewHolder.setGone(R.id.iv_image, true)
        }
    }

    var mFilterList = ArrayList<AssetBean>()

    fun appendList(list: List<AssetBean>) {
        data = list as MutableList<AssetBean>
        //这里需要初始化filterList
        mFilterList = list as ArrayList<AssetBean>
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    mFilterList = data as ArrayList<AssetBean>
                } else {
                    val filteredList: MutableList<AssetBean> = ArrayList()
                    for (i in data.indices) {
                        val bean = data[i]
                        val assetNo = bean.AssetNo
                        val labelTag = bean.LabelTag
                        val title = bean.Title
                        if (assetNo?.contains(charString, ignoreCase = true) == true
                            || title?.contains(charString, ignoreCase = true) == true
                            || labelTag?.contains(charString, ignoreCase = true) == true
                        ) {
                            filteredList.add(bean)
                        }
                    }
                    mFilterList = filteredList as ArrayList<AssetBean>
                }
                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilterList = filterResults.values as ArrayList<AssetBean>
                notifyDataSetChanged()
                // 调用搜索回调方法，传递过滤后的数据
                searchCallback?.onSearchResults(mFilterList)
            }
        }
    }

    interface SearchCallback {
        fun onSearchResults(filteredData: ArrayList<AssetBean>)
    }

    private var searchCallback: SearchCallback? = null

    fun setSearchCallback(callback: SearchCallback) {
        searchCallback = callback
    }

    override fun getItemCount(): Int {
        return mFilterList.size
    }

    override fun hashCode(): Int {
        return mFilterList.hashCode()
    }
}