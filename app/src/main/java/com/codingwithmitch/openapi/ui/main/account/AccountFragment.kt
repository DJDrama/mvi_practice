package com.codingwithmitch.openapi.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.main.MainScope
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.codingwithmitch.openapi.ui.main.account.state.AccountStateEvent
import com.codingwithmitch.openapi.ui.main.account.state.AccountViewState
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

@MainScope
class AccountFragment
@Inject
constructor(
    private val viewmodelFactory: ViewModelProvider.Factory
) : BaseAccountFragment(R.layout.fragment_account) {

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
        change_password.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }
        logout_button.setOnClickListener {
            viewModel.logout()
        }
        subscribeObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAccountDataFields(accountProperties: AccountProperties) {
        email?.setText(accountProperties.email)
        username?.setText(accountProperties.username)

    }

    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(
            AccountStateEvent.GetAccountPropertiesEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                /** should check null due to process death **/
                stateChangeListener.onDataStateChange(dataState)
                dataState?.let {
                    it.data?.let { data ->
                        data.data?.let { event ->
                            event.getContentIfNotHandled()?.let { viewState ->
                                viewState.accountProperties?.let { accountProperties ->
                                    Log.d(TAG, "AccountFragment, DataState: ${accountProperties}")
                                    viewModel.setAccountPropertiesData(accountProperties)
                                }
                            }
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let {
                it.accountProperties?.let {
                    Log.d(TAG, "AccountFragment, ViewState: ${it}")
                    setAccountDataFields(it)
                }
            }
        })
    }
}