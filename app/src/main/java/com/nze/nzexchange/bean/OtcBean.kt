package com.nze.nzexchange.bean

data class OtcBean(var name: String,
                   var min: Double = 0.0,
                   var max: Double = 0.0,
                   var totalNum: Double = 0.0,
                   var orderNum: Int = 0,
                   var price: Double = 0.0) {
    companion object {
        fun getList():MutableList<OtcBean> {
            val list = mutableListOf<OtcBean>()
            for (i in 0..9) {
                list.add(OtcBean("小韭菜$i", 832.32, 2133.13, 2124.00, 474, 1000.00))
            }
            return list
        }
    }
}