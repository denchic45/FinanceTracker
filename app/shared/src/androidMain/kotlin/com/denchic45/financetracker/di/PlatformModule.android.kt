package com.denchic45.financetracker.di

import com.denchic45.financetracker.ui.toast.ToastManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module { singleOf(::ToastManager) }