package com.citrus.digitalsignage.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.digitalsignage.di.prefs
import com.citrus.digitalsignage.model.Repository
import com.citrus.digitalsignage.model.vo.DataBean
import com.citrus.digitalsignage.model.vo.Layout
import com.citrus.digitalsignage.model.vo.SendRequest
import com.citrus.digitalsignage.util.Constants
import com.citrus.digitalsignage.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


sealed class BlockStatus {
    data class StatusInfo(val type: ShowType, val param: Any) : BlockStatus()
}

sealed class FetchStatus {
    object FirstTime : FetchStatus()
    object ReFetchAndNotChange : FetchStatus()
    object ReFetchAndNeedChange : FetchStatus()
}

enum class ShowType {
    VIDEO, IMG, BANNER, URL
}

enum class LayoutID {
    A01, A02, A03, A04, A05
}



class SharedViewModel @ViewModelInject constructor(private val model: Repository) : ViewModel() {

    /**排程任務*/
    lateinit var job: Job
    private fun isJobInit() = ::job.isInitialized

    /**暫存上一次撈取的資料*/
    lateinit var preLayout: Layout
    private fun isLayoutInit() = ::preLayout.isInitialized

    /**對應版面編號*/
    private val _layoutID = SingleLiveEvent<LayoutID>()
    val layoutID: SingleLiveEvent<LayoutID>
        get() = _layoutID

    /**更版用*/
    private val _triggerUpdate = SingleLiveEvent<Boolean>()
    val triggerUpdate: SingleLiveEvent<Boolean>
        get() = _triggerUpdate

    /**p1~p4對應版面裡的區塊位置*/
    private val _p1 = MutableLiveData<BlockStatus>()
    val p1: LiveData<BlockStatus>
        get() = _p1

    private val _p2 = MutableLiveData<BlockStatus>()
    val p2: LiveData<BlockStatus>
        get() = _p2

    private val _p3 = MutableLiveData<BlockStatus>()
    val p3: LiveData<BlockStatus>
        get() = _p3

    private val _p4 = MutableLiveData<BlockStatus>()
    val p4: LiveData<BlockStatus>
        get() = _p4


    /**定時排程*/
    private fun startTimer() {
        job = CoroutineScope(Dispatchers.Main).launch {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    fetchLayoutInfo()
                }
            }, getTimeInMillis(), 60 * 60 * 1000)
            /**下一個整點啟動，間隔一小時(每個整點撈取)*/
        }
    }


    /**撈取版面資料*/
    fun fetchLayoutInfo() =
        viewModelScope.launch {
            model.getLayoutData(
                prefs.serverIP + Constants.GET_ALL_DATA,
                SendRequest(prefs.deviceID, prefs.storeID)
            ).collect { layout ->

                preLayout = when(fetchDataType(layout)){
                    is FetchStatus.FirstTime -> {
                        layout
                    }

                    is FetchStatus.ReFetchAndNotChange -> {
                        return@collect
                    }

                    is FetchStatus.ReFetchAndNeedChange -> {
                        layout
                    }
                }

                defineBlockStyle(preLayout)

                /**初次執行未init則啟動排程*/
                if (!isJobInit()) {
                    startTimer()
                }
            }
        }


    /**當前fetch狀態分析*/
    private fun fetchDataType(layout: Layout):FetchStatus{
        if(!isLayoutInit()){
            return FetchStatus.FirstTime
        }

        return if(layout == preLayout){
            FetchStatus.ReFetchAndNotChange
        }else{
            FetchStatus.ReFetchAndNeedChange
        }
    }




    /**輪播所需圖片url拆解*/
    private fun spiltDataBean(para: String): List<DataBean> {
        var dataBeans = mutableListOf<DataBean>()
        var urlAry = para.split(";")
        for (element in urlAry) {
            var dataBean = DataBean(Constants.IMG_DOMAIN + element, "", 1)
            dataBeans.add(dataBean)
        }
        return dataBeans
    }


    /**定義版面類型*/
    private fun defineStyle(styleId: String): LayoutID {
        return when (styleId) {
            "A01" -> LayoutID.A01
            "A02" -> LayoutID.A02
            "A03" -> LayoutID.A03
            "A04" -> LayoutID.A04
            "A05" -> LayoutID.A05
            else -> LayoutID.A01
        }
    }

    /**定義版面內區塊資料類型*/
    private fun defineType(layoutSubtitle: Int): ShowType {
        return when (layoutSubtitle) {
            1 -> ShowType.IMG
            2 -> ShowType.VIDEO
            4 -> ShowType.BANNER
            5 -> ShowType.URL
            else -> ShowType.IMG
        }
    }


    /**定義版面內區塊類型*/
    private fun defineBlockStyle(layout: Layout) {
        _layoutID.postValue(defineStyle(layout.styleID))
        _p1.postValue(
            BlockStatus.StatusInfo(
                defineType(layout.layoutSubtitle01),
                if (layout.layoutSubtitle01 != 4) layout.layoutContent01 else spiltDataBean(
                    layout.layoutContent01
                )
            )
        )
        _p2.postValue(
            BlockStatus.StatusInfo(
                defineType(layout.layoutSubtitle02),
                if (layout.layoutSubtitle02 != 4) layout.layoutContent02 else spiltDataBean(
                    layout.layoutContent02
                )
            )
        )
        _p3.postValue(
            BlockStatus.StatusInfo(
                defineType(layout.layoutSubtitle03),
                if (layout.layoutSubtitle03 != 4) layout.layoutContent03 else spiltDataBean(
                    layout.layoutContent03
                )
            )
        )
        _p4.postValue(
            BlockStatus.StatusInfo(
                defineType(layout.layoutSubtitle04),
                if (layout.layoutSubtitle04 != 4) layout.layoutContent04 else spiltDataBean(
                    layout.layoutContent04
                )
            )
        )
    }

    /**計算初次啟動距離整點毫秒數*/
    private fun getTimeInMillis(): Long {
        val currentTime = Calendar.getInstance()
        val nextHourTime = Calendar.getInstance()
        nextHourTime.set(Calendar.MINUTE, 0)
        nextHourTime.set(Calendar.SECOND, 0)
        nextHourTime.set(Calendar.HOUR_OF_DAY, nextHourTime.get(Calendar.HOUR_OF_DAY) + 1)
        return (nextHourTime.timeInMillis - currentTime.timeInMillis)
    }


    /**更版*/
    fun intentUpdate() {
        _triggerUpdate.postValue(true)
    }
}