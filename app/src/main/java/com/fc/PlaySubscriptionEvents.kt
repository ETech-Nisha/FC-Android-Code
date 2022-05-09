package com.fc

import com.android.volley.VolleyError
import org.json.JSONObject


open class PlaySubscriptionEvents {

    /*companion object {
        const val TAG = "WebActivity"
        const val GOOGLE_CLIENT_ID =
            "868722952990-28c1j8p1m833jrn8j02lse5lnvjrdujn.apps.googleusercontent.com"

        const val GOOGLE_CLIENT_SECRET = "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"
        const val GOOGLE_REDIRECT_URI =
            "https://effectualtech.net/redirectGoogleoath2Result.php"
    }
    var apiServerUrl =
        "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/androidpublisher" +
                "&response_type=code&prompt=consent&access_type=offline&" +
                "redirect_uri=" + GOOGLE_REDIRECT_URI + "&client_id=" + GOOGLE_CLIENT_ID

    public fun getAuthCode(authcode: String, context: Activity,iResultResponse: IResult) {
        val queue = Volley.newRequestQueue(context)
        val postRequest = object : StringRequest(
            Request.Method.POST,
            "https://accounts.google.com/o/oauth2/token",
            Response.Listener<String> { response ->
                Log.v("data", "get 1 :::" + response)
                val mainObject = JSONObject(response)
                val refresh_token = mainObject.getString("refresh_token")
                getAppRefreshToken(refresh_token, context,iResultResponse)
            },
            Response.ErrorListener { e ->
                Log.v("data", "get error 1 :::" + e.message)
                Log.v("data", "get error 1 :::" + e.printStackTrace())
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("grant_type", "authorization_code")
                params.put(
                    "client_id",
                    "868722952990-28c1j8p1m833jrn8j02lse5lnvjrdujn.apps.googleusercontent.com"
                )
                params.put("client_secret", "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")
                params.put("code", authcode)
                params.put(
                    "redirect_uri",
                    GOOGLE_REDIRECT_URI
                )
                return params
            }
        }
        queue.add(postRequest)
    }

    private fun getAppRefreshToken(refresh_token: String, context: Activity,iResultResponse:IResult) {
        val queuee = Volley.newRequestQueue(context)

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
                getAppExpiryDetail(context,iResultResponse)
            },
            Response.ErrorListener { e ->
                Log.v("data", "get error 1 :::" + e.message)
                Log.v("data", "get error 1 :::" + e.printStackTrace())
                e.printStackTrace()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("grant_type", "refresh_token");
                params.put(
                    "client_id",
                    GOOGLE_CLIENT_ID
                )
                params.put("client_secret", GOOGLE_CLIENT_SECRET)
                params.put("refresh_token", refresh_token);
                return params
            }
        }
        queuee.add(postRequest)

    }*/

    /* public fun getAppExpiryDetail(
         context: Activity,
         iResultResponse: IResult
     ) {
         val queuee = Volley.newRequestQueue(context)

         var url ="https://www.googleapis.com/androidpublisher/v3/applications/com.fractal.chaoss/purchases/subscriptions/"+
                 PermissionAccept.SUBS_SKUS.get(0) + "/" +"tokens/" + AppApplication.getPref(
             AppApplication.getInstance()!!
         )
             .getString("S_Token", "").toString()+"?access_token="+"ya29.A0ARrdaM849-gFKMbQE8fB5zjsT1mOSkj39mhkUSEV1GQP6Vrotx4utdxJ6Q1XUYHkoAwzF2lduuEe9udMHWGRkSRPy7IUedDZQZKBzIFVSGxUoVxwn-9HdO5Jd7KRyY7w07LISfovnqFtTQl6rUppeRZ9xKlb"
         Log.v("data", "URL Get publisher :::" + url)
         val getResponse = object : StringRequest(Request.Method.GET,
             url,
             Response.Listener<String> { response ->
                 Log.v("data", "get 2 :::" + response)
                 val mainObject = JSONObject(response)
                 iResultResponse.notifySuccess(mainObject)
             },
             Response.ErrorListener { e ->

                 Log.v("data", "get error 2 :::" + e.networkResponse.statusCode)
                 iResultResponse.notifyError(e)
             }) {
         }
         queuee.add(getResponse)
    }*/

    interface IResult {
        fun notifySuccess(response: JSONObject)
        fun notifyError(error: VolleyError?)
    }

}