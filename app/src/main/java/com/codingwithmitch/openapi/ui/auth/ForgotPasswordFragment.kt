package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.di.auth.AuthScope
import javax.inject.Inject


@AuthScope
class ForgotPasswordFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.fragment_forgot_password) {

    val viewModel: AuthViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

}
