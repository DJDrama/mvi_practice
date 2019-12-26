package com.codingwithmitch.openapi.ui

interface DataStateChangeListener {
    fun onDataStateChange(dataState: DataState<*>?)

    fun expandAppbar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean
}