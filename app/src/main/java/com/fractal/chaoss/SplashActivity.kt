package com.fractal.chaoss

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.fractal.chaoss.manager.home.PermissionAccept
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class SplashActivity : AppCompatActivity() {
    lateinit var videoView: ImageView
    companion object{
        public var PNToken:String=""
        public var LAST_SCREEN:String=""
        public var GOOGLE_CLIENT_ID =
            "588171352860-7q3uuh1sfhob79q3vu8cb39noi7kf753.apps.googleusercontent.com"
        public var GOOGLE_CLIENT_SECRET = "hjX8uscIp32J-D3vCgme6BFj"
        public var GOOGLE_REDIRECT_URI =
            "http://ec2-3-7-135-212.ap-south-1.compute.amazonaws.com/login"
        private const val REQUEST_CODE_WRITE_SETTINGS_PERMISSION = 5
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            && this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK < Configuration.SCREENLAYOUT_SIZE_LARGE
        ) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)


        
        getSupportActionBar()!!.hide()
        setContentView(R.layout.splash)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            //val token = task.result
            PNToken = task.result.toString()
            Log.v("Nisha", "Token::" + PNToken)
        })
        videoView = findViewById<ImageView>(R.id.videoView)
        /*if (canWriteSettings) {
            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0)

        } else {
            startManageWriteSettingsPermission()
        }*/
        getEmails()
    }
    private fun startManageWriteSettingsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:${this.packageName}")
            ).let {
                startActivityForResult(it, REQUEST_CODE_WRITE_SETTINGS_PERMISSION)
            }
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_WRITE_SETTINGS_PERMISSION -> {
                if (canWriteSettings) {
                    Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0)
                    getEmails()
                } else {
                    Toast.makeText(this, "Write settings permission is not granted!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/

 /*   val canWriteSettings: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.System.canWrite(this)
*/

    var gif:Int=0
    private fun getEmails() {
        if (resources.getBoolean(R.bool.isTablet)) {
            gif = R.drawable.tab
        }else{
            gif = R.drawable.phone
        }
       // gif = R.drawable.tab
        Glide.with(this).asGif().load(gif)
            .listener(object : RequestListener<GifDrawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: Target<GifDrawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is GifDrawable) {
                        resource.setLoopCount(1)
                    }

                    resource!!.registerAnimationCallback(object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            var mainIntent =
                                Intent(this@SplashActivity, PermissionAccept::class.java)
                            startActivity(mainIntent)
                            finishAffinity()
                        }
                    })
                    return false
                }
            }).into(videoView)
    }
}