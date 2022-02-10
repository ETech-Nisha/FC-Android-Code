package com.fractal.chaoss.util

import android.content.Context
import android.graphics.*
import android.os.StrictMode
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import com.fractal.chaoss.SplashActivity
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.MessageDigest
import java.util.*

class RoundedCornersTransformation @JvmOverloads constructor(
    private val mBitmapPool: BitmapPool, private val mRadius: Int, margin: Int,
    cornerType: CornerType = CornerType.ALL
) : Transformation<Bitmap?> {
    enum class CornerType {
        ALL, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT, OTHER_TOP_LEFT, OTHER_TOP_RIGHT, OTHER_BOTTOM_LEFT, OTHER_BOTTOM_RIGHT, DIAGONAL_FROM_TOP_LEFT, DIAGONAL_FROM_TOP_RIGHT, BORDER
    }

    private val mDiameter: Int
    private val mMargin: Int
    private val mCornerType: CornerType
    private var mColor: String? = null
    private var mBorder = 0

    constructor(
        context: Context?,
        radius: Int,
        margin: Int,
        color: String?,
        border: Int
    ) : this(context, radius, margin, CornerType.BORDER) {
        mColor = color
        mBorder = border
    }

    @JvmOverloads
    constructor(
        context: Context?, radius: Int, margin: Int,
        cornerType: CornerType = CornerType.ALL
    ) : this(Glide.get(context!!).bitmapPool, radius, margin, cornerType) {
    }

    override fun transform(
        context: Context,
        resource: Resource<Bitmap?>,
        outWidth: Int,
        outHeight: Int
    ): Resource<Bitmap?> {
        val source = resource.get()
        val width = source.width
        val height = source.height
        var bitmap: Bitmap? = mBitmapPool[width, height, Bitmap.Config.ARGB_8888]
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap!!)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        drawRoundRect(canvas, paint, width.toFloat(), height.toFloat())
        return BitmapResource.obtain(bitmap, mBitmapPool)!!
    }

    private fun drawRoundRect(
        canvas: Canvas,
        paint: Paint,
        width: Float,
        height: Float
    ) {
        val right = width - mMargin
        val bottom = height - mMargin
        when (mCornerType) {
            CornerType.ALL -> canvas.drawRoundRect(
                RectF(mMargin.toFloat(), mMargin.toFloat(), right, bottom),
                mRadius.toFloat(),
                mRadius.toFloat(),
                paint
            )
            CornerType.TOP_LEFT -> drawTopLeftRoundRect(canvas, paint, right, bottom)
            CornerType.TOP_RIGHT -> drawTopRightRoundRect(canvas, paint, right, bottom)
            CornerType.BOTTOM_LEFT -> drawBottomLeftRoundRect(canvas, paint, right, bottom)
            CornerType.BOTTOM_RIGHT -> drawBottomRightRoundRect(canvas, paint, right, bottom)
            CornerType.TOP -> drawTopRoundRect(canvas, paint, right, bottom)
            CornerType.BOTTOM -> drawBottomRoundRect(canvas, paint, right, bottom)
            CornerType.LEFT -> drawLeftRoundRect(canvas, paint, right, bottom)
            CornerType.RIGHT -> drawRightRoundRect(canvas, paint, right, bottom)
            CornerType.OTHER_TOP_LEFT -> drawOtherTopLeftRoundRect(canvas, paint, right, bottom)
            CornerType.OTHER_TOP_RIGHT -> drawOtherTopRightRoundRect(canvas, paint, right, bottom)
            CornerType.OTHER_BOTTOM_LEFT -> drawOtherBottomLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.OTHER_BOTTOM_RIGHT -> drawOtherBottomRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.DIAGONAL_FROM_TOP_LEFT -> drawDiagonalFromTopLeftRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.DIAGONAL_FROM_TOP_RIGHT -> drawDiagonalFromTopRightRoundRect(
                canvas,
                paint,
                right,
                bottom
            )
            CornerType.BORDER -> drawBorder(canvas, paint, right, bottom)
            else -> canvas.drawRoundRect(
                RectF(mMargin.toFloat(), mMargin.toFloat(), right, bottom),
                mRadius.toFloat(),
                mRadius.toFloat(),
                paint
            )
        }
    }

    private fun drawTopLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(
                mMargin.toFloat(),
                mMargin.toFloat(),
                (mMargin + mDiameter).toFloat(),
                (mMargin + mDiameter).toFloat()
            ),
            mRadius.toFloat(), mRadius.toFloat(), paint
        )
        canvas.drawRect(
            RectF(
                mMargin.toFloat(),
                (mMargin + mRadius).toFloat(),
                (mMargin + mRadius).toFloat(),
                bottom
            ), paint
        )
        canvas.drawRect(
            RectF((mMargin + mRadius).toFloat(), mMargin.toFloat(), right, bottom),
            paint
        )
    }

    private fun drawTopRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, mMargin.toFloat(), right, (mMargin + mDiameter).toFloat()),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(RectF(mMargin.toFloat(), mMargin.toFloat(), right - mRadius, bottom), paint)
        canvas.drawRect(RectF(right - mRadius, (mMargin + mRadius).toFloat(), right, bottom), paint)
    }

    private fun drawBottomLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), bottom - mDiameter, (mMargin + mDiameter).toFloat(), bottom),
            mRadius.toFloat(), mRadius.toFloat(), paint
        )
        canvas.drawRect(
            RectF(
                mMargin.toFloat(),
                mMargin.toFloat(),
                (mMargin + mDiameter).toFloat(),
                bottom - mRadius
            ), paint
        )
        canvas.drawRect(
            RectF((mMargin + mRadius).toFloat(), mMargin.toFloat(), right, bottom),
            paint
        )
    }

    private fun drawBottomRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius.toFloat(),
            mRadius.toFloat(), paint
        )
        canvas.drawRect(RectF(mMargin.toFloat(), mMargin.toFloat(), right - mRadius, bottom), paint)
        canvas.drawRect(RectF(right - mRadius, mMargin.toFloat(), right, bottom - mRadius), paint)
    }

    private fun drawTopRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), right, (mMargin + mDiameter).toFloat()),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(mMargin.toFloat(), (mMargin + mRadius).toFloat(), right, bottom),
            paint
        )
    }

    private fun drawBottomRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), bottom - mDiameter, right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(RectF(mMargin.toFloat(), mMargin.toFloat(), right, bottom - mRadius), paint)
    }

    private fun drawLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), (mMargin + mDiameter).toFloat(), bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF((mMargin + mRadius).toFloat(), mMargin.toFloat(), right, bottom),
            paint
        )
    }

    private fun drawRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, mMargin.toFloat(), right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(RectF(mMargin.toFloat(), mMargin.toFloat(), right - mRadius, bottom), paint)
    }

    private fun drawOtherTopLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), bottom - mDiameter, right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(right - mDiameter, mMargin.toFloat(), right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                mMargin.toFloat(),
                mMargin.toFloat(),
                right - mRadius,
                bottom - mRadius
            ), paint
        )
    }

    private fun drawOtherTopRightRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), (mMargin + mDiameter).toFloat(), bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), bottom - mDiameter, right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                (mMargin + mRadius).toFloat(),
                mMargin.toFloat(),
                right,
                bottom - mRadius
            ), paint
        )
    }

    private fun drawOtherBottomLeftRoundRect(
        canvas: Canvas,
        paint: Paint,
        right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), right, (mMargin + mDiameter).toFloat()),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(right - mDiameter, mMargin.toFloat(), right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                mMargin.toFloat(),
                (mMargin + mRadius).toFloat(),
                right - mRadius,
                bottom
            ), paint
        )
    }

    private fun drawOtherBottomRightRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), right, (mMargin + mDiameter).toFloat()),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), (mMargin + mDiameter).toFloat(), bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRect(
            RectF(
                (mMargin + mRadius).toFloat(),
                (mMargin + mRadius).toFloat(),
                right,
                bottom
            ), paint
        )
    }

    private fun drawDiagonalFromTopLeftRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(
                mMargin.toFloat(),
                mMargin.toFloat(),
                (mMargin + mDiameter).toFloat(),
                (mMargin + mDiameter).toFloat()
            ),
            mRadius.toFloat(), mRadius.toFloat(), paint
        )
        canvas.drawRoundRect(
            RectF(right - mDiameter, bottom - mDiameter, right, bottom), mRadius.toFloat(),
            mRadius.toFloat(), paint
        )
        canvas.drawRect(
            RectF(
                mMargin.toFloat(),
                (mMargin + mRadius).toFloat(),
                right - mDiameter,
                bottom
            ), paint
        )
        canvas.drawRect(
            RectF(
                (mMargin + mDiameter).toFloat(),
                mMargin.toFloat(),
                right,
                bottom - mRadius
            ), paint
        )
    }

    private fun drawDiagonalFromTopRightRoundRect(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {
        canvas.drawRoundRect(
            RectF(right - mDiameter, mMargin.toFloat(), right, (mMargin + mDiameter).toFloat()),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), bottom - mDiameter, (mMargin + mDiameter).toFloat(), bottom),
            mRadius.toFloat(), mRadius.toFloat(), paint
        )
        canvas.drawRect(
            RectF(
                mMargin.toFloat(),
                mMargin.toFloat(),
                right - mRadius,
                bottom - mRadius
            ), paint
        )
        canvas.drawRect(
            RectF(
                (mMargin + mRadius).toFloat(),
                (mMargin + mRadius).toFloat(),
                right,
                bottom
            ), paint
        )
    }

    private fun drawBorder(
        canvas: Canvas, paint: Paint, right: Float,
        bottom: Float
    ) {

        // stroke
        val strokePaint = Paint()
        strokePaint.style = Paint.Style.STROKE
        if (mColor != null) {
            strokePaint.color = Color.parseColor(mColor)
        } else {
            strokePaint.color = Color.BLACK
        }
        strokePaint.strokeWidth = mBorder.toFloat()
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            paint
        )

        // stroke
        canvas.drawRoundRect(
            RectF(mMargin.toFloat(), mMargin.toFloat(), right, bottom),
            mRadius.toFloat(),
            mRadius.toFloat(),
            strokePaint
        )
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}
    val id: String
        get() = ("RoundedTransformation(radius=" + mRadius + ", margin=" + mMargin + ", diameter="
                + mDiameter + ", cornerType=" + mCornerType.name + ")")

    companion object {
        fun getCode() :String{
            val client: HttpClient = DefaultHttpClient()
            val post = HttpPost("https://accounts.google.com/o/oauth2/v2/auth")
            try {
                val nameValuePairs: MutableList<NameValuePair> =
                    ArrayList(4)
                nameValuePairs.add(BasicNameValuePair("response_type", "code"))
                nameValuePairs.add(
                    BasicNameValuePair(
                        "client_id",
                        SplashActivity.GOOGLE_CLIENT_ID
                    )
                )
                nameValuePairs.add(
                    BasicNameValuePair(
                        "scope",
                        "https://www.googleapis.com/auth/androidpublisher"
                    )
                )
                nameValuePairs.add(
                    BasicNameValuePair(
                        "response_type",
                        "code"
                    )
                )
                nameValuePairs.add(
                    BasicNameValuePair(
                        "redirect_uri",
                        SplashActivity.GOOGLE_REDIRECT_URI
                    )
                )
                post.entity = UrlEncodedFormEntity(nameValuePairs)
                val response = client.execute(post)
                val reader = BufferedReader(
                    InputStreamReader(
                        response.entity.content
                    )
                )
                val buffer = StringBuffer()
                var line = reader.readLine()
                while (line != null) {
                    buffer.append(line)
                    line = reader.readLine()
                }
                Log.v("Nisha", "Buffer::$buffer")
                val json = JSONObject(buffer.toString())
            }catch (ex:java.lang.Exception){

            }
            return ""
        }


        fun getRefreshToken(purchaseToken: String?): Long? {
            val policy =
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            var expirey: Long? = 0L
            //var code =getCode()
            val client: HttpClient = DefaultHttpClient()
            val post = HttpPost("https://accounts.google.com/o/oauth2/token")
            try {
                val nameValuePairs: MutableList<NameValuePair> =
                    ArrayList(5)
                nameValuePairs.add(BasicNameValuePair("grant_type", "authorization_code"))
                nameValuePairs.add(
                    BasicNameValuePair(
                        "client_id",
                        SplashActivity.GOOGLE_CLIENT_ID
                    )
                )
                nameValuePairs.add(
                    BasicNameValuePair(
                        "client_secret",
                        SplashActivity.GOOGLE_CLIENT_SECRET
                    )
                )
                nameValuePairs.add(
                    BasicNameValuePair(
                        "code",
                        "4/0AX4XfWg_YKbz3qLrLjbN7e4ft_x_DM0yTD56Ji7dtsfB-UGxaZdQPSU-u-Biq9vG7AwPdQ"
                    )//"4/0AX4XfWg_YKbz3qLrLjbN7e4ft_x_DM0yTD56Ji7dtsfB-UGxaZdQPSU-u-Biq9vG7AwPdQ"
                )
                nameValuePairs.add(BasicNameValuePair("redirect_uri", SplashActivity.GOOGLE_REDIRECT_URI))
                nameValuePairs.add(BasicNameValuePair("access_type", "offline"))
                post.entity = UrlEncodedFormEntity(nameValuePairs)
                val response = client.execute(post)
                val reader = BufferedReader(
                    InputStreamReader(
                        response.entity.content
                    )
                )




                val buffer = StringBuffer()
                var line = reader.readLine()
                while (line != null) {
                    buffer.append(line)
                    line = reader.readLine()
                }
                Log.v("Nisha", "Buffer::$buffer")
                val json = JSONObject(buffer.toString())
                val refreshToken = json.getString("access_token")
                Log.v("Nisha", "access_token::$refreshToken")
                expirey = getAccessToken(
                    refreshToken,
                    purchaseToken!!
                )
                Log.v("Nisha", "expirey::$expirey")
                // return refreshToken;
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return expirey
        }

        private fun getAccessToken(
            refreshToken: String,
            purchaseToken: String
        ): Long? {
            var expirey: Long? = 0L
            val client: HttpClient = DefaultHttpClient()
            val post = HttpPost("https://accounts.google.com/o/oauth2/token")
            try {
                val nameValuePairs: MutableList<NameValuePair> =
                    ArrayList(4)
                nameValuePairs.add(BasicNameValuePair("grant_type", "refresh_token"))
                nameValuePairs.add(
                    BasicNameValuePair(
                        "client_id",
                        SplashActivity.GOOGLE_CLIENT_ID
                    )
                )
                nameValuePairs.add(
                    BasicNameValuePair(
                        "client_secret",
                        SplashActivity.GOOGLE_CLIENT_SECRET
                    )
                )
                nameValuePairs.add(BasicNameValuePair("refresh_token", refreshToken))
                post.entity = UrlEncodedFormEntity(nameValuePairs)
                val response = client.execute(post)
                val reader = BufferedReader(
                    InputStreamReader(
                        response.entity.content
                    )
                )
                val buffer = StringBuffer()
                var line = reader.readLine()
                while (line != null) {
                    buffer.append(line)
                    line = reader.readLine()
                }
                val json = JSONObject(buffer.toString())
                val accessToken = json.getString("access_token")
                expirey = getSubscriptionExpire(
                    refreshToken,
                    accessToken,
                    "product_002",
                    purchaseToken
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return expirey
        }

        private val HTTP_TRANSPORT: HttpTransport = NetHttpTransport()
        private val JSON_FACTORY: JsonFactory = JacksonFactory()
        private fun getSubscriptionExpire(
            accessToken: String,
            refreshToken: String,
            subscriptionId: String,
            purchaseToken: String
        ): Long? {
            try {
                val tokenResponse = TokenResponse()
                tokenResponse.accessToken = accessToken
                tokenResponse.refreshToken = refreshToken
                tokenResponse.expiresInSeconds = 3600L
                tokenResponse.scope = "https://www.googleapis.com/auth/androidpublisher"
                tokenResponse.tokenType = "Bearer"
                val credential: HttpRequestInitializer = GoogleCredential.Builder()
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(
                        SplashActivity.GOOGLE_CLIENT_ID,
                        SplashActivity.GOOGLE_CLIENT_SECRET
                    )
                    .build()
                    .setFromTokenResponse(tokenResponse)
                val publisher = AndroidPublisher.Builder(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    credential
                ).setApplicationName("product_002").build()
                val purchases = publisher.purchases()
                val get =
                    purchases.subscriptions()["com.fractal.chaos", subscriptionId, purchaseToken]
                val subscripcion = get.execute()
                return subscripcion.expiryTimeMillis
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }
    }

    init {
        mDiameter = mRadius * 2
        mMargin = margin
        mCornerType = cornerType
    }
}