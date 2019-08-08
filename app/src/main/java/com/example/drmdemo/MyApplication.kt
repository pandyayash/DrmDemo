package com.example.drmdemo

import android.app.Application
import com.example.drmdemo.retrofit.Retrofit


class MyApplication : Application() {

    companion object Singleton {
        private lateinit var app: MyApplication
        fun getInstance(): MyApplication {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        Retrofit.init()


    }


}