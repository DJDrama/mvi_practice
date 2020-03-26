package com.codingwithmitch.openapi.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.main.MainScope
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import kotlinx.android.synthetic.main.fragment_update_account.*
import javax.inject.Inject

@MainScope
class UpdateAccountFragment
@Inject
constructor(
    private val viewmodelFactory: ViewModelProvider.Factory
) : BaseAccountFragment(R.layout.fragment_update_account) {

    val viewModel: AccountViewModel by viewModels {
        viewmodelFactory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        //restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }
    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState->
            if (dataState != null) {
                /** should check null due to process death **/
                stateChangeListener.onDataStateChange(dataState)
                Log.d(TAG, "UpdateAccountFragment, DataState: ${dataState}")
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState->
            if(viewState!=null){
                viewState.accountProperties?.let{
                    Log.d(TAG, "UpdateAccountFragment, ViewState : ${it}")
                    setAccountDataFields(it)
                }
            }
        })
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        input_email?.setText(accountProperties.email)
        input_username?.setText(accountProperties.username)
    }

    private fun saveChanges(){
        viewModel.setStateEvent(
            AccountStateEvent.UpdateAccountPropertiesEvent(
                input_email.text.toString(),
                input_username.text.toString()
            )
        )
        stateChangeListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save ->{
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}