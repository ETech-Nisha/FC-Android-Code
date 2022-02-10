package com.fractal.chaoss.apidata

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.AppApplication
import com.app.myapplication.RetrofitClient
import com.fractal.chaoss.SplashActivity
import com.fractal.chaoss.manager.detail.model.SelectWallaperModel
import com.fractal.chaoss.manager.home.model.ValidateSubscriptionModel
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.SubscriptionPurchase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ApiRepo {
    fun getInstance(): ApiRepo? {
        if (repository == null) {
            repository =
                ApiRepo()
        }
        return repository
    }

    fun getdeviceDetail(context: Context):StringBuffer{
        val infoBuffer = StringBuffer()
        //infoBuffer.append("""${Build.MANUFACTURER}""".trimIndent()) //The name of the industrial design.
        infoBuffer.append("-" + """${Build.MODEL}""".trimIndent()) //The end-user-visible name for the end product.
        //infoBuffer.append("-"+"""${Build.BRAND}""".trimIndent()) //The consumer-visible brand with which the product/hardware will be associated, if any.
        //infoBuffer.append("-"+"""${Build.SERIAL}""".trimIndent())
        Log.v("Nisha", "......" + infoBuffer)
        return infoBuffer
    }

    fun getData(type: String, context: Context): MutableLiveData<JsonObject?> {
        val newsModel: MutableLiveData<JsonObject?> = MutableLiveData<JsonObject?>()
        var deviceId = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )

        var rejultObj = JsonObject()
        rejultObj.addProperty("deviceType", type)
        rejultObj.addProperty("deviceToken", deviceId)
       /* rejultObj.addProperty("refresh_token",  AppApplication.getPref(AppApplication.getInstance()!!).getString(
            "refresh_token", ""
        ).toString())
        rejultObj.addProperty("access_token",  AppApplication.getPref(AppApplication.getInstance()!!).getString(
            "play_access_token", ""
        ).toString())
        rejultObj.addProperty("expiryTimeMillis", calulateDate(
            AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "E_Time", "")!!
        ))*/
        rejultObj.addProperty("current_date", getCurrTime())
        rejultObj.addProperty(
            "userEmail", AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "email", ""
            )
        )
        rejultObj.addProperty("deviceName", "Android" + getdeviceDetail(context))
        if (SplashActivity.PNToken.equals("") || SplashActivity.PNToken.equals("null") || SplashActivity.PNToken==null ) {

        } else {
            rejultObj.addProperty("pnToken", SplashActivity.PNToken)
        }
        rejultObj.addProperty(
            "subscriptionId", AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "S_Token", ""
            ).toString()
        )
        val json: String = Gson().toJson(rejultObj)
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(json).asJsonObject
        RetrofitClient.instance!!.homeApi(jsonObject).enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: retrofit2.Call<JsonObject>,
                response: Response<JsonObject?>
            ) {
                if (response.isSuccessful()) {
                    response.body()
                    newsModel.setValue(response.body())
                } else {
                    val gson = Gson()
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val json = gson.toJson(jObjError)
                        val jsonParser = JsonParser()
                        val jsonObject = jsonParser.parse(json).asJsonObject
                        newsModel.setValue(jsonObject.get("nameValuePairs").asJsonObject)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable?) {
                Toast.makeText(
                    context,
                    t!!.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return newsModel
    }

    fun selectWallapper(
        type: String,
        id: String,
        context: Context
    ): MutableLiveData<SelectWallaperModel?> {
        val data: MutableLiveData<SelectWallaperModel?> = MutableLiveData<SelectWallaperModel?>()
        var deviceId = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        var rejultObj = JsonObject()
        rejultObj.addProperty("deviceToken", deviceId)
        rejultObj.addProperty("selWallpaperId", id)
        rejultObj.addProperty("selWallpaperType", type)
        rejultObj.addProperty(
            "subscriptionId", AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "S_Token", ""
            ).toString()
        )
        rejultObj.addProperty("userSubscriptionType", "0")
        rejultObj.addProperty(
            "userEmail", AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "email", ""
            )
        )


        val json: String = Gson().toJson(rejultObj)
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(json).asJsonObject
        RetrofitClient.instance!!.selectedWallpaperForAndroidUsers(jsonObject)
            .enqueue(object : Callback<SelectWallaperModel> {
                override fun onResponse(
                    call: retrofit2.Call<SelectWallaperModel>,
                    response: Response<SelectWallaperModel?>
                ) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body())
                    }
                }

                override fun onFailure(call: retrofit2.Call<SelectWallaperModel>, t: Throwable?) {
                    data.setValue(null)
                }
            })
        return data
    }


    fun subscribePlayStore(context: Context, email: String): MutableLiveData<JsonObject?> {
        val data: MutableLiveData<JsonObject?> = MutableLiveData<JsonObject?>()
        var deviceId = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        var rejultObj = JsonObject()
        rejultObj.addProperty("deviceToken", deviceId)
        rejultObj.addProperty(
            "subscriptionId", AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "S_Token", ""
            ).toString()
        )
        rejultObj.addProperty(
            "subscriptionStartDate", calulateDate(
                AppApplication.getPref(AppApplication.getInstance()!!).getString(
                    "S_Time", ""
                )!!
            )
        )
        rejultObj.addProperty("userSubscriptionDays", calculatePeriod(""))
        rejultObj.addProperty("subscriptionEndDate",  calulateExpireDate())
        rejultObj.addProperty("userEmail", email)
        val json: String = Gson().toJson(rejultObj)
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(json).asJsonObject
        RetrofitClient.instance!!.addUpdSubscriptionForAndroidUsers(jsonObject)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: retrofit2.Call<JsonObject>,
                    response: Response<JsonObject?>
                ) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body())
                    }
                }

                override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable?) {
                    data.setValue(null)
                }
            })
        return data
    }

    fun calulateExpireDate(per: String = ""):String{
        var time = AppApplication.getPref(AppApplication.getInstance()!!).getString(
            "S_Time", ""
        ).toString()
        var period:String
        if(per.equals("")) {
            period = AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "S_Period", ""
            ).toString()
        }else{
            period=per
        }
        val purchaseDate = Date(time.toLong())
        val calendar = Calendar.getInstance()
        calendar.time = purchaseDate
        val now = Date()
        Log.v("Nisha", "period:::" + period)
        Log.v("Nisha", "purchaseDate:::" + purchaseDate)
        //while (calendar.time.before(now)) {
            when (period) {
                "P1W" -> calendar.add(Calendar.HOUR, 7 * 24)
                "P1M" -> calendar.add(Calendar.MONTH, 1)
                "P3M" -> calendar.add(Calendar.MONTH, 3)
                "P6M" -> calendar.add(Calendar.MONTH, 6)
                "P1Y" -> calendar.add(Calendar.YEAR, 1)
                "P1H" -> calendar.add(Calendar.MINUTE, 30)
            }
        //}

        Log.v("Nisha", "Date:::" + calendar.time)  //Mon Aug 16 11:09:39 GMT+05:30 2021
        var outputDateStr = parseDate(calendar.time.toString())
        Log.v("Nisha", "Date new" + outputDateStr)
        return outputDateStr.toString()
    }
    fun getCurrTime(): String {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date: String = df.format(Calendar.getInstance().time)
        return date
    }

    fun calulateDate(time: String):String{
        val purchaseDate = Date(time.toLong())
        val calendar = Calendar.getInstance()
        calendar.time = purchaseDate
        Log.v("Nisha", "Date:::" + calendar.time)  //Mon Aug 16 11:09:39 GMT+05:30 2021
        var outputDateStr = parseDate(calendar.time.toString())
        Log.v("Nisha", "Date new" + outputDateStr)
        return outputDateStr.toString()
    }


    fun calculatePeriod(per: String = ""):String{
        var valu:String=""
        var preiod ="30"
        if(per.equals("")) {
           val valu = AppApplication.getPref(AppApplication.getInstance()!!).getString(
               "S_Period", ""
           ).toString()
        }else{
            valu=per
        }
        when (valu) {
            "P1W" -> preiod = "7"
            "P1M" -> preiod = "30"
            "P3M" -> preiod = "90"
            "P6M" -> preiod = "180"
            "P1Y" -> preiod = "365"
            "P1H" -> preiod = "0"
        }
        return preiod
    }

    fun parseDate(
        inputDateString: String?
    ): String? {
        var date: Date? = null
        var outputDateString: String? = null
        try {
            date = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDateString)
            outputDateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return outputDateString
    }


    fun validateSubscription(
        context: Context,
        email: String
    ): MutableLiveData<ValidateSubscriptionModel?> {
        val data: MutableLiveData<ValidateSubscriptionModel?> =
            MutableLiveData<ValidateSubscriptionModel?>()
        var deviceId = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        var rejultObj = JsonObject()
        rejultObj.addProperty("deviceToken", deviceId)
        rejultObj.addProperty(
            "subscriptionId", AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "S_Token", ""
            ).toString()
        )
        rejultObj.addProperty("userEmail", email)
        val json: String = Gson().toJson(rejultObj)
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(json).asJsonObject
        RetrofitClient.instance!!.getSubscriptionInfoForAndroidUsers(jsonObject)
            .enqueue(object : Callback<ValidateSubscriptionModel> {
                override fun onResponse(
                    call: retrofit2.Call<ValidateSubscriptionModel>,
                    response: Response<ValidateSubscriptionModel?>
                ) {
                    if (response.isSuccessful()) {
                        data.setValue(response.body())
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ValidateSubscriptionModel>,
                    t: Throwable?
                ) {
                    data.setValue(null)
                }
            })
        return data
    }

    fun getRefreshToken(): LiveData<String?>? {
        val data: MutableLiveData<String?> =
            MutableLiveData<String?>()
        var refreshToken: String = ""
        var accessToken: String = ""
        var rejultObj = JsonObject()
        rejultObj.addProperty("grant_type", "authorization_code")
        rejultObj.addProperty("client_id", GOOGLE_CLIENT_ID)
        rejultObj.addProperty("client_secret", GOOGLE_CLIENT_SECRET)
        rejultObj.addProperty("code", CODE)
        rejultObj.addProperty("redirect_uri", GOOGLE_REDIRECT_URI)
        rejultObj.addProperty("access_type", "offline")
        val json: String = Gson().toJson(rejultObj)
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(json).asJsonObject
        RetrofitClient.instanceGmail!!.getToken(jsonObject)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: retrofit2.Call<JsonObject>,
                    response: Response<JsonObject?>
                ) {
                    if (response.isSuccessful()) {
                        refreshToken = response.body()?.get("refresh_token").toString()
                        accessToken = getAccessToken()
                        data.value = refreshToken + "-" + accessToken
                    }
                }

                override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable?) {
                    data.value = ""
                }
            })
        return data
    }

    fun getAccessToken(): String {
        var accessToken: String = ""
        var rejultObj = JsonObject()
        rejultObj.addProperty("grant_type", "refresh_token")
        rejultObj.addProperty("client_id", GOOGLE_CLIENT_ID)
        rejultObj.addProperty("client_secret", GOOGLE_CLIENT_SECRET)
        rejultObj.addProperty("redirect_uri", GOOGLE_REDIRECT_URI)
        val json: String = Gson().toJson(rejultObj)
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(json).asJsonObject
        RetrofitClient.instanceGmail!!.getToken(jsonObject)
            .enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: retrofit2.Call<JsonObject>,
                    response: Response<JsonObject?>
                ) {
                    if (response.isSuccessful()) {
                        accessToken = response.body()?.get("access_token").toString()
                        // getSubscriptionExpire(accessToken,refreshToken,)
                    }
                }

                override fun onFailure(call: retrofit2.Call<JsonObject>, t: Throwable?) {

                }
            })
        return accessToken
    }


    fun getSubscriptionExpire(
        accessToken: String,
        refreshToken: String,
        subscriptionId: String,
        purchaseToken: String
    ): LiveData<Long?>?   {
        val data: MutableLiveData<Long?> =
            MutableLiveData<Long?>()
        try {
            var tokenResponse = TokenResponse()
            tokenResponse.setAccessToken(accessToken)
            tokenResponse.setRefreshToken(refreshToken)
            tokenResponse.setExpiresInSeconds(3600L)
            tokenResponse.setScope("https://www.googleapis.com/auth/androidpublisher")
            tokenResponse.setTokenType("Bearer")

            var credential = GoogleCredential.Builder()
                .setTransport(HTTP_TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET)
                .build()
                .setFromTokenResponse(tokenResponse)

            var publisher = AndroidPublisher.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                credential
            ).setApplicationName("FractalChaosTest").build()

            var purchases = publisher.purchases() as AndroidPublisher.Purchases
            var get = purchases.products().get("com.fractal.chaos", subscriptionId, purchaseToken)
            var subscripcion = get.execute() as SubscriptionPurchase
            data.value=subscripcion.expiryTimeMillis

        } catch (e: IOException) {
            data.value=0
        }
        return data
    }


    companion object {
        var repository: ApiRepo? = null
        var GOOGLE_CLIENT_ID =
            "588171352860-7q3uuh1sfhob79q3vu8cb39noi7kf753.apps.googleusercontent.com"
        var GOOGLE_CLIENT_SECRET = "hjX8uscIp32J-D3vCgme6BFj"
        var GOOGLE_REDIRECT_URI = "http://ec2-3-7-135-212.ap-south-1.compute.amazonaws.com/login"
        var CODE = "4/0AX4XfWhYm7APTZk6NXoz8m88in4DGe_Kdz__NTrU5UBnQ8EBhrLJ3_mSr_ULvZjq_LTxpw"
        var HTTP_TRANSPORT = NetHttpTransport()
        var JSON_FACTORY = JacksonFactory()
    }

}