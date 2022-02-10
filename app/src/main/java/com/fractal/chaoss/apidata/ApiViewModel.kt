package com.fractal.chaoss.apidata

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fractal.chaoss.manager.home.model.ValidateSubscriptionModel
import com.fractal.chaoss.manager.detail.DetailActivity
import com.fractal.chaoss.manager.detail.model.SelectWallaperModel
import com.fractal.chaoss.manager.home.MainActivity
import com.google.gson.JsonObject


class ApiViewModel : ViewModel() {
    private var repository: ApiRepo? = null

    private var mutableLiveModel: MutableLiveData<JsonObject?>? = null
    private var mutableTokenModel: LiveData<String?>? = null
    private var mutableExpiryModel: LiveData<Long?>? = null
    private var mutableSelectLiveDataModel: MutableLiveData<SelectWallaperModel?>? = null
    private var mutableSubscribePlayStoreDataModel: MutableLiveData<JsonObject?>? = null
    private var mutableValidateSubscriptionModelDataModel: MutableLiveData<ValidateSubscriptionModel?>? =
        null

    fun getHomeList(mainActivity: MainActivity) {
        if (mutableLiveModel != null) {
            return
        }
        repository = ApiRepo().getInstance()

        if (isTablet(mainActivity)) {
            mutableLiveModel = repository!!.getData("2", mainActivity)
        } else {
            mutableLiveModel = repository!!.getData("1", mainActivity)
        }

    }  // 1 mobile and 2 tab


    fun isTablet(context: Context): Boolean {
        return ((context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }


    fun selectWallapper(mainActivity: DetailActivity, id: String) {
        if (mutableSelectLiveDataModel != null) {
            return
        }
        repository = ApiRepo().getInstance()

        if (isTablet(mainActivity)) {
            mutableSelectLiveDataModel = repository!!.selectWallapper("2", id,mainActivity)
        } else {
            mutableSelectLiveDataModel = repository!!.selectWallapper("1", id,mainActivity)
        }
    }

    fun subscribePlayStore(mainActivity: Activity, email: String) {
        if (mutableSelectLiveDataModel != null) {
            return
        }
        repository = ApiRepo().getInstance()
        mutableSubscribePlayStoreDataModel = repository!!.subscribePlayStore(mainActivity, email)
    }


    fun validateSubscription(mainActivity: MainActivity, email: String) {
        if (mutableSelectLiveDataModel != null) {
            return
        }
        repository = ApiRepo().getInstance()
        mutableValidateSubscriptionModelDataModel =
            repository!!.validateSubscription(mainActivity, email)
    }

    /*fun getRefreshToken(mainActivity: Activity) {
       if (mutableTokenModel != null) {
           return
       }
       repository = ApiRepo().getInstance()

       if (isTablet(mainActivity)) {
           mutableTokenModel = repository!!.getRefreshToken()
       } else {
           mutableTokenModel = repository!!.getRefreshToken()
       }

   }

  fun getExpiry(mainActivity: PermissionAccept,rtoken:String,token : String
                 ,purchaseToken : String,id : String) {
       if (mutableTokenModel != null) {
           return
       }
       repository = ApiRepo().getInstance()

       if (isTablet(mainActivity)) {
           mutableExpiryModel = repository!!.getSubscriptionExpire(rtoken,token,purchaseToken,id)
       } else {
           mutableExpiryModel =  repository!!.getSubscriptionExpire(rtoken,token,purchaseToken,id)
       }

   }*/

   /* val tokenModel:LiveData<String?>?
        get() = mutableTokenModel

    val expiryModel:LiveData<Long?>?
        get() = mutableExpiryModel*/

    val homeRepostary: LiveData<JsonObject?>?
        get() = mutableLiveModel

    val selectRepostaryModel: LiveData<SelectWallaperModel?>?
        get() = mutableSelectLiveDataModel

    val subscribePlayStoreDataModel: LiveData<JsonObject?>?
        get() = mutableSubscribePlayStoreDataModel

    val validateSubscriptionModelDataModel: LiveData<ValidateSubscriptionModel?>?
        get() = mutableValidateSubscriptionModelDataModel
}