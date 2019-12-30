package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.room.Update
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogQueryUtils
import com.codingwithmitch.openapi.repository.main.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.Loading
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_FILTER
import com.codingwithmitch.openapi.util.PreferenceKeys.Companion.BLOG_ORDER
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val blogRepository: BlogRepository,
    val sharedPreferences: SharedPreferences,
    val editor: SharedPreferences.Editor
): BaseViewModel<BlogStateEvent, BlogViewState>(){
    init{
        setBlogFilter(
            sharedPreferences.getString(BLOG_FILTER, BlogQueryUtils.BLOG_FILTER_DATE_UPDATED)
        )
        setBlogOrder(sharedPreferences.getString(BLOG_ORDER, BlogQueryUtils.BLOG_ORDER_ASC))
    }
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
                        filterAndOrder = getOrder()+getFilter(),
                        page = getPage()
                    )
                }?:AbsentLiveData.create()
            }
            is CheckAuthorOfBlogPost->{
                return sessionManager.cachedToken.value?.let{authToken ->
                    blogRepository.isAuthorOfBlogPost(
                        authToken = authToken,
                        slug = getSlug()
                    )
                }?:AbsentLiveData.create()
            }
            is DeleteBlogPostEvent ->{
                return sessionManager.cachedToken.value?.let{authToken ->
                    blogRepository.deleteBlogPost(
                        authToken = authToken,
                       blogPost = getBlogPost()
                    )
                }?:AbsentLiveData.create()
            }
            is UpdatedBlogPostEvent ->{
                return sessionManager.cachedToken.value?.let{authToken ->
                    val title = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.title
                    )
                    val body = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.body
                    )
                    blogRepository.updateBlogPost(
                        authToken,
                        getSlug(),
                        title,
                        body,
                        stateEvent.image
                    )
                }?:AbsentLiveData.create()
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

    fun saveFilterOptions(filter: String, order: String){
        editor.putString(BLOG_FILTER, filter)
        editor.apply()

        editor.putString(BLOG_ORDER, order)
        editor.apply()
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