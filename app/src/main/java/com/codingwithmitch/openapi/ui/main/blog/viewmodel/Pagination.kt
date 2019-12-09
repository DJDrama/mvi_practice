package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import android.util.Log
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState

fun BlogViewModel.resetPage(){
    val update = getCurrrentViewStateOrNew()
    update.blogFields.page=1
    setViewState(update)
}

fun BlogViewModel.loadFirstPage(){
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    setStateEvent(BlogSearchEvent())
}

fun BlogViewModel.incrementPageNumber(){
    val update = getCurrrentViewStateOrNew()
    val page = update.copy().blogFields.page
    update.blogFields.page = page+1
    setViewState(update)
}

fun BlogViewModel.nextPage(){
    if(!getIsQueryExhuasted() && !getIsQueryInProgress()){
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")
        incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(BlogSearchEvent())
    }
}

fun BlogViewModel.handleIncomingBlogListData(viewState: BlogViewState){
    setQueryExhausted(viewState.blogFields.isQueryExhausted)
    setQueryInProgress(viewState.blogFields.isQueryInProgress)
    setBlogListData(viewState.blogFields.blogList)
}

