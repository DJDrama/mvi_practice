package com.codingwithmitch.openapi.ui.main.blog

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
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseBlogFragment : Fragment(), Injectable {

    val TAG: String = "AppDebug"

    lateinit var dependencyProvider: MainDependencyProvider

    lateinit var viewModel: BlogViewModel

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var uiCommunicationListener: UICommunicationListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(
                this,
                dependencyProvider.getViewModelProviderFactory()
            ).get(BlogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        cancelActiveJobs()

        //restore state after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
    fun isViewModelInitialized() = ::viewModel.isInitialized
    override fun onSaveInstanceState(outState: Bundle) {
        if(isViewModelInitialized()){
            outState.putParcelable(
                BLOG_VIEW_STATE_BUNDLE_KEY,
                viewModel.viewState.value
            )
        }
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }

        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener")
        }

        try {
            dependencyProvider = context as MainDependencyProvider
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement MainDependencyProvider")
        }
    }

    /** viewmodel should be instantiated byu the time onviewcreated called
     * all of datas need to be set before state is restored.
     * **/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)


    }

    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    private fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }


}