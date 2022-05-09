package com.fc.homescreen.manager.home.model

import BaseModel
import java.io.Serializable

class ValidateSubscriptionModel : BaseModel, Serializable {
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
        var subscriptionEndDate: String,
        var userSubscriptionDays: String,
        var isUserSubscriptionActive: String,
        var subscriptionStartDate: String,
        var subscriptionId: String
    ) : Serializable
}

