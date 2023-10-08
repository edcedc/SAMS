package com.yyc.smas.bean.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author nike
 * @Date 2023/7/25 15:31
 * @Description
 */
@Entity
open class OrderBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "order_id")
    var ID: String = ""

    @ColumnInfo(name = "order_stocktakeno")
    var stocktakeno: String = ""

    @ColumnInfo(name = "order_orderType")
    var OrderType: Int = 0

    @ColumnInfo(name = "order_orderNo")
    var OrderNo: String = ""

    @ColumnInfo(name = "order_orderName")
    var OrderName: String = ""

    @ColumnInfo(name = "order_startDate")
    var StartDate: String = ""

    @ColumnInfo(name = "order_createDate")
    var CreateDate: String = ""

    @ColumnInfo(name = "order_endDate")
    var EndDate: String = ""

    @ColumnInfo(name = "order_progress")
    var progress: Int = 0

    @ColumnInfo(name = "order_remarks")
    var Remarks: String = ""

    @ColumnInfo(name = "order_roNo")
    var RoNo: String = ""

    @ColumnInfo(name = "order_total_count")
    var Total_count: Int = 0

    @ColumnInfo(name = "order_number_of_discs")
    var Number_of_discs: Int = 0

    @ColumnInfo(name = "order_companyId")
    var companyId: String = ""

    override fun toString(): String {
        return "OrderBean(uid=$uid, ID='$ID', stocktakeno='$stocktakeno', OrderType=$OrderType, OrderNo='$OrderNo', OrderName='$OrderName', CreateDate='$CreateDate', EndDate='$EndDate', progress=$progress, remarks='$Remarks', RoNo='$RoNo', Total_count=$Total_count, Number_of_discs=$Number_of_discs)"
    }

}