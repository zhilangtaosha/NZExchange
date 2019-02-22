package com.nze.nzexchange.bean
//{"code":"5000","message":"已经存在登录邮箱为16681805@qq.com的用户","page":1,"result":"","success":false,"totalSize":1}
data class Result<T>(
        var code: Int = 0,
        var success: Boolean = false,
        var message: String = "",
        var totalSize: Int = 0,
        var page: Int = 0,
        var pageSize: Int = 0,
        var result: T
)