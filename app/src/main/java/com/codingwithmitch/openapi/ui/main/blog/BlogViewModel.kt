package com.codingwithmitch.openapi.ui.main.blog

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.repository.main.BlogRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
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
            is BlogStateEvent.BlogSearchEvent->{
                return AbsentLiveData.create()
            }
            is BlogStateEvent.None->{
                return AbsentLiveData.create()
            }
        }
    }

    fun setQuery(query: String){
        val update = getCurrrentViewStateOrNew()
        if(query.equals(update.blogFields.searchQuery)){
            return
        }
        update.blogFields.searchQuery = query
        _viewState.value=update
    }

    fun setBlogListData(blogList: List<BlogPost>){
        val update = getCurrrentViewStateOrNew()
        update.blogFields.blogList = blogList
        _viewState.value=update
    }

    fun cancelActiveJobs(){
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }
    private fun handlePendingData(){
        setStateEvent(BlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}