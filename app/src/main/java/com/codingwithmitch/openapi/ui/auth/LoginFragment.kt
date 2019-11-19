package com.codingwithmitch.openapi.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.util.GenericApiResponse
import com.codingwithmitch.openapi.util.GenericApiResponse.*


class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.testLogin().observe(viewLifecycleOwner, Observer {response->
            when(response){
                is ApiSuccessResponse->{
                    Log.e("fuck", "login response success: " + response.body)
                }
                is ApiErrorResponse->{
                    Log.e("fuck", "login response error: " + response.errorMessage)
                }
                is ApiEmptyResponse->{
                    Log.e("fuck", "login response : empty response")
                }
            }

        })


    }


}
