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

fun BlogViewModel.setBlogFilter(filter: String?) {
    fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
        filter?.let {
            val update = getCurrrentViewStateOrNew()
            update.blogFields.filter = it
            setViewState(update)
        }
    }
}

fun BlogViewModel.setBlogOrder(order: String) {
    val update = getCurrrentViewStateOrNew()
    update.blogFields.order = order
    setViewState(update)
}