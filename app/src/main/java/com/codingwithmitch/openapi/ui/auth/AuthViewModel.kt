package com.codingwithmitch.openapi.ui.auth

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent.*
import com.codingwithmitch.openapi.ui.auth.state.AuthViewState
import com.codingwithmitch.openapi.ui.auth.state.LoginFields
import com.codingwithmitch.openapi.ui.auth.state.RegistrationFields
import com.codingwithmitch.openapi.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent, AuthViewState>(){



    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){
            is LoginAttemptEvent->{
                return authRepository.attemptLogin(
                    stateEvent.email, stateEvent.password
                )

            }
            is RegisterAttemptEvent->{
                return authRepository.attemptRegistration(
                    stateEvent.email, stateEvent.username, stateEvent.password, stateEvent.confirm_password
                )
            }
            is CheckPreviousAuthEvent->{
                return authRepository.checkPreviousAuthUser()
            }
            is None->{
                return object: LiveData<DataState<AuthViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrrentViewStateOrNew()
        if(update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        setViewState(update)

    }
    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrrentViewStateOrNew()
        if(update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        setViewState(update)

    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrrentViewStateOrNew()
        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        setViewState(update)
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }


    fun cancelActiveJobs(){
        handlePendingData()
        authRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }
    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}
























