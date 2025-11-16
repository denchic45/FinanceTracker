package com.denchic45.financetracker.di

import com.denchic45.financetracker.ui.accountpicker.AccountPickerInteractor
import com.denchic45.financetracker.ui.categorypicker.CategoryPickerInteractor
import com.denchic45.financetracker.ui.tagspicker.TagsPickerInteractor
import org.koin.dsl.module

val interactorModule = module {
    single { AccountPickerInteractor() }
    single { CategoryPickerInteractor() }
    single { TagsPickerInteractor() }
}