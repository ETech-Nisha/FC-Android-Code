package com.fc.homescreen.manager.home.model

import BaseModel
import java.io.Serializable

class SubscriptionSaveModel : BaseModel, Serializable {
    constructor() {
        super.isSuccess = isSuccess
        super.errorCode = errorCode
        super.errorMessage = errorMessage
        super.cause = cause
    }

    var `data`: SubscriptionData? = null

    data class SubscriptionData(
        var userEmail: String,
        var deviceToken: String,
        var userSubscriptionType: String,
        var subscriptionDate: String
    ) : Serializable
}
