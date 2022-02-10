package com.fractal.chaoss.manager.home.model

import BaseModel
import java.io.Serializable
import java.util.*

class HomeModel : BaseModel, Serializable {
    constructor() {
        super.isSuccess = isSuccess
        super.errorCode = errorCode
        super.errorMessage = errorMessage
        super.cause = cause
    }

    var `data`: Data? = null
    class Data(
        var past: List<Filter>? = null,
        var current: List<Filter>? = null,
        var upcoming: List<Filter>? = null,
        var `userCanChangeWallpaper`: String? = ""
    )

    data class Filter(
        var wallpaperName: String,
        var wallpaperId: Int,
        var wallpaperDate: Date,
        var fileName: String,
        var url: String
    ) : Serializable
}



