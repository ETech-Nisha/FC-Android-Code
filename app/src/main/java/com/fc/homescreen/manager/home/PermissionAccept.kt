package com.fc.homescreen.manager.home

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.AppApplication
import com.AppApplication.Companion.getInstance
import com.android.billingclient.api.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fc.PlaySubscriptionEvents
import com.fc.homescreen.R
import com.fc.homescreen.SplashActivity
import com.fc.homescreen.WebActivity
import com.fc.homescreen.apidata.ApiViewModel
import com.fc.homescreen.manager.detail.DetailActivity
import com.fc.homescreen.util.Utility
import com.google.gson.JsonObject
import dmax.dialog.SpotsDialog
import org.json.JSONObject
import java.util.*


class PermissionAccept : AppCompatActivity(), PurchasesUpdatedListener, BillingClientStateListener,
        PlaySubscriptionEvents.IResult {
    lateinit var viewModel: ApiViewModel
    lateinit var progress: AlertDialog
    lateinit var billingClient: BillingClient
    val SUBS_SKUS = listOf("product_001")
    companion object {
      //  lateinit var billingClient: BillingClient

        lateinit var context: Activity
    }

    private lateinit var serverEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                && this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK < Configuration.SCREENLAYOUT_SIZE_LARGE
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()!!.hide()
        setContentView(R.layout.permission)
        progress = SpotsDialog.Builder().setContext(this).setCancelable(false)
                .setMessage(getString(R.string.login_txt))
                .build()

        viewModel = ViewModelProviders.of(this@PermissionAccept).get(ApiViewModel::class.java)
          /*if (intent?.action != null) {
               handleIntent(getIntent(), this@PermissionAccept)
           } else {
               val i = Intent(Intent.ACTION_VIEW)
               i.data = Uri.parse(PlaySubscriptionEvents().apiServerUrl)
               *//*i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
               i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
               i.addFlags(Intent.FLAG_FROM_BACKGROUND)*//*
               context.startActivity(i)
               context.finish()
           }*/
        enableRuntimePermission()
    }

    private fun enableRuntimePermission() {
        val permissionArrays = arrayOf<String>(
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 1)
        } else {
            loadData()
        }
    }

    private fun loadData() {
        if (AppApplication.getPref(getInstance()!!).getString(
                        "email", ""
                ).equals("")
        ) {
            try {
                var acc = getAccountsName()
                serverEmail = acc!!.name
                validateSubscription(acc!!.name!!)
            }catch (ex:Exception){
                this@PermissionAccept.runOnUiThread(Runnable {
                    Utility().showAlert(this)
                })
            }
        } else {
            validateSubscription(
                    AppApplication.getPref(getInstance()!!)
                            .getString("email", "")
                            .toString()
            )
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0) {
                val GetAccountPermission =
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                val ReadPhoneStatePermission =
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (GetAccountPermission && ReadPhoneStatePermission) {
                    // loadData()
                    if (AppApplication.getPref(AppApplication.getInstance()!!).getString(
                            "email", ""
                        ).equals("")
                    ) {
                        loadData()
                    } else {
                        validateSubscription(
                            AppApplication.getPref(AppApplication.getInstance()!!)
                                .getString("email", "")
                                .toString()
                        )
                    }
                } else {
                    Toast.makeText(
                            this@PermissionAccept,
                            "Please accept Permission",
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun getAccountsName(): Account? {
        var possibleEmail = ""
        //var returnEmail = ""
        var emailPattern = Patterns.EMAIL_ADDRESS
        var accounts = AccountManager.get(this@PermissionAccept).getAccounts()
        //returnEmail = accounts!!.get(0).name
        for (account in accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = possibleEmail + "::" + account!!.name
            }
        }
        Log.v("Nisha:...", "email::" + possibleEmail)
        return accounts!!.get(0)
    }


     private fun getAccount(accountManager: AccountManager): Account? {
            val accounts =
                accountManager.getAccountsByType("com.google")
            val account: Account?
            account = if (accounts.size > 0) {
            accounts[0]
        } else {
            null
        }
        return account
    }

    fun validateSubscription(email: String) {
        Log.e("Nisha", "PermissionScriptionActivity validateSubscription")
        isPurchasedMain = 0
        connectToBillingService()
    }


    override fun onBillingServiceDisconnected() {
        if (billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        progress!!.dismiss()
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            checkSub(billingClient)
        } else {
             this@PermissionAccept.runOnUiThread(Runnable {
                 Utility().showAlert(this)
             })
        }
    }

    var isPurchasedMain = 0
    override fun onPurchasesUpdated(
            billingResult: BillingResult,
            purchases: MutableList<Purchase>?
    ) {
        Log.e("Nisha", "PermissionSubscriptionActivity")
        if (isPurchasedMain == 0) {
            Log.e("Nisha", "PermissionSubscriptionActivity inside")
            isPurchasedMain = 1
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    purchases?.apply {
                        Log.e("Nisha", "purchases")
                        processPurchases(this.toSet())
                    }
                }
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    Log.e("Nisha", "ITEM_ALREADY_OWNED")
                    queryPurchases()
                }
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                    Log.e("Nisha", "SERVICE_DISCONNECTED")
                    connectToBillingService()
                }
                else -> {
                    Log.e("Nisha", "Failed to onPurchasesUpdated")
                }
            }
        }
    }

    fun connectToBillingService() {
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this).build()
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    private fun processPurchases(purchases: Set<Purchase>) {
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                Log.e("Nisha", "processPurchases::::" + purchase.purchaseToken)
                Log.e("Nisha", "processPurchases::::" + purchase.orderId)
                Log.e("Nisha", "processPurchases::::" + purchase.purchaseTime)
                acknowledgePurchase(purchase, billingClient)
            }
        }
    }

    private fun queryPurchases() {
        Log.e("Nisha", "queryPurchases")
        val purchasesResult = HashSet<Purchase>()
        var result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.apply { purchasesResult.addAll(this) }
        result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.apply { purchasesResult.addAll(this) }
        processPurchases(purchasesResult)
    }

    fun checkSub(billingClient: BillingClient) {
        val purchasesResult = HashSet<Purchase>()
        var result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.apply { purchasesResult.addAll(this) }

        if (purchasesResult.size > 0) {
            isPurchased = true
        } else {
            isPurchased = false
        }
        if (isPurchased) {
            querySkuDetailsDetailAsync(BillingClient.SkuType.SUBS, SUBS_SKUS, result, billingClient)
        } else {
            querySkuDetailsAsync(BillingClient.SkuType.SUBS, SUBS_SKUS, billingClient)
        }
    }

    var isPurchased = false
    private val skusWithSkuDetails = mutableMapOf<String, SkuDetails>()
    private var dataList =
            ArrayList<SkuDetails>()
    private fun querySkuDetailsDetailAsync(
            @BillingClient.SkuType skuType: String,
            skuList: List<String>,
            result: Purchase.PurchasesResult, billingClient: BillingClient
    ) {
        val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build()

        billingClient.querySkuDetailsAsync(
                params
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (details in skuDetailsList) {
                    skusWithSkuDetails[details.sku] = details
                    dataList.add(details)
                }
                subPer = dataList.get(0).subscriptionPeriod

                Log.e(
                        "Nisha",
                        "checkSub" + isPurchased + "::" + result.purchasesList!!.get(0).purchaseToken
                )
                if (AppApplication.getPref(AppApplication.getInstance()!!)
                                .getString("email", "").toString()
                                .equals("")
                ) {
                    val editor = AppApplication.getPref(getInstance()!!).edit()
                    editor!!.putString("email", serverEmail)
                    editor!!.apply()
                }

                val editor = AppApplication.getPref(getInstance()!!).edit()
                editor!!.putString("is_from_sub", "false")
                editor!!.putString(
                        "email",
                        AppApplication.getPref(getInstance()!!)
                                .getString("email", "").toString()
                )
                editor!!.putString(
                        "S_Token",
                        result.purchasesList!!.get(0).purchaseToken
                )
                editor!!.putString(
                        "S_Time",
                        result.purchasesList!!.get(0).purchaseTime.toString()
                )
                editor!!.putString("S_Period", subPer)
                editor!!.apply()
                // var handler = Handler(Looper.getMainLooper());
                var handler = Handler(Looper.getMainLooper());
                handler.post(Runnable {
                    redirectToNextSection()
                });

            }
        }
    }

    private fun redirectToNextSection() {
        /* var is_from_sub = AppApplication.getPref(AppApplication.getInstance()!!).getString(
             "is_from_sub", ""
         )
         if (is_from_sub.equals("true")) {

             } catch (ex: Exception) {
                 Log.e("err", "api :::" + ex.message)
             }
         } else {*/
        var last_end_time = AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "E_Time", ""
        )
        /*if(last_end_time.equals("")){
            var mainIntent =
                Intent(this@PermissionAccept, WebActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
            finishAffinity()
        }else {
            if (last_end_time!!.toLong() <= System.currentTimeMillis()) {
                var mainIntent =
                    Intent(this@PermissionAccept, WebActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(mainIntent)
                finishAffinity()
            }else{
                if (SplashActivity.LAST_SCREEN.equals("Detail")) {
                    var mainIntent =
                        Intent(this@PermissionAccept, DetailActivity::class.java)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(mainIntent)
                    finishAffinity()
                } else {
                    var mainIntent =
                        Intent(this@PermissionAccept, MainActivity::class.java)
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(mainIntent)
                    finishAffinity()
                }
            }
        }*/
        // }
        /*var is_from_sub = AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "is_from_sub", ""
        )*/
        var mainIntent =
            Intent(this@PermissionAccept, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        finishAffinity()
    }


    override fun notifySuccess(response: JSONObject) {
        progress.dismiss()
        val expiryTimeMillis = response.getString("expiryTimeMillis")
        val editor = AppApplication.getPref(AppApplication.getInstance()!!).edit()
        editor!!.putString("E_Time", expiryTimeMillis.toString())
        editor!!.apply()
        if (SplashActivity.LAST_SCREEN.equals("Detail")) {
            var mainIntent =
                    Intent(this@PermissionAccept, DetailActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
            finishAffinity()
        } else {
            var mainIntent =
                    Intent(this@PermissionAccept, MainActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
            finishAffinity()
        }

    }


    override fun notifyError(error: VolleyError?) {
        progress.dismiss()

    }

    private fun querySkuDetailsAsync(
            @BillingClient.SkuType skuType: String,
            skuList: List<String>, billingClient: BillingClient
    ) {
        val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build()

        billingClient.querySkuDetailsAsync(
                params
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                // progress!!.dismiss()



                for (details in skuDetailsList) {
                    skusWithSkuDetails[details.sku] = details
                    dataList.add(details)
                }
                purchase(dataList.get(0), billingClient)
                subPer = dataList.get(0).subscriptionPeriod
            }
        }
    }

    var subPer = ""

    private fun purchase(skuDetails: SkuDetails, billingClient: BillingClient) {
        if (AppApplication.getPref(AppApplication.getInstance()!!)
                        .getString("email", "").toString()
                        .equals("")
        ) {
            val editor = AppApplication.getPref(getInstance()!!).edit()
            editor!!.putString("is_from_sub", "true")
            editor!!.putString("email", serverEmail)
            editor!!.apply()
        }
        var deviceid = Settings.Secure.getString(
                this@PermissionAccept.getContentResolver(),
                Settings.Secure.ANDROID_ID
        )
        val params = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .setObfuscatedAccountId(deviceid)
                .setObfuscatedProfileId(deviceid)
                .build()

        billingClient.launchBillingFlow(this, params)
                .takeIf { billingResult -> billingResult.responseCode != BillingClient.BillingResponseCode.OK }
                ?.let { billingResult ->
                    Log.e("Nisha", "Failed to launch billing flow $billingResult")
                }
    }


    private fun acknowledgePurchase(purchase: Purchase, billingClient: BillingClient) {
        acknowledge(purchase.purchaseToken, purchase.purchaseTime, billingClient)
    }

    private fun acknowledge(purchaseToken: String, time: Long, billingClient: BillingClient) {

        Log.e("Nisha", "acknowledge method")
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()

        billingClient.acknowledgePurchase(
                acknowledgePurchaseParams
        ) { billingResult ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    progress.dismiss()
                    Log.e("Nisha", "acknowledge purchase ::" + billingResult.toString())
                    var acc = getAccountsName()
                    serverEmail = acc!!.name
                    val editor = AppApplication.getPref(getInstance()!!).edit()
                    editor!!.putString("is_from_sub", "true")
                    editor!!.putString("email", serverEmail)
                    editor!!.putString("S_Token", purchaseToken)
                    editor!!.putString("S_Time", time.toString())
                    editor!!.putString("S_Period", subPer)
                    editor!!.apply()
                    var handler = Handler(Looper.getMainLooper());
                    handler.post(Runnable {
                        redirectToNextSection()
                    })
                }
                else -> {
                    progress.dismiss()
                    Log.e("Nisha", "Failed to acknowledge purchase $billingResult")
                }
            }
        }
    }
}