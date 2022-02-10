package com.fractal.chaoss.manager.detail

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.AppApplication
import com.android.billingclient.api.*
import com.bumptech.glide.Glide
import com.fractal.chaoss.R
import com.fractal.chaoss.SplashActivity
import com.fractal.chaoss.apidata.ApiViewModel
import com.fractal.chaoss.manager.detail.model.SelectWallaperModel
import com.fractal.chaoss.manager.home.MainActivity
import com.fractal.chaoss.manager.home.PermissionAccept
import com.fractal.chaoss.manager.home.PermissionAccept.Companion.billingClient
import dmax.dialog.SpotsDialog
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*


class DetailActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var model: JSONObject
    lateinit var viewModel: ApiViewModel
    lateinit var progress: AlertDialog
    lateinit var context:Context
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
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
       // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        getSupportActionBar()!!.hide()
        setContentView(R.layout.detail)
        SplashActivity.LAST_SCREEN ="Detail"
        ((findViewById(R.id.back)) as RelativeLayout).setOnClickListener(this)
        ((findViewById(R.id.set)) as RelativeLayout).setOnClickListener(this)
        ((findViewById(R.id.download)) as RelativeLayout).setOnClickListener(this)
        context=this
        progress = SpotsDialog.Builder().setContext(this).setCancelable(false)
            .setMessage(getString(R.string.login_txt))
            .build()
        var img = (findViewById(R.id.img)) as ImageView
        viewModel = ViewModelProviders.of(this).get(ApiViewModel::class.java)
        progress = SpotsDialog.Builder().setContext(this).setCancelable(false)
            .setMessage(getString(R.string.login_txt))
            .build()
        validateSubscription(
            AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "email", ""
            ).toString()
        )
       // var myStoredVal = AppApplication.getPref(AppApplication.getInstance()!!).getString("preview_data", "").toString()
        Log.v("Details::", "asdads::" + intent.getStringExtra("url").toString())
        model=  JSONObject(intent.getStringExtra("url").toString())
       if (intent != null) {
            val isChanged = intent.getStringExtra("isChanged")
            if (isChanged!!.contains("No")) {
                ((findViewById(R.id.set)) as RelativeLayout).visibility = View.GONE
                ((findViewById(R.id.download)) as RelativeLayout).visibility = View.GONE
            } else {
                ((findViewById(R.id.set)) as RelativeLayout).visibility = View.VISIBLE
                ((findViewById(R.id.download)) as RelativeLayout).visibility = View.VISIBLE
            }

            /*  ((findViewById(R.id.set)) as RelativeLayout).visibility = View.VISIBLE
              ((findViewById(R.id.download)) as RelativeLayout).visibility = View.VISIBLE*/

            Glide.with(this@DetailActivity)
                .load(model.getString("url"))
                .into(img)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.v("Nisha Detail", "onStop:::")
    }
    override fun onRestart() {
        super.onRestart()
        Log.v("Nisha Detail", "onRestart:::")
        validateSubscription(
            AppApplication.getPref(AppApplication.getInstance()!!).getString(
                "email", ""
            ).toString()
        )
    }
    var isPurchased = false
    fun validateSubscription(email: String) {
        checkSub()
    }

    fun checkSub() {
        val purchasesResult = HashSet<Purchase>()
        var result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.apply { purchasesResult.addAll(this) }

        result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.apply { purchasesResult.addAll(this) }

        if (purchasesResult.size > 0) {
            isPurchased = true
        } else {
            isPurchased = false
        }
        Log.v("Nisha", "isPurchased:::" + isPurchased)
        if (isPurchased) {
        } else {
            this@DetailActivity.runOnUiThread(Runnable {
                try {
                    AlertDialog.Builder(this@DetailActivity)
                        .setTitle("Alert")
                        .setCancelable(false)
                        .setMessage("Your Subscription has been expired , Please renew your account.")
                        .setPositiveButton(
                            "OK"
                        ) { dialog, which ->
                            var mainIntent =
                                Intent(this@DetailActivity, PermissionAccept::class.java)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(mainIntent)
                            finishAffinity()
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@DetailActivity, "..." + e.message,
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            })
        }
    }

    override fun onBackPressed() {
        var mainIntent = Intent(this@DetailActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finishAffinity()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                onBackPressed()
            }
            R.id.set -> {
                setWallaper("wallpaper")
            }
            R.id.download -> {
                val permissionArrays = arrayOf<String>(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissionArrays, 1)
                } else {
                    downloadImageNew(model.getString("url"))
                }
            }
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
                    downloadImageNew(model.getString("url"))
                } else {
                    Toast.makeText(
                        this@DetailActivity,
                        "Please accept Permission",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    }

    private fun setWallaper(type: String) {
        progress!!.show()
        viewModel.selectWallapper(this@DetailActivity, model.getString("wallpaperId"))
        viewModel.selectRepostaryModel!!.observe(
            this,
            object : Observer<SelectWallaperModel?> {
                override fun onChanged(response: SelectWallaperModel?) {
                    progress!!.dismiss()
                    if (response?.data != null) {
                        if (type.equals("wallpaper")) {
                            LongOperation(context,model).execute("")
                            ((findViewById(R.id.set)) as RelativeLayout).visibility = View.GONE
                            ((findViewById(R.id.download)) as RelativeLayout).visibility = View.GONE
                        } else {
                            Toast.makeText(
                                this@DetailActivity,
                                "Download Complete...",
                                Toast.LENGTH_SHORT
                            ).show()
                            ((findViewById(R.id.set)) as RelativeLayout).visibility = View.GONE
                            ((findViewById(R.id.download)) as RelativeLayout).visibility = View.GONE
                        }
                    } else {
                        Toast.makeText(
                            this@DetailActivity,
                            getString(R.string.api_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }
    class LongOperation(val context: Context  , val model: JSONObject ) : AsyncTask<String, Void, String>() {
         override fun doInBackground(vararg params: String): String {
             val policy =
                 StrictMode.ThreadPolicy.Builder().permitAll().build()
             StrictMode.setThreadPolicy(policy)

             val wpm = WallpaperManager.getInstance(context)
             var ins: InputStream? = null
             try {
                 ins =
                     URL(model.getString("url")).openStream()
                 wpm.setStream(ins)
             } catch (e: IOException) {
                 e.printStackTrace()
             }
            return "Executed"
        }
        override fun onPostExecute(result: String) {
            Toast.makeText(
                context,
                "Wallpaper applied.",
                Toast.LENGTH_SHORT
            ).show()

        }
        override fun onPreExecute() {}
        override fun onProgressUpdate(vararg values: Void) {}
    }

    private fun downloadImageNew(downloadUrlOfImage: String) {
        try {
            var fileName = downloadUrlOfImage.substring(
                downloadUrlOfImage.lastIndexOf('/') + 1,
                downloadUrlOfImage.length
            )
            val dm =
                getSystemService(Context.DOWNLOAD_SERVICE ) as DownloadManager
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_PICTURES,
                    File.separator.toString() + fileName
                )
            dm.enqueue(request)
            registerReceiver(
                downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )

        } catch (e: Exception) {
            Toast.makeText(this, "Image download failed.", Toast.LENGTH_SHORT).show()
        }
    }

    var downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                setWallaper("down")
            }
        }
    }

}

