package com.codingwithmitch.openapi.ui.main.blog.viewmodel

import com.codingwithmitch.openapi.models.BlogPost


fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
    val update = getCurrrentViewStateOrNew()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}

fun BlogViewModel.setQueryInProgress(isInProgress: Boolean) {
    fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
        val update = getCurrrentViewStateOrNew()
        update.blogFields.isQueryInProgress = isInProgress
        setViewState(update)
    }
}

fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrrentViewStateOrNew()
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
}

fun BlogViewModel.setIsQuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
    val update = getCurrrentViewStateOrNew()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

fun BlogViewModel.setQuery(query: String) {
    val update = getCurrrentViewStateOrNew()
//        if(query.equals(update.blogFields.searchQuery)){
//            return
//        }
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun BlogViewModel.setBlogListData(blogList: List<BlogPost>) {
    val update = getCurrrentViewStateOrNew()
    update.blogFields.blogList = blogList
    setViewState(update)
}