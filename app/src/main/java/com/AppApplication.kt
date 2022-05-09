package com

import android.app.Application
import android.content.SharedPreferences


class AppApplication : Application(){
    override fun onCreate() {
        super.onCreate()
    }
    lateinit var editor: SharedPreferences.Editor

    init {
        instance = this

    }


    companion object {
        private var instance: AppApplication? = null

        fun getInstance(): AppApplication? {
            return instance
        }
        fun getPref(inst: AppApplication): SharedPreferences {
            var sharedPref: SharedPreferences = inst.getSharedPreferences("FC", 0)
            return sharedPref
        }
        fun isTablet(): Boolean {
            return instance!!.getResources().getBoolean(com.fc.homescreen.R.bool.isTablet)
        }


    }

}

