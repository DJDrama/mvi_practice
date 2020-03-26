package com.codingwithmitch.openapi.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import com.codingwithmitch.openapi.BaseApplication
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.fragments.auth.AuthNavHostFragment
import com.codingwithmitch.openapi.ui.BaseActivity
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.main.MainActivity
import com.codingwithmitch.openapi.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import com.codingwithmitch.openapi.viewmodels.AuthViewModelFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity() {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: AuthViewModelFactory

    val viewModel: AuthViewModel by viewModels{
        providerFactory
    }

    override fun inject() {
        (application as BaseApplication).authComponent()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        subscribeObservers()
        onRestoreInstanceState()
    }
    private fun onRestoreInstanceState(){
        val host = supportFragmentManager
            .findFragmentById(R.id.auth_fragments_container)
        host?.let{
            //do nothing
        }?: createNavHost()
    }

    private fun createNavHost(){
        val navHost = AuthNavHostFragment.create(
            R.navigation.auth_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_fragments_container,
                navHost,
                getString(R.string.AuthNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        checkPreviousAuthUser()
    }

    private fun subscribeObservers() {

        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)
            dataState.data?.let { data ->
                data.data?.let { event ->
                    event.getContentIfNotHandled()?.let {
                        it.authToken?.let {
                            Log.d(TAG, "AuthActivity, DataState : ${it}")
                            viewModel.setAuthToken(it)
                        }
                    }
                }
                data.response?.let{event ->
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                                onFinishCheckPreviousAuthUser()
                            }
                        }
                    }
                }
            }

        })
        viewModel.viewState.observe(this, Observer {
            it.authToken?.let { authToken ->
                sessionManager.login(authToken)
            }
        })
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "AuthActivity: subsribeObservers: AuthToken : $authToken")
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navMainActivity()
            }
        })
    }
    private fun checkPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }
    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }

    private fun navMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        (application as BaseApplication).releaseAuthComponent()
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }


    override fun expandAppbar() {
        //ignore
    }
}

















