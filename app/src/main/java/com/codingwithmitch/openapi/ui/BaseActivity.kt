package com.codingwithmitch.openapi.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.ResponseType.*
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), DataStateChangeListener, UICommunicationListener {

    val TAG: String = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager


    override fun onUIMessageReceived(uiMessage: UIMessage) {
        when(uiMessage.uiMessageType){
            is UIMessageType.AreYouSureDialog->{
                areYouSureDialog(uiMessage.message, uiMessage.uiMessageType.callback)
            }
            is UIMessageType.Toast->{
                displayToast(uiMessage.message)
            }
            is UIMessageType.Dialog->{
                displayInfoDialog(uiMessage.message)
            }
            is UIMessageType.None->{
                Log.i(TAG, "onUiMessageReceived : ${uiMessage.message}")
            }
        }
    }

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

    override fun hideSoftKeyboard() {
        if(currentFocus!=null){
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}











