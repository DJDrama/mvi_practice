package com.codingwithmitch.openapi.di.main

import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.di.ViewModelKey
import com.codingwithmitch.openapi.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

}