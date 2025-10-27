package com.denchic45.financetracker

import android.app.Application
import com.denchic45.financetracker.di.dataModule
import com.denchic45.financetracker.di.networkModule
import com.denchic45.financetracker.di.useCaseModule
import com.denchic45.financetracker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(networkModule, dataModule, useCaseModule, viewModelModule)
        }
    }
}