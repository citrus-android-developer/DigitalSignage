package com.citrus.digitalsignage.model


import android.util.Log
import androidx.datastore.dataStore
import com.citrus.digitalsignage.model.api.ApiService
import com.citrus.digitalsignage.model.vo.SendRequest
import com.google.gson.Gson
import com.skydoves.sandwich.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class Repository @Inject constructor(private val apiService: ApiService) {

    fun getLayoutData(url: String, sendRequest: SendRequest, onError: () -> Unit) = flow {
        val jsonString = Gson().toJson(sendRequest)
        Log.e("jsonString",jsonString)
        apiService.getLayoutData(url, jsonString)
            .suspendOnSuccess {
                data?.let {
                   if(it.status == 1){
                       emit(it.data.layout[0])
                   }else{
                       onError()
                   }
                }
            }.suspendOnError(ErrorEnvelopeMapper) {
                val message = this.message
                Log.e("onError",message)
                onError()
            }
    }.flowOn(Dispatchers.IO)



}

data class ErrorEnvelope(
    val code: Int,
    val message: String
)


object ErrorEnvelopeMapper : ApiErrorModelMapper<ErrorEnvelope> {

    override fun map(apiErrorResponse: ApiResponse.Failure.Error<*>): ErrorEnvelope {
        return ErrorEnvelope(apiErrorResponse.statusCode.code, apiErrorResponse.message())
    }
}


