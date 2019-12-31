package com.codingwithmitch.openapi.ui.main.account

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
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.Injectable
import com.codingwithmitch.openapi.ui.DataStateChangeListener
import com.codingwithmitch.openapi.ui.main.MainDependencyProvider
import com.codingwithmitch.openapi.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import com.codingwithmitch.openapi.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState
import com.codingwithmitch.openapi.ui.main.blog.viewmodel.BlogViewModel
import com.codingwithmitch.openapi.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

abstract class BaseAccountFragment : Fragment(), Injectable {

    val TAG: String = "AppDebug"

    lateinit var dependencyProvider: MainDependencyProvider

    lateinit var viewModel: AccountViewModel

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
        try{
            dependencyProvider = context as MainDependencyProvider
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement MainDependencyProvider" )
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(
                this,
                dependencyProvider.getViewModelProviderFactory()
            ).get(AccountViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        cancelActiveJobs()

        //restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
    fun isViewModelInitialized() = ::viewModel.isInitialized

    override fun onSaveInstanceState(outState: Bundle) {
        if(isViewModelInitialized()){
            outState.putParcelable(
                ACCOUNT_VIEW_STATE_BUNDLE_KEY,
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
