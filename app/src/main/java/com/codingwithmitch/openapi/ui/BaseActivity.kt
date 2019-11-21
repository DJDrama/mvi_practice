package com.codingwithmitch.openapi.ui

import android.util.Log
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.ResponseType.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener {

    val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Main) {
                displayProgressBar(it.loading.isLoading)
                it.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }
                it.data?.let {
                    it.response?.let { responseEvent ->
                        handleStateResponse(responseEvent)
                    }
                }
            }
        }
    }



    private fun handleStateError(errorEvent: Event<StateError>){
        errorEvent.getContentIfNotHandled()?.let{
            when(it.response.responseType){
                is Toast ->{
                    it.response.message?.let{
                        displayToast(it)
                    }
                }
                is Dialog->{
                    it.response.message?.let{
                        displayErrorDialog(it)
                    }

                }
                is None ->{
                    Log.e(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }
    private fun handleStateResponse(event: Event<Response>){
        event.getContentIfNotHandled()?.let{
            when(it.responseType){
                is Toast ->{
                    it.message?.let{msg->
                        displayToast(msg)
                    }
                }
                is Dialog->{
                    it.message?.let{msg->
                        displaySuccessDialog(msg)
                    }

                }
                is None ->{
                    Log.e(TAG, "handleStateError: ${it.message}")
                }
            }
        }
    }
    abstract fun displayProgressBar(bool: Boolean)
}











