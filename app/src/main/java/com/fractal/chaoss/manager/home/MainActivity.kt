package com.fractal.chaoss.manager.home


import android.accounts.AccountManager
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.*
import android.text.Html
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.AppApplication
import com.android.billingclient.api.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.fractal.chaoss.R
import com.fractal.chaoss.SplashActivity.Companion.LAST_SCREEN
import com.fractal.chaoss.apidata.ApiViewModel
import com.fractal.chaoss.manager.alldata.AllActivity
import com.fractal.chaoss.manager.detail.DetailActivity
import com.fractal.chaoss.manager.home.PermissionAccept.Companion.billingClient
import com.fractal.chaoss.util.Screen
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dmax.dialog.SpotsDialog
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var viewModel: ApiViewModel
    lateinit var img_curr: ImageView
    lateinit var img_upc_one: ImageView
    lateinit var img_upc_two: ImageView
    lateinit var img_pen_one: ImageView
    lateinit var img_pen_two: ImageView
    lateinit var img_pen_three: ImageView
    lateinit var txt_no_pen: TextView
    lateinit var txt_no_upc: TextView
    lateinit var txt_no_curr: TextView
    lateinit var rl_pe: RelativeLayout
    lateinit var rl_up: RelativeLayout
    lateinit var rl_curr: RelativeLayout
    lateinit var lv_pen_two: CardView
    lateinit var lv_pen_one: CardView
    lateinit var lv_up_two: CardView
    lateinit var lv_up_one: CardView
    lateinit var lv_up_three: CardView
    lateinit var lv_curr_one: CardView
    lateinit var selModel: JsonElement
    var isChanged: String = ""
    lateinit var progress: AlertDialog
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }else{
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setRequestedOrientation(requestedOrientation)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            && this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK < Configuration.SCREENLAYOUT_SIZE_LARGE
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {

        }
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()!!.hide()
        LAST_SCREEN = "Main"
        setContentView(R.layout.activity_main)
        progress = SpotsDialog.Builder().setContext(this).setCancelable(false)
            .setMessage(getString(R.string.login_txt))
            .build()
        rl_pe = findViewById(R.id.rl_pe) as RelativeLayout
        rl_up = findViewById(R.id.rl_up) as RelativeLayout
        rl_curr = findViewById(R.id.rl_curr) as RelativeLayout
        img_curr = findViewById(R.id.img_curr) as ImageView
        img_curr.setOnClickListener(this)
        img_upc_one = findViewById(R.id.img_upc_one) as ImageView
        img_upc_one.setOnClickListener(this)
        img_upc_two = findViewById(R.id.img_upc_two) as ImageView
        img_upc_two.setOnClickListener(this)
        img_pen_one = findViewById(R.id.img_pen_one) as ImageView
        img_pen_one.setOnClickListener(this)
        img_pen_two = findViewById(R.id.img_pen_two) as ImageView
        img_pen_two.setOnClickListener(this)
        img_pen_three = findViewById(R.id.img_pen_three) as ImageView
        img_pen_three.setOnClickListener(this)
        var all_pen = findViewById(R.id.all_pen) as TextView
        all_pen.setOnClickListener(this)
        var all_up = findViewById(R.id.all_up) as TextView
        all_up.setOnClickListener(this)
        var all_curr = findViewById(R.id.all_curr) as TextView
        all_curr.setOnClickListener(this)
        txt_no_pen = findViewById(R.id.txt_no_pen) as TextView
        txt_no_curr = findViewById(R.id.txt_no_curr) as TextView
        txt_no_upc = findViewById(R.id.txt_no_upc) as TextView
        lv_pen_two = findViewById(R.id.lv_pen_two) as CardView
        lv_pen_one = findViewById(R.id.lv_pen_one) as CardView
        lv_up_one = findViewById(R.id.lv_up_one) as CardView
        lv_up_two = findViewById(R.id.lv_up_two) as CardView
        lv_up_three = findViewById(R.id.lv_up_three) as CardView
        lv_curr_one = findViewById(R.id.lv_curr_one) as CardView

        var privacy = findViewById(R.id.privacy_txt) as TextView
        privacy.text = Html.fromHtml(getString(R.string.privacy))
        privacy.setMovementMethod(LinkMovementMethod.getInstance())
        privacy.removeLinksUnderline()
        privacy.setLinkTextColor(resources.getColor(R.color.text))

        txt_no_pen.visibility = View.VISIBLE
        txt_no_curr.visibility = View.VISIBLE
        txt_no_upc.visibility = View.VISIBLE

        viewModel = ViewModelProviders.of(this).get(ApiViewModel::class.java)


        validateSubscription()
    }

   // https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher&response_type=code&access_type=offline&redirect_uri=https://ec2-3-7-135-212.ap-south-1.compute.amazonaws.com/redirectGoogleoath2Result.php&client_id=868722952990-28c1j8p1m833jrn8j02lse5lnvjrdujn.apps.googleusercontent.com

    private fun handleSignInResult(completedTask: GoogleSignInAccount) {
        try {
            //  val account = completedTask.getResult(ApiException::class.java)
            Log.v("SignUp", "sdasdasd" + completedTask!!.serverAuthCode)
            Log.v("SignUp", "sdasdasd" + completedTask!!.idToken)
        } catch (e: ApiException) {
            Log.w("SignUp", "signInResult:failed code=" + e.getStatusCode())

        }
    }

    private fun getWallpaperData() {
        this@MainActivity.runOnUiThread(Runnable {
            viewModel.getHomeList(this@MainActivity)
            progress = SpotsDialog.Builder().setContext(this).setCancelable(false)
                .setMessage(getString(R.string.login_txt))
                .build()
            progress!!.show()
            try {
                viewModel.homeRepostary!!.observe(
                    this@MainActivity,
                    object : Observer<JsonObject?> {
                        override fun onChanged(response: JsonObject?) {
                            progress!!.dismiss()
                            if (response?.get("isSuccess").toString().contains("false")) {
                                Toast.makeText(
                                    this@MainActivity,
                                    response?.get("errorMessage").toString().replace("\"", ""),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (response?.get("data") != null) {
                                    isChanged =
                                        response?.getAsJsonObject("data")!!
                                            .get("userCanChangeWallpaper")
                                            .toString()
                                    scaleImages(response?.getAsJsonObject("data")!!)
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        getString(R.string.api_error),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
            } catch (ex: java.lang.Exception) {
                Log.v("getWallpaperData", "Api error:::" + ex.message)
            }
        })
    }



    var isPurchased = false
    fun validateSubscription() {
        checkSub()
    }


    override fun onStop() {
        super.onStop()
        Log.v("Nisha MainActivity", "onStop:::")
    }

    override fun onRestart() {
        super.onRestart()
        Log.v("Nisha MainActivity", "onRestart:::")
        validateSubscription()
    }


    fun checkSub() {
        Log.v("Nisha", "checkSub:::")
        val purchasesResult = HashSet<Purchase>()
        purchasesResult.clear()

        var result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.apply { purchasesResult.addAll(this) }

        if (purchasesResult.size > 0) {
            isPurchased = true
        } else {
            isPurchased = false
        }
        Log.v("Nisha", "isPurchased:::" + isPurchased)
        if (isPurchased) {
            getWallpaperData()
        } else {
            this@MainActivity.runOnUiThread(Runnable {
                try {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Alert")
                        .setCancelable(false)
                        .setMessage("Your Subscription has been expired , Please renew your account.")
                        .setPositiveButton(
                            "OK"
                        ) { dialog, which ->
                            var mainIntent =
                                Intent(this@MainActivity, PermissionAccept::class.java)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(mainIntent)
                            finishAffinity()
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity, "..." + e.message,
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            })
        }
    }

    private fun validateSub() {
        /*viewModel.validateSubscription(
            this@MainActivity, AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "email", ""
            ).toString()
        )
        progress!!.show()
        viewModel.validateSubscriptionModelDataModel!!.observe(
            this@MainActivity,
            object : Observer<ValidateSubscriptionModel?> {
                override fun onChanged(response: ValidateSubscriptionModel?) {
                    progress!!.dismiss()
                    if (response?.isSuccess.toString().contains("false")) {
                        Toast.makeText(
                            this@MainActivity,
                            response?.errorMessage.toString().replace("\"", ""),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (response?.data != null) {
                            if (System.currentTimeMillis() > parseDate(response?.data!!.subscriptionEndDate)) {
                                if (System.currentTimeMillis() > calulateExpireDate(response?.data!!.subscriptionStartDate)) {
                                    var mainIntent =
                                        Intent(this@MainActivity, PermissionAccept::class.java)
                                    startActivity(mainIntent)
                                    finishAffinity()
                                } else {
                                    getWallpaperData()
                                }
                            } else {
                                getWallpaperData()
                            }
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                getString(R.string.api_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })*/
    }

    fun calulateExpireDate(subscriptionStartDate: String): Long {
        var time = AppApplication.getPref(AppApplication.getInstance()!!).getString(
            "S_Time", ""
        ).toString()
        var period = AppApplication.getPref(AppApplication.getInstance()!!).getString(
            "S_Period", ""
        ).toString()
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
        }
        //}

        Log.v("Nisha", "Date:::" + calendar.time)  //Mon Aug 16 11:09:39 GMT+05:30 2021
        var outputDateStr = parseEDate(calendar.time.toString())
        Log.v("Nisha", "Date new" + outputDateStr)
        return outputDateStr!!
    }


    fun parseDate(valu: String): Long {
        val timeInMilliseconds: Long = 0
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        try {
            val mDate: Date = sdf.parse(valu)
            val timeInMilliseconds = mDate.time
            println("Date in milli :: $timeInMilliseconds")
            return timeInMilliseconds
        } catch (e: ParseException) {
            e.printStackTrace()
            return 0
        }
    }

    fun parseEDate(
        inputDateString: String?
    ): Long? {
        var date: Date? = null
        var outputDateString: String? = null
        try {
            date = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDateString)
            outputDateString = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return parseDate(outputDateString!!)
    }

    // var callbackManager: CallbackManager? = null
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val result: GoogleSignInResult? =
                Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            val statusCode: Int = result!!.getStatus().getStatusCode()
            if (resultCode == RESULT_OK) {
                GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnSuccessListener { account ->
                        handleSignInResult(account)
                    }
                    .addOnFailureListener { e ->
                        Log.d("Fail", "Fail")
                    }
            }
        }
    }

    fun TextView.removeLinksUnderline() {
        val spannable = SpannableString(text)
        for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
            spannable.setSpan(object : URLSpan(u.url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
        }
        text = spannable
    }

    fun scaleImages(result: JsonObject) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            handler.post {
                if (resources.getBoolean(R.bool.isTablet)) {
                    scale16by9()
                } else {
                    scale9by16()
                }
                //scale16by9()
                try {
                    if ((result!!.getAsJsonArray("current")).size() > 0) {
                        try {
                            img_curr.visibility = View.VISIBLE
                            txt_no_curr.visibility = View.GONE
                            Glide.with(this@MainActivity)
                                .load(
                                    ((result!!.getAsJsonArray("current")).get(0) as JsonObject).get(
                                        "url"
                                    )
                                        .getAsString()
                                )
                                .override(width.roundToInt(), height.roundToInt())
                                .into(img_curr)
                            img_curr.setTag(
                                R.id.img_curr,
                                (result!!.getAsJsonArray("current")).get(0)
                            )
                        } catch (ex: Exception) {
                            txt_no_curr.visibility = View.VISIBLE
                            lv_curr_one.visibility = View.GONE
                        }
                    } else {
                        txt_no_curr.visibility = View.VISIBLE
                        lv_curr_one.visibility = View.GONE
                    }
                }catch (ex: Exception)
                {

                }
                if ((result!!.getAsJsonArray("upcoming")).size() > 0) {
                    txt_no_upc.visibility = View.GONE
                    if ((result!!.getAsJsonArray("upcoming")).size() > 1) {
                        if((result!!.getAsJsonArray("upcoming")).size()==3) {
                            lv_up_three.visibility = View.VISIBLE
                            img_pen_three.visibility = View.VISIBLE
                            lv_up_two.visibility = View.VISIBLE
                            img_upc_two.visibility = View.VISIBLE
                            img_upc_one.visibility = View.VISIBLE
                            lv_up_one.visibility = View.VISIBLE
                        }
                        if((result!!.getAsJsonArray("upcoming")).size()==2) {
                            lv_up_two.visibility = View.VISIBLE
                            img_upc_two.visibility = View.VISIBLE
                            img_upc_one.visibility = View.VISIBLE
                            lv_up_one.visibility = View.VISIBLE
                            lv_up_three.visibility = View.GONE
                            img_pen_three.visibility = View.GONE
                        }
                        Glide.with(this@MainActivity)
                            .load(
                                ((result!!.getAsJsonArray("upcoming")).get(1) as JsonObject).get(
                                    "url"
                                )
                                    .getAsString()
                            )
                            .override(width.roundToInt(), height.roundToInt())
                            .into(img_upc_two)
                        img_upc_two.setTag(
                            R.id.img_upc_two,
                            (result!!.getAsJsonArray("upcoming")).get(1)
                        )
                        try {
                            Glide.with(this@MainActivity)
                                .load(
                                    ((result!!.getAsJsonArray("upcoming")).get(2) as JsonObject).get(
                                        "url"
                                    )
                                        .getAsString()
                                )
                                .override(width.roundToInt(), height.roundToInt())
                                .into(img_pen_three)


                            img_pen_three.setTag(
                                R.id.img_pen_three,
                                (result!!.getAsJsonArray("upcoming")).get(2)
                            )
                        } catch (ex: Exception) {

                        }
                    }
                    if((result!!.getAsJsonArray("upcoming")).size()==1) {
                        img_upc_one.visibility = View.VISIBLE
                        lv_up_one.visibility = View.VISIBLE
                        lv_up_two.visibility = View.GONE
                        img_upc_two.visibility = View.GONE
                        lv_up_three.visibility = View.GONE
                        img_pen_three.visibility = View.GONE
                    }
                    Glide.with(this@MainActivity)
                        .load(
                            ((result!!.getAsJsonArray("upcoming")).get(0) as JsonObject).get(
                                "url"
                            )
                                .getAsString()
                        )
                        .override(width.roundToInt(), height.roundToInt())
                        .into(img_upc_one)
                    img_upc_one.setTag(
                        R.id.img_upc_one,
                        (result!!.getAsJsonArray("upcoming")).get(0)
                    )
                } else {
                    txt_no_upc.visibility = View.VISIBLE
                    lv_up_two.visibility = View.GONE
                    lv_up_one.visibility = View.GONE
                    lv_up_three.visibility = View.GONE
                }
                if ((result!!.getAsJsonArray("past")).size() > 0) {
                    txt_no_pen.visibility = View.GONE
                    Glide.with(this@MainActivity)
                        .load(
                            ((result!!.getAsJsonArray("past")).get(0) as JsonObject).get("url")
                                .getAsString()
                        )
                        .override(width.roundToInt(), height.roundToInt())
                        .into(img_pen_one)

                    img_pen_one.setTag(
                        R.id.img_pen_one,
                        (result!!.getAsJsonArray("past")).get(0)
                    )
                    img_pen_one.visibility = View.VISIBLE
                    if ((result!!.getAsJsonArray("past")).size() > 1) {
                        img_pen_two.visibility = View.VISIBLE
                        Glide.with(this@MainActivity)
                            .load(
                                ((result!!.getAsJsonArray("past")).get(1) as JsonObject).get(
                                    "url"
                                )
                                    .getAsString()
                            )
                            .override(width.roundToInt(), height.roundToInt())
                            .into(img_pen_two)
                        img_pen_two.setTag(
                            R.id.img_pen_two,
                            (result!!.getAsJsonArray("past")).get(1)
                        )
                    }
                } else {
                    lv_pen_one.visibility = View.GONE
                    lv_pen_two.visibility = View.GONE
                    txt_no_pen.visibility = View.VISIBLE
                }
            }
        }
    }

    var width: Float = 0.0f
    var height: Float = 0.0f

    private fun scale9by16() {
        val screen = Screen(this@MainActivity) // Setting Screen
        width = screen.getWidth() / 3
        var ht = (width / 9)
        height = (ht * 16)
        Log.v("adasd", "width" + (width - 60))
        Log.v("adsa", "height" + height)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams((width.roundToInt() - 60), height.roundToInt())
        layoutParams.rightMargin = 20
        val layoutParamsOne: FrameLayout.LayoutParams =
            FrameLayout.LayoutParams((width.roundToInt() - 60), height.roundToInt())
        layoutParamsOne.rightMargin = 20
        img_pen_one.setLayoutParams(layoutParamsOne)
        img_pen_two.setLayoutParams(layoutParamsOne)
        img_pen_three.setLayoutParams(layoutParamsOne)
        img_upc_one.setLayoutParams(layoutParamsOne)
        img_upc_two.setLayoutParams(layoutParamsOne)
        img_curr.setLayoutParams(layoutParamsOne)

        lv_pen_two.setLayoutParams(layoutParams)
        lv_pen_one.setLayoutParams(layoutParams)
        lv_up_two.setLayoutParams(layoutParams)
        lv_up_one.setLayoutParams(layoutParams)
        lv_up_three.setLayoutParams(layoutParams)
        lv_curr_one.setLayoutParams(layoutParams)

    }

    private fun scale16by9() {
        val screen = Screen(this@MainActivity) // Setting Screen
        width = screen.getWidth() / 3
        var ht = (width / 16)
        height = (ht * 9)
        Log.v("adasd", "width" + (width - 60))
        Log.v("adsa", "height" + height)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams((width.roundToInt() - 60), height.roundToInt())
        layoutParams.rightMargin = 20
        val layoutParamsOne: FrameLayout.LayoutParams =
            FrameLayout.LayoutParams((width.roundToInt() - 60), height.roundToInt())
        layoutParamsOne.rightMargin = 20
        img_pen_one.setLayoutParams(layoutParamsOne)
        img_pen_two.setLayoutParams(layoutParamsOne)
        img_pen_three.setLayoutParams(layoutParamsOne)
        img_upc_one.setLayoutParams(layoutParamsOne)
        img_upc_two.setLayoutParams(layoutParamsOne)
        img_curr.setLayoutParams(layoutParamsOne)

        lv_pen_two.setLayoutParams(layoutParams)
        lv_pen_one.setLayoutParams(layoutParams)
        lv_up_two.setLayoutParams(layoutParams)
        lv_up_one.setLayoutParams(layoutParams)
        lv_up_three.setLayoutParams(layoutParams)
        lv_curr_one.setLayoutParams(layoutParams)

    }


    private fun scale(bitmap: Bitmap): Bitmap {
        val screen = Screen(this@MainActivity) // Setting Screen
        val width: Float
        val height: Float
        width = screen.getWidth() / 3
        height = width / 2
        val scaledBitmap =
            Bitmap.createScaledBitmap(bitmap, width.roundToInt(), height.roundToInt(), true)
        return scaledBitmap
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_curr -> {
                selModel = img_curr.getTag(R.id.img_curr) as JsonElement
                var mainIntent = Intent(this@MainActivity, DetailActivity::class.java)
                /*  val bundle = Bundle()
                  bundle.putSerializable("url", (img_curr.getTag(R.id.img_curr) as JsonElement).toString())
                  bundle.putSerializable("isChanged", isChanged)*/
                mainIntent.putExtra("url", selModel.toString())
                mainIntent.putExtra("myBundle", selModel.toString())
                mainIntent.putExtra("isChanged", isChanged)
                /* var model = JSONObject(intent.getStringExtra("myBundle"))
                 val editor = AppApplication.getPref(AppApplication.getInstance()!!).edit()
                 editor!!.putString("preview_data", Gson().toJson(bundle))
                 editor!!.apply()*/
                startActivity(mainIntent)
                //finishAffinity()
            }
            R.id.img_upc_one -> {
                selModel = img_upc_one.getTag(R.id.img_upc_one) as JsonElement
                var mainIntent = Intent(this@MainActivity, DetailActivity::class.java)
                /* val bundle = Bundle()
                 bundle.putSerializable("url", selModel.toString())*/
                mainIntent.putExtra("url", selModel.toString())
                mainIntent.putExtra("myBundle", selModel.toString())
                mainIntent.putExtra("isChanged", isChanged)
                startActivity(mainIntent)
                // finishAffinity()
            }
            R.id.img_upc_two -> {
                selModel = img_upc_two.getTag(R.id.img_upc_two) as JsonElement
                var mainIntent = Intent(this@MainActivity, DetailActivity::class.java)
                /*val bundle = Bundle()
                bundle.putSerializable("url", selModel.toString())*/
                mainIntent.putExtra("url", selModel.toString())
                mainIntent.putExtra("myBundle", selModel.toString())
                mainIntent.putExtra("isChanged", isChanged)
                startActivity(mainIntent)
                // finishAffinity()
            }
            R.id.img_pen_one -> {
                selModel = img_pen_one.getTag(R.id.img_pen_one) as JsonElement
                var mainIntent = Intent(this@MainActivity, DetailActivity::class.java)
/*                val bundle = Bundle()
                bundle.putSerializable("url", selModel.toString())*/
                mainIntent.putExtra("url", selModel.toString())
                mainIntent.putExtra("myBundle", selModel.toString())
                mainIntent.putExtra("isChanged", isChanged)
                startActivity(mainIntent)
                // finishAffinity()
            }
            R.id.img_pen_two -> {
                selModel = img_pen_two.getTag(R.id.img_pen_two) as JsonElement
                var mainIntent = Intent(this@MainActivity, DetailActivity::class.java)
                /* val bundle = Bundle()
                 bundle.putSerializable("url", selModel.toString())*/
                mainIntent.putExtra("url", selModel.toString())
                mainIntent.putExtra("myBundle", selModel.toString())
                mainIntent.putExtra("isChanged", isChanged)
                startActivity(mainIntent)
                // finishAffinity()
            }
            R.id.img_pen_three -> {
                selModel = img_pen_three.getTag(R.id.img_pen_three) as JsonElement
                var mainIntent = Intent(this@MainActivity, DetailActivity::class.java)
                /*val bundle = Bundle()
                bundle.putSerializable("url", selModel.toString())*/
                mainIntent.putExtra("url", selModel.toString())
                mainIntent.putExtra("myBundle", selModel.toString())
                mainIntent.putExtra("isChanged", isChanged)
                startActivity(mainIntent)
                // finishAffinity()
            }
            R.id.all_pen -> {
                var mainIntent = Intent(this@MainActivity, AllActivity::class.java)
                startActivity(mainIntent)
                // finishAffinity()
            }
            R.id.all_up -> {
                var mainIntent = Intent(this@MainActivity, AllActivity::class.java)
                startActivity(mainIntent)
                //finishAffinity()
            }
            R.id.all_curr -> {
                var mainIntent = Intent(this@MainActivity, AllActivity::class.java)
                startActivity(mainIntent)
                // finishAffinity()
            }
        }
    }
}

/*fun calculateInSampleSize(
    options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    return inSampleSize
}*/


/*val authorizeUrl: HttpUrl =
    "https://accounts.google.com/o/oauth2/v2/auth".toHttpUrlOrNull()!!
        .newBuilder()
        .addQueryParameter("client_id", GOOGLE_CLIENT_ID)
        .addQueryParameter(
            "scope",
            "https://www.googleapis.com/auth/androidpublisher"
        )
        .addQueryParameter("redirect_uri", GOOGLE_REDIRECT_URI)
        .addQueryParameter("response_type", "code")
        .build()*/

// Load the webpage

/*val gsoBuilder =
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
this@MainActivity?.let {
    GoogleSignIn.getClient(it, gsoBuilder.build())?.signOut()
}

val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken("328010034058-dprjc09jka78sdq9cs143rp1qg8v4v34.apps.googleusercontent.com")
    .requestEmail()
    .build()



var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
startActivityForResult(mGoogleSignInClient.signInIntent, 1)*/


/*    mGoogleSignInClient.silentSignIn()
        .addOnCompleteListener(this, OnCompleteListener<GoogleSignInAccount> { task ->
            handleSignInResult(task)
        })*/

//RetrieveFeedTask().execute("")

/*val am: AccountManager = AccountManager.get(this)
val options = Bundle()

var accounts = am.getAccountsByType("com.google")
am.getAuthToken(
    accounts.get(0),                     // Account retrieved using getAccountsByType()
    "Manage your tasks",            // Auth scope
    options,                        // Authenticator-specific options
    this,                           // Your activity
    OnTokenAcquired(),              // Callback called when a token is successfully acquired
    Handler(OnError())              // Callback called if an error occurs
)*/

/*   private fun startConnection() {
       billingClient =
           BillingClient.newBuilder(this).setListener(this@MainActivity).enablePendingPurchases()
               .build()
       billingClient!!.startConnection(object : BillingClientStateListener {
           override fun onBillingSetupFinished(billingResult: BillingResult) {
               if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                   //planSelected = resources.getString(R.string.monthly_product)
                   //planType = "monthly"
                   purchaseProduct(resources.getString(R.string.monthly_product))

               }
           }

           override fun onBillingServiceDisconnected() {}
       })
   }*/

