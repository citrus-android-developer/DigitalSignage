package com.citrus.digitalsignage.model.vo

import com.citrus.digitalsignage.R

class DataBean {
    var imageRes: Int? = null
    var imageUrl: String? = null
    var title: String?
    var viewType: Int

    constructor(imageRes: Int?, title: String?, viewType: Int) {
        this.imageRes = imageRes
        this.title = title
        this.viewType = viewType
    }

    constructor(imageUrl: String?, title: String?, viewType: Int) {
        this.imageUrl = imageUrl
        this.title = title
        this.viewType = viewType
    }

    companion object {
        //测试数据，如果图片链接失效请更换
        val testData3: List<DataBean>
            get() {
                val list: MutableList<DataBean> = ArrayList()
                list.add(
                    DataBean(
                        R.drawable.banner1,
                        null,
                        1
                    )
                )
                list.add(
                    DataBean(
                        R.drawable.banner2,
                        null,
                        1
                    )
                )
                list.add(
                    DataBean(
                        R.drawable.banner3,
                        null,
                        1
                    )
                )
                list.add(
                    DataBean(
                        R.drawable.banner4,
                        null,
                        1
                    )
                )
                return list
            }
    }
}