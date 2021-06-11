package com.citrus.digitalsignage.model.api

import com.citrus.digitalsignage.model.vo.LayoutData
import com.citrus.digitalsignage.util.ResponseFormat
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*


interface ApiService {

    companion object {
        const val BASE_URL = "https://cms.citrus.tw/"
    }

    /**取得layout data*/
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @ResponseFormat("json")
    @POST
    suspend fun getLayoutData(
        @Url url: String,
        @Field("jsonData") jsonData: String
    ): ApiResponse<LayoutData>



}