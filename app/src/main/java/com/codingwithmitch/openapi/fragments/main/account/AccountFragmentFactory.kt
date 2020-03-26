package com.codingwithmitch.openapi.fragments.main.account

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.openapi.di.auth.AuthScope
import com.codingwithmitch.openapi.di.main.MainScope
import com.codingwithmitch.openapi.ui.auth.ForgotPasswordFragment
import com.codingwithmitch.openapi.ui.auth.LauncherFragment
import com.codingwithmitch.openapi.ui.auth.LoginFragment
import com.codingwithmitch.openapi.ui.auth.RegisterFragment
import com.codingwithmitch.openapi.ui.main.account.AccountFragment
import com.codingwithmitch.openapi.ui.main.account.ChangePasswordFragment
import com.codingwithmitch.openapi.ui.main.account.UpdateAccountFragment
import javax.inject.Inject

@MainScope
class AccountFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            AccountFragment::class.java.name -> {
                AccountFragment(viewModelFactory)
            }
            UpdateAccountFragment::class.java.name -> {
                UpdateAccountFragment(viewModelFactory)
            }
            ChangePasswordFragment::class.java.name -> {
                ChangePasswordFragment(viewModelFactory)
            }

            else -> {
                AccountFragment(viewModelFactory)
            }
        }


}