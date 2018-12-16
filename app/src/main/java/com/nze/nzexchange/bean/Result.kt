package com.nze.nzexchange.bean

data class Result<T>(
        var code: Int = 0,
        var success: Boolean = false,
        var message: String = "",
        var totalSize: Int = 0,
        var page: Int = 0,
        var pageSize: Int = 0,
        var result: T
)