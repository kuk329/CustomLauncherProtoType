package com.example.customerlauncher.di

import com.example.customerlauncher.ui.main.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel{ HomeViewModel(get(),get()) }
}