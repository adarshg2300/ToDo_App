package com.trainee.appinventiv.notesapp.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.trainee.appinventiv.notesapp.R
import com.trainee.appinventiv.notesapp.databinding.FragmentLoginBinding
import com.trainee.appinventiv.notesapp.model.request.UserRequest
import com.trainee.appinventiv.notesapp.utils.NetworkResult
import com.trainee.appinventiv.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by activityViewModels<AuthViewModel>()

   @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater , container , false)
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)

        binding.btnLogin.setOnClickListener {
            if (isValidate().first) {

                authViewModel.signIn(getUserRequest())

            } else {
                showValidationError(isValidate().second)
            }

        }
        binding.btnSignUp.setOnClickListener {
            findNavController().popBackStack()
        }
        onObserver()
    }

    private fun showValidationError(error: String) {
        binding.txtError.text = error
    }

    private fun isValidate(): Pair<Boolean , String> {
        val getUser = getUserRequest()
        return authViewModel.validCredential(
            getUser.email ,
            "",
            getUser.password ,
            true
        )
    }

    private fun getUserRequest(): UserRequest {
        return binding.run {
            UserRequest(
                txtEmail.text.toString(),
                txtPassword.text.toString(),
                ""
            )
        }
    }

    private fun onObserver() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner , Observer {
            when (it) {
                is NetworkResult.Success -> {
                    Log.e("hiiiii", "${it.data!!.token}" , )
                    tokenManager.saveToken(it.data.token)
                    moveToMainFragment()
                }
                is NetworkResult.Error -> {
                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading -> {
                    // binding.
                }
            }
        })
    }

    private fun moveToMainFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}