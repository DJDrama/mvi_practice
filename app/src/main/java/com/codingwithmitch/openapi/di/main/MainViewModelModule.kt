package com.codingwithmitch.openapi.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.openapi.di.auth.keys.MainViewModelKey
import com.codingwithmitch.openapi.ui.main.account.AccountViewModel
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.BlogViewModel
import com.codingwithmitch.openapi.ui.main.create_blog.CreateBlogViewModel
import com.codingwithmitch.openapi.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @MainScope
    @Binds
    abstract fun provideViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(ceateBlogViewModel: CreateBlogViewModel): ViewModel
}