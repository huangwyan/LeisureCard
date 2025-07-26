package com.leisure.card.app

import android.app.Application
import android.content.Context

/**
 *  Create by hwy on 2025/7/26
 **/
class App : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}