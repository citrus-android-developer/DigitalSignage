package com.citrus.digitalsignage.util

import android.content.Context
import android.os.Environment
import com.citrus.digitalsignage.viewmodel.DownloadStatus
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import kotlin.math.roundToInt


object Constants {
    const val SHARED_PREFERENCES_NAME = "sharedPref"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"


    const val GET_ALL_DATA = "/POS/digisinageWS/Service1.asmx/SetRegistered"
    const val IMG_DOMAIN ="http://cms.citrus.tw/demo/SIGINAGE/Assets/Upload/"

    const val KEY_SERVER_IP = "KEY_SERVER_IP"
    const val KEY_STORE_ID = "KEY_STORE_ID"
    const val KEY_DEVICE_ID = "KEY_DEVICE_ID"


    suspend fun HttpClient.downloadFile(file: File,url: String): Flow<DownloadStatus> {
        return flow {
            val response = call {
                url(url)
                method = HttpMethod.Get
            }.response
            val byteArray = ByteArray(response.contentLength()!!.toInt())
            var offset = 0
            do {
                val currentRead = response.content.readAvailable(byteArray, offset, byteArray.size)
                offset += currentRead
                val progress = (offset * 100f / byteArray.size).roundToInt()
                emit(DownloadStatus.Progress(progress))
            } while (currentRead > 0)
            response.close()
            if (response.status.isSuccess()) {
                file.writeBytes(byteArray)
                emit(DownloadStatus.Success)
            } else {
                emit(DownloadStatus.Error("File not downloaded"))
            }
        }
    }

}