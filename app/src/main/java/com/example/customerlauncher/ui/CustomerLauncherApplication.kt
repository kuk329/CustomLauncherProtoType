package com.example.customerlauncher.ui

import android.app.Application
import com.example.customerlauncher.di.NetWorkModule
import com.example.customerlauncher.di.appModule
import com.example.customerlauncher.service.ledcontrol.LedControlIml
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CustomerLauncherApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CustomerLauncherApplication)
            modules(appModule, NetWorkModule)
        }

//        try{
//            val ledService = LedControlIml()
//        }

    }
}