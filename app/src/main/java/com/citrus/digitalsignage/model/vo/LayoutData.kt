package com.citrus.digitalsignage.model.vo
import com.google.gson.annotations.SerializedName



data class LayoutData(
    @SerializedName("data")
    val data: Data,
    @SerializedName("status")
    val status: Int
)


data class Data(
    @SerializedName("Device")
    val device: Device,
    @SerializedName("Layout")
    val layout: List<Layout>
)


data class Device(
    @SerializedName("CreateTime")
    val createTime: String,
    @SerializedName("CreateUser")
    val createUser: String,
    @SerializedName("DeviceID")
    val deviceID: String,
    @SerializedName("Flag")
    val flag: String,
    @SerializedName("LayoutID")
    val layoutID: String,
    @SerializedName("Location")
    val location: String,
    @SerializedName("Remark")
    val remark: String,
    @SerializedName("UpdateTime")
    val updateTime: String,
    @SerializedName("UpdateUser")
    val updateUser: String,
    @SerializedName("UserID")
    val userID: String
)


data class Layout(
    @SerializedName("CreateTime")
    val createTime: String,
    @SerializedName("CreateUser")
    val createUser: String,
    @SerializedName("Flag")
    val flag: String,
    @SerializedName("Layout_content01")
    val layoutContent01: String,
    @SerializedName("Layout_content02")
    val layoutContent02: String,
    @SerializedName("Layout_content03")
    var layoutContent03: String,
    @SerializedName("Layout_content04")
    var layoutContent04: String,
    @SerializedName("Layout_content05")
    val layoutContent05: String,
    @SerializedName("LayoutID")
    val layoutID: String,
    @SerializedName("LayoutName")
    val layoutName: String,
    @SerializedName("Layout_subtitle01")
    val layoutSubtitle01: Int,
    @SerializedName("Layout_subtitle02")
    val layoutSubtitle02: Int,
    @SerializedName("Layout_subtitle03")
    var layoutSubtitle03: Int,
    @SerializedName("Layout_subtitle04")
    var layoutSubtitle04: Int,
    @SerializedName("Layout_subtitle05")
    val layoutSubtitle05: Int,
    @SerializedName("Remark")
    val remark: String,
    @SerializedName("StyleID")
    val styleID: String,
    @SerializedName("UpdateTime")
    val updateTime: String,
    @SerializedName("UpdateUser")
    val updateUser: String
)