package com.codingwithmitch.openapi.ui.main.blog.state

import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogPostDao

data class BlogViewState(
    //BlogFragment Vars
    var blogFields: BlogFields = BlogFields(),

    //ViewBlogFragment Vars
    var viewBlogFields: ViewBlogFields = ViewBlogFields()

    //UpdateBlogFragment Vars
){
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = "",
        var page: Int =1,//pagination
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false
    )
    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    )
}