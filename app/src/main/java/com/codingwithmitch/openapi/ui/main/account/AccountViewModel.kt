package com.codingwithmitch.openapi.ui.main.account

import android.accounts.Account
import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.repository.main.AccountRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.auth.state.AuthViewState
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent.*
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()

    }

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {
            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }
            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                   authToken.account_pk?.let{pk->
                       accountRepository.saveAccountProperties(authToken,
                           AccountProperties(pk, stateEvent.email, stateEvent.username)
                       )
                   }
                }?: AbsentLiveData.create()
            }
            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(authToken, stateEvent.currentPassword, stateEvent.newPassword, stateEvent.confirmNewPassword)
                }?: AbsentLiveData.create()
            }
            is None -> {
                return object: LiveData<DataState<AccountViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }

        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update

    }

    fun logout() {
        sessionManager.logout()
    }
    fun cancelActiveJobs(){
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    fun handlePendingData(){
        setStateEvent(AccountStateEvent.None())
    }
    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}