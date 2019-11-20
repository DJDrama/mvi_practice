package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register_button.setOnClickListener {
            register()
        }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer {authViewState->
            authViewState.registrationFields?.let { registrationFields ->
                registrationFields.registration_email?.let { registration_email ->
                    input_email.setText(registration_email)
                }
                registrationFields.registration_username?.let{registration_username->
                    input_username.setText(registration_username)
                }
                registrationFields.registration_password?.let{registration_password->
                    input_password.setText(registration_password)
                }
                registrationFields.registration_confirm_password?.let{registration_confirm_password->
                    input_password_confirm.setText(registration_confirm_password)
                }
            }
        })
    }
private    fun register(){
       viewModel.setStateEvent(
           AuthStateEvent.RegisterAttemptEvent(
               input_email.text.toString(),
               input_username.text.toString(),
               input_password.text.toString(),
               input_password_confirm.text.toString()
           )
       )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }
}
