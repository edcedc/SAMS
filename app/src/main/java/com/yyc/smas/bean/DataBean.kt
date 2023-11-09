package com.yyc.smas.bean


import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import me.hgj.jetpackmvvm.callback.databind.StringObservableField

/**
 * Created by yc on 2017/8/17.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class DataBean(
    var id: String? = null,
    var LoginID: String? = null,
    var RoNo: String = "",
    var type: Int = 0,
    var mType: Int = 0,
    var Borrowstatus: Int = 0,
    var Status: Int = 0,
    var pImgfile: String? = null,
    var Time: String? = null,
    var Language: String? = null,
    var Img: String? = null,
    var ArchivesYear: String? = null,
    var text: String? = null,
    var companyId: String? = null,
    var OrderNo: String? = null,
    var Disposal_speed: String? = null,
    var Editions_Year: String? = null,
    var AssetNo: String? = null,
    var LevelType: String? = null,
    var ArchivesType: String? = null,
    var QRCode: String? = null,
    var DisposalModel: String? = null,
    var Location: String? = null,
    var Author: String? = null,
    var Title: String? = null,
    var LibraryCallNo: String? = null,
    var ArchivesNo: String? = null,
    var LabelTag: String? = null,
    var OrderName: String? = null,
    var org: String? = null,
    var us: String? = null,
    var BorrowDate: String? = null,
    var Phone: String? = null,
    var Progress: String? = null,
    var Password: String? = null,
    var StatusID: Int = 0
) : Parcelable