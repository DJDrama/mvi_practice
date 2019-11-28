package com.codingwithmitch.openapi.repository.main

import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.codingwithmitch.openapi.api.main.OpenApiMainService
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.persistence.AccountPropertiesDao
import com.codingwithmitch.openapi.repository.NetworkBoundResource
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
){
    private val TAG: String = "AppDebug"

    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>>{
        return object: NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true
        ){
            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object: LiveData<AccountViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value= AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let{
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.email,
                        cacheObject.username
                    )
                }
            }
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main){
                    //finish by viewing the db cache
                    result.addSource(loadFromCache()){viewState->
                        onCompleteJob(DataState.data(
                            data=viewState,
                            response=null
                        ))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: GenericApiResponse.ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties(
                    "Token ${authToken.token}"

                    )
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob=job
            }



        }.asLiveData()
    }

    fun cancelActiveJobs(){
        Log.d(TAG, "AuthRepository: cancelling on going jobs...")
    }
}