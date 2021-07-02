package com.citrus.digitalsignage.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import kotlin.math.roundToInt

sealed class DownloadStatus {
    object Success : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
    data class Progress(val progress: Int) : DownloadStatus()
}

suspend fun HttpClient.downloadFile(file: File, url: String): Flow<DownloadStatus> {
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
            emit(DownloadStatus.Error(response.status.description))
        }
    }
}
