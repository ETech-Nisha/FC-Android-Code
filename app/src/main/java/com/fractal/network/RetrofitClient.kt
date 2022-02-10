package com.app.myapplication

import com.fractal.network.UnsafeOkHttpClient
import com.vision2020.network.ApiInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.http.conn.ssl.AbstractVerifier.getDNSSubjectAlts
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern.matches
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLException


object RetrofitClient {
    private val CACHE_CONTROL = "Cache-Control"
    private var retrofit: Retrofit? = null
    private var apiInterface: ApiInterface? = null

    val instance: ApiInterface?
        get() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("http://ec2-3-7-135-212.ap-south-1.compute.amazonaws.com/api/")
                    //.client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            if (apiInterface == null) {
                apiInterface = retrofit!!.create(ApiInterface::class.java)
            }
            return apiInterface
        }


    val instanceGmail: ApiInterface?
        get() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(logging)
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("http://ec2-3-7-135-212.ap-south-1.compute.amazonaws.com/api/")
                    //.client(UnsafeOkHttpClient.getUnsafeOkHttpClient())
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            if (apiInterface == null) {
                apiInterface = retrofit!!.create(ApiInterface::class.java)
            }
            return apiInterface
        }



    //Creating OKHttpClient
    private fun provideOkHttpClient(): OkHttpClient {
        var ok=UnsafeOkHttpClient.getUnsafeOkHttpClient();
        var builder = OkHttpClient.Builder()
        builder.hostnameVerifier(varifier)
        return builder
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    var varifier = HostnameVerifier { hostname, session ->
        val certs: Array<Certificate>
        certs = try {
            session.peerCertificates
        } catch (e: SSLException) {
            return@HostnameVerifier false
        }
        val x509 =
            certs[0] as X509Certificate
        // We can be case-insensitive when comparing the host we used to
        // establish the socket to the hostname in the certificate.
        val hostName = hostname.trim { it <= ' ' }.toLowerCase(Locale.ENGLISH)
        // Verify the first CN provided. Other CNs are ignored. Firefox, wget,
        // curl, and Sun Java work this way.
        val firstCn: String? = this!!.getFirstCn(x509)
        if (matches(hostName, firstCn)) {
            return@HostnameVerifier true
        }
        for (cn in getDNSSubjectAlts(x509)) {
            if (matches(hostName, cn)) {
                return@HostnameVerifier true
            }
        }
        false
    }
    private fun getFirstCn(cert: X509Certificate): String? {
        val subjectPrincipal = cert.subjectX500Principal.toString()
        for (token in subjectPrincipal.split(",".toRegex()).toTypedArray()) {
            val x = token.indexOf("CN=")
            if (x >= 0) {
                return token.substring(x + 3)
            }
        }
        return ""
    }

}
