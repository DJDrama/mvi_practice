package com.codingwithmitch.openapi.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.Injectable
import com.codingwithmitch.openapi.ui.DataStateChangeListener
import com.codingwithmitch.openapi.ui.UICommunicationListener
import com.codingwithmitch.openapi.ui.main.MainDependencyProvider
import com.codingwithmitch.openapi.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.BlogViewModel
import com.codingwithmitch.openapi.ui.main.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.create_blog.state.CreateBlogViewState
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseCreateBlogFragment :  Fragment(), Injectable {

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var dependencyProvider: MainDependencyProvider

    lateinit var uiCommunicationListener: UICommunicationListener

    lateinit var viewModel: CreateBlogViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }

        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }

        try{
            dependencyProvider = context as MainDependencyProvider
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement MainDependencyProvider" )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(
                this,
                dependencyProvider.getViewModelProviderFactory()
            ).get(CreateBlogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        cancelActiveJobs()

        //restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
    fun isViewModelInitialized() = ::viewModel.isInitialized
    override fun onSaveInstanceState(outState: Bundle) {
        if(isViewModelInitialized()){
            outState.putParcelable(
                CREATE_BLOG_VIEW_STATE_BUNDLE_KEY,
                viewModel.viewState.value
            )
        }
        super.onSaveInstanceState(outState)
    }

     fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    private fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity){
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }
}