package com.yyc.smas.bean.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author nike
 * @Date 2023/7/28 10:22
 * @Description
 */
@Entity
open class AssetBean {

    @PrimaryKey(autoGenerate = true)//自增长
    var uid: Int = 0

    @ColumnInfo(name = "asset_id")
    var ids: String = ""

    @ColumnInfo(name = "asset_orderNo")
    var OrderRoNo: String = ""

    @ColumnInfo(name = "asset_assetNo")
    var AssetNo: String = ""

    @ColumnInfo(name = "asset_libraryCallNo")
    var LibraryCallNo: String = ""

    @ColumnInfo(name = "asset_archivesNo")
    var ArchivesNo: String = ""

    @ColumnInfo(name = "asset_remarks")
    var Remarks: String = ""

    @ColumnInfo(name = "asset_labelTag")
    var LabelTag: String = ""

    @ColumnInfo(name = "asset_title")
    var Title: String = ""

    @ColumnInfo(name = "asset_language")
    var Language: String = ""

    @ColumnInfo(name = "asset_image_list")
    var imageList: String = ""

    @ColumnInfo(name = "asset_location")
    var Location: String = ""

    @ColumnInfo(name = "asset_foundStatus")
    var FoundStatus: Int = 0

    @ColumnInfo(name = "asset_inventoryStatus")
    var InventoryStatus: Int = 0

    @ColumnInfo(name = "asset_status")
    var status: Int = 0

    @ColumnInfo(name = "asset_roNo")
    var RoNo: String = ""

    @ColumnInfo(name = "asset_scanTime")
    var scanTime: String = ""

    @ColumnInfo(name = "asset_scanStatus")
    var scanStatus: Int = 0

    @ColumnInfo(name = "asset_companyId")
    var companyId: String = ""

    @ColumnInfo(name = "asset_Type")
    var Type: String = ""

    @ColumnInfo(name = "asset_Author")
    var Author: String = ""

    @ColumnInfo(name = "asset_Editions_Year")
    var Editions_Year: String = ""

    @ColumnInfo(name = "asset_BorrowStatus")
    var BorrowStatus: Int = 0

    @ColumnInfo(name = "asset_StatusKey")
    var StatusKey: String = ""

    @ColumnInfo(name = "asset_Img")
    var Img: String = ""

    @ColumnInfo(name = "asset_LevelType")
    var LevelType: String = ""

    @ColumnInfo(name = "asset_BishopName")
    var BishopName: String = ""

    @ColumnInfo(name = "asset_ArchivesYear")
    var ArchivesYear: String = ""

    @ColumnInfo(name = "asset_StatusID")
    var StatusId: String = ""

    override fun toString(): String {
        return "AssetBean(uid=$uid, ids='$ids', OrderRoNo='$OrderRoNo', AssetNo='$AssetNo', LibraryCallNo='$LibraryCallNo', ArchivesNo='$ArchivesNo', Remarks='$Remarks', LabelTag='$LabelTag', Title='$Title', Language='$Language', imageList='$imageList', Location='$Location', FoundStatus=$FoundStatus, InventoryStatus=$InventoryStatus, status=$status, RoNo='$RoNo', scanTime='$scanTime', scanStatus=$scanStatus, companyId='$companyId', Type='$Type', Author='$Author', Editions_Year='$Editions_Year', BorrowStatus=$BorrowStatus, StatusKey='$StatusKey', Img='$Img', LevelType='$LevelType', BishopName='$BishopName', ArchivesYear='$ArchivesYear', StatusID='$StatusId')"
    }

}