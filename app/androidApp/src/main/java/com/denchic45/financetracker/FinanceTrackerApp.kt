package com.denchic45.financetracker

import android.app.Application
import com.denchic45.financetracker.di.dataModule
import com.denchic45.financetracker.di.interactorModule
import com.denchic45.financetracker.di.networkModule
import com.denchic45.financetracker.di.useCaseModule
import com.denchic45.financetracker.di.viewModelModule
import com.denchic45.financetracker.ui.toast.ToastManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class FinanceTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FinanceTrackerApp)
            modules(
                module { singleOf(::ToastManager) },
                appModule,
                networkModule,
                dataModule,
                useCaseModule,
                viewModelModule,
                interactorModule
            )
        }
    }
}