package com

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.billingclient.api.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fc.homescreen.apidata.ApiRepo
import kotlin.collections.HashMap
import kotlin.collections.HashSet


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class OnceADay : JobService(), PurchasesUpdatedListener, BillingClientStateListener {
    lateinit var billingClient: BillingClient
    lateinit var jobParameters: JobParameters
    lateinit var con: Context
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        Log.v("Service...","Checking subscription...")
        this.jobParameters = jobParameters
        billingClient =
            BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()

        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
        con = this

        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {

    }

    override fun onBillingServiceDisconnected() {
        if (billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(p0: BillingResult) {
        if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
            val purchasesResult = HashSet<Purchase>()
            var result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
            result.purchasesList?.apply { purchasesResult.addAll(this) }
            var res = if (purchasesResult.size > 0) "1" else "0"
            Log.v("Background Service::::", "msg app::" + res)
            var queuee = Volley.newRequestQueue(con)
            val postRequest = object : StringRequest(
                Request.Method.POST,
               "http://app.fractalchaos.com/api/androidBackendServicesForSubscriptionInfoV2",
                Response.Listener<String> { response ->
                    Log.v("Background Service::::", "get 1 :::" + response)
                },
                Response.ErrorListener { e ->
                    Log.v("Background Service::::", "get error 1 :::" + e.message)
                    e.printStackTrace()

                }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put(
                        "userEmail",
                        AppApplication.getPref(AppApplication.getInstance()!!).getString(
                            "email",
                            ""
                        ).toString()
                    )
                    params.put(
                        "current_date", ApiRepo().calulateDate(
                            System.currentTimeMillis().toString()
                        )
                    )
                    params.put(
                        "subscriptionId",
                        AppApplication.getPref(AppApplication.getInstance()!!).getString(
                            "S_Token",
                            ""
                        ).toString()
                    )
                    params.put(
                        "subscriptionStartDate", ApiRepo().calulateDate(
                            AppApplication.getPref(AppApplication.getInstance()!!).getString(
                                "S_Time", ""
                            )!!
                        )
                    )
                    params.put(
                        "subscriptionEndDate", ApiRepo().calulateDate(
                            AppApplication.getPref(AppApplication.getInstance()!!).getString(
                                "E_Time", ""
                            )!!
                        )
                    )
                    params.put("isAndroidUserSubscribed", res)
                    return params
                }
            }
            queuee.add(postRequest)
        }
    }
}




