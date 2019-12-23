package com.codingwithmitch.openapi.ui.main.blog.viewmodel


fun BlogViewModel.getPage(): Int{
    getCurrrentViewStateOrNew().let{
        return it.blogFields.page
    }
}

fun BlogViewModel.getIsQueryExhuasted(): Boolean{
    getCurrrentViewStateOrNew().let{
        return it.blogFields.isQueryExhausted
    }
}

fun BlogViewModel.getIsQueryInProgress(): Boolean{
    getCurrrentViewStateOrNew().let{
        return it.blogFields.isQueryInProgress
    }
}

fun BlogViewModel.getSearchQuery(): String{
    getCurrrentViewStateOrNew().let{
        return it.blogFields.searchQuery
    }
}

fun BlogViewModel.getFilter(): String{
    getCurrrentViewStateOrNew().let{
        return it.blogFields.filter
    }
}
fun BlogViewModel.getOrder(): String{
    getCurrrentViewStateOrNew().let{
        return it.blogFields.order
    }
}
fun BlogViewModel.getSlug(): String{
    getCurrrentViewStateOrNew().let{
        it.viewBlogFields.blogPost?.let{blogPost->
            return blogPost.slug
        }
    }
    return ""
}
fun BlogViewModel.isAuthorOfBlogPost(): Boolean{
    getCurrrentViewStateOrNew().let{
        return it.viewBlogFields.isAuthorOfBlogPost
    }
}