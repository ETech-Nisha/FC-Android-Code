package com.fractal.chaoss.manager.detail.model

import BaseModel
import java.io.Serializable

class SelectWallaperModel : BaseModel, Serializable {
    constructor() {
        super.isSuccess = isSuccess
        super.errorCode = errorCode
        super.errorMessage = errorMessage
        super.cause = cause
    }

    var `data`: WallpaperData? = null

    data class WallpaperData(
        var userEmail: String,
        var deviceToken: String,
        var userSubscriptionType: String,
        var selWallpaperId: String,
        var selWallpaperType: String
    ) : Serializable
}


