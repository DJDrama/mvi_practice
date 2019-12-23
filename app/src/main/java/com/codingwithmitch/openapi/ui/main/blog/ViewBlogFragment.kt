package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.models.BlogPost
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent
import com.codingwithmitch.openapi.ui.main.blog.state.BlogStateEvent.*
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.isAuthorOfBlogPost
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.setIsQuthorOfBlogPost
import com.codingwithmitch.openapi.util.DateUtils
import kotlinx.android.synthetic.main.fragment_view_blog.*

class ViewBlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        checkIsAuthorOfBlogPost()
        subscribeObservers()
    }
    private fun checkIsAuthorOfBlogPost(){
        viewModel.setIsQuthorOfBlogPost(false)
        viewModel.setStateEvent(CheckAuthorOfBlogPost())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if(viewModel.isAuthorOfBlogPost()){
            inflater.inflate(R.menu.edit_view_menu, menu)
        }
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let{data->
                data.data?.getContentIfNotHandled()?.let{viewState->
                    viewModel.setIsQuthorOfBlogPost(
                        viewState.viewBlogFields.isAuthorOfBlogPost
                    )

                }

            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState->
            viewState.viewBlogFields.blogPost?.let{blogPost->
                setBlogProperties(blogPost)
            }
            if(viewState.viewBlogFields.isAuthorOfBlogPost){
                adapterViewToAuthorMode()
            }

        })
    }
    private fun adapterViewToAuthorMode(){
        activity?.invalidateOptionsMenu() //refresh menu
        delete_button.visibility = View.VISIBLE
    }
    private fun setBlogProperties(blogPost: BlogPost){
        requestManager
            .load(blogPost.image)
            .into(blog_image)
        blog_title.text = blogPost.title
        blog_author.text = blogPost.username
        blog_update_date.text = DateUtils.convertLongToStringDate(
            blogPost.date_updated
        )
        blog_body.text = blogPost.body
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(viewModel.isAuthorOfBlogPost()){
            when(item.itemId){
                R.id.edit->{
                    navUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun navUpdateBlogFragment(){
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}