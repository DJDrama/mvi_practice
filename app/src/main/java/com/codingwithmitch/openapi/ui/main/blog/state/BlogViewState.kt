package com.codingwithmitch.openapi.ui.main.blog.state

import android.net.Uri
import android.os.Parcelable
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.persistence.BlogPostDao
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.persistence.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED
import kotlinx.android.parcel.Parcelize

const val BLOG_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState"
@Parcelize  /** in order to put into Bundle **/
data class BlogViewState(
    //BlogFragment Vars
    var blogFields: BlogFields = BlogFields(),

    //ViewBlogFragment Vars
    var viewBlogFields: ViewBlogFields = ViewBlogFields(),

    //UpdateBlogFragment Vars
    var updatedBlogFields: UpdateBlogFields = UpdateBlogFields()
) : Parcelable {
    @Parcelize
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList(),
        var searchQuery: String = "",
        var page: Int = 1,//pagination
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var filter: String = ORDER_BY_ASC_DATE_UPDATED, //default : date_updated
        var order: String = BLOG_ORDER_ASC // default : ""
    ) : Parcelable

    @Parcelize
    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    ) : Parcelable

    @Parcelize
    data class UpdateBlogFields(
        var updatedBlogTitle: String? = null,
        var updatedBlogBody: String? = null,
        var updatedImageUri: Uri? = null
    ) : Parcelable
}