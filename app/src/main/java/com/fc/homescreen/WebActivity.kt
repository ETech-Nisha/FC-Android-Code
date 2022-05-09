package com.fc.homescreen

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.AppApplication
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.fc.homescreen.apidata.ApiViewModel
import com.fc.homescreen.manager.detail.DetailActivity
import com.fc.homescreen.manager.home.MainActivity
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.HashMap


class WebActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var homeintent: Intent
    lateinit var button: Button
    lateinit var linearProgressIndicator: LinearProgressIndicator
    lateinit var viewModel: ApiViewModel
    companion object {
        const val TAG = "WebActivity"
        const val GOOGLE_CLIENT_ID =
            "1096942657526-pe72v8rd230qp4ccv2sns8qm8evkd2sb.apps.googleusercontent.com"

        const val GOOGLE_CLIENT_SECRET = "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"
        const val GOOGLE_REDIRECT_URI =
            "https://app.fractalchaos.com/redirectGoogleoath2Result.php"
    }

    var apiServerUrl =
        "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher" +
                "&response_type=code&prompt=consent&access_type=offline&" +
                "redirect_uri=" + GOOGLE_REDIRECT_URI + "&client_id=" + GOOGLE_CLIENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            && this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK < Configuration.SCREENLAYOUT_SIZE_LARGE
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        homeintent = intent
        getSupportActionBar()!!.hide()
        setContentView(R.layout.web)
        linearProgressIndicator = findViewById(R.id.linearProgressIndicator)
        viewModel = ViewModelProviders.of(this@WebActivity).get(ApiViewModel::class.java)
        button = findViewById<Button>(R.id.btn)
        button.setOnClickListener(this)
        if (intent?.data == null) {
            val i = Intent(Intent.ACTION_VIEW)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP  and Intent.FLAG_ACTIVITY_NO_HISTORY and Intent.FLAG_FROM_BACKGROUND
            i.data = Uri.parse(apiServerUrl)
            startActivity(i)
            finish()
        } else {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data
        Log.v(TAG, "appLinkAction::" + appLinkAction)
        Log.v(TAG, "appLinkData::" + appLinkData)
        if (appLinkData != null) {
            val authcode = appLinkData.toString()
                .split("code=")[1].split("&scope=")[0] // can be assign at runtime
            getAuthCode(authcode)
        }
    }

    private fun getAuthCode(authcode: String) {
        //linearProgressIndicator.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this@WebActivity)
        val postRequest = object : StringRequest(
            Request.Method.POST,
            "https://accounts.google.com/o/oauth2/token",
            Response.Listener<String> { response ->
                Log.v("data", "get 1 :::" + response)
                val mainObject = JSONObject(response)
                val refresh_token = mainObject.getString("refresh_token")
                val editor = AppApplication.getPref(AppApplication.getInstance()!!).edit()
                editor!!.putString("refresh_token", refresh_token)
                editor!!.apply()
                getAppRefreshToken(refresh_token)
            },
            Response.ErrorListener { e ->
                //  linearProgressIndicator.visibility = View.GONE
                Log.v("data", "get error 1 :::" + e.message)
                Log.v("data", "get error 1 :::" + e.printStackTrace())
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("grant_type", "authorization_code")
                params.put(
                    "client_id",
                    "1096942657526-pe72v8rd230qp4ccv2sns8qm8evkd2sb.apps.googleusercontent.com"
                )
                params.put("client_secret", "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")
                params.put("code", authcode)
                params.put(
                    "redirect_uri",
                    "https://app.fractalchaos.com/redirectGoogleoath2Result.php"
                )
                Log.v("saved ", "params::" + params)
                return params
            }
        }
        queue.add(postRequest)
    }

    private fun getAppRefreshToken(refresh_token: String) {
        val queuee = Volley.newRequestQueue(this@WebActivity)

        val postRequest = object : StringRequest(
            Request.Method.POST,
            "https://accounts.google.com/o/oauth2/token",
            Response.Listener<String> { response ->
                Log.v("data", "get 1 :::" + response)
                val mainObject = JSONObject(response)
                val access_token = mainObject.getString("access_token")
                val editor = AppApplication.getPref(AppApplication.getInstance()!!).edit()
                editor!!.putString("play_access_token", access_token)
                editor!!.apply()
                getAppExpiryDetail()
            },
            Response.ErrorListener { e ->
                linearProgressIndicator.visibility = View.GONE
                Log.v("data", "get error 1 :::" + e.message)
                Log.v("data", "get error 1 :::" + e.printStackTrace())
                e.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("grant_type", "refresh_token")
                params.put(
                    "client_id",
                    "1096942657526-pe72v8rd230qp4ccv2sns8qm8evkd2sb.apps.googleusercontent.com"
                )
                params.put("client_secret", "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")
                params.put("refresh_token", refresh_token)
                return params
            }
        }
        queuee.add(postRequest)

    }
    val SUBS_SKUS = listOf("product_001")
    private fun getAppExpiryDetail() {
        val queuee = Volley.newRequestQueue(this@WebActivity)
        var play_access_token = AppApplication.getPref(AppApplication.getInstance()!!).getString(
            "play_access_token", ""
        )
        var url =
            "https://www.googleapis.com/androidpublisher/v3/applications/com.fc.homescreen/purchases/subscriptions/" +
                    SUBS_SKUS.get(0) + "/" + "tokens/" + AppApplication.getPref(
                AppApplication.getInstance()!!
            )
                .getString("S_Token", "")
                .toString() + "?access_token=" + play_access_token
        Log.v("data", "URL Get publisher :::" + url)
        val getResponse = object : StringRequest(Request.Method.GET,
            url,
            Response.Listener<String> { response ->
                Log.v("data", "get 2 :::" + response)
                val mainObject = JSONObject(response)
                val expiryTimeMillis = mainObject.getString("expiryTimeMillis")
                val editor = AppApplication.getPref(AppApplication.getInstance()!!).edit()
                editor!!.putString("E_Time", expiryTimeMillis.toString())
                editor!!.apply()
                linearProgressIndicator.visibility = View.GONE
                try {
                    viewModel.subscribePlayStore(
                        this@WebActivity,
                        AppApplication.getPref(AppApplication.getInstance()!!).getString(
                            "email", ""
                        ).toString()
                    )
                    viewModel.subscribePlayStoreDataModel!!.observe(
                        this@WebActivity,
                        object : Observer<JsonObject?> {
                            override fun onChanged(response: JsonObject?) {
                                Log.v("Nisha", "Nisha" + response?.get("isSuccess").toString())
                                if (response?.get("isSuccess").toString().replace("\"", "")
                                        .equals("true")
                                ) {
                                    val editor =
                                        AppApplication.getPref(AppApplication.getInstance()!!)
                                            .edit()
                                    editor!!.putString("is_from_sub", "false")
                                    editor!!.apply()
                                    if (SplashActivity.LAST_SCREEN.equals("Detail")) {
                                        var mainIntent =
                                            Intent(this@WebActivity, DetailActivity::class.java)
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(mainIntent)
                                        finishAffinity()
                                    } else {
                                        var mainIntent =
                                            Intent(this@WebActivity, MainActivity::class.java)
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(mainIntent)
                                        finishAffinity()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@WebActivity,
                                        getString(R.string.api_error),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
                } catch (ex: Exception) {
                    Log.e("err", "api :::" + ex.message)
                }

            },
            Response.ErrorListener { e ->
                linearProgressIndicator.visibility = View.GONE
                Log.v("data", "get error 2 :::" + e.networkResponse.statusCode)
                e.printStackTrace();
                /* val i = Intent(Intent.ACTION_VIEW)
                 i.data = Uri.parse(apiServerUrl)
                 startActivity(i)*/
            }) {
        }
        queuee.add(getResponse)
    }

    //  handleIntent(intent)
    override fun onClick(v: View?) {
        when (v?.id) {
            (R.id.btn) -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(apiServerUrl)
                startActivity(i)
            }

        }
    }

}