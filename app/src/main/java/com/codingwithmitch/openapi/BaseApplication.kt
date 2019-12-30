package com.codingwithmitch.openapi

import android.app.Activity
import android.app.Application
import com.codingwithmitch.openapi.di.AppInjector
import com.codingwithmitch.openapi.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class BaseApplication: Application(), HasAndroidInjector{

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

//    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
//       // return DaggerAppComponent.builder().application(this).build()
//
//    }




}