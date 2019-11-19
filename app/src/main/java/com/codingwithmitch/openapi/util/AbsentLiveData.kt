package com.codingwithmitch.openapi.util

import androidx.lifecycle.LiveData

//live data of any type, livedata of null value
class AbsentLiveData <T : Any?>
private constructor(): LiveData<T>(){
    init{
        postValue(null)
    }
    companion object{
        fun <T> create(): LiveData<T>{
            return AbsentLiveData()
        }
    }
}