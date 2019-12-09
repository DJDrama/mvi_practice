package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val blogRepository: BlogRepository,
    val sharedPreferences: SharedPreferences,
    val requestManager: RequestManager
): BaseViewModel<BlogStateEvent, BlogViewState>(){
    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when(stateEvent){
            is BlogSearchEvent->{
                return sessionManager.cachedToken.value?.let{authToken ->
                    blogRepository.searchBlogPosts(
                        authToken= authToken,
                        query = getSearchQuery(),
                        page = getPage()
                    )
                }?:AbsentLiveData.create()
            }
            is CheckAuthorOfBlogPost->{
                return AbsentLiveData.create()
            }
            is None->{
                return object: LiveData<DataState<BlogViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(
                            null,
                            Loading(false),
                            null
                        )
                    }
                }
            }
        }
    }


    fun cancelActiveJobs(){
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }
    private fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}