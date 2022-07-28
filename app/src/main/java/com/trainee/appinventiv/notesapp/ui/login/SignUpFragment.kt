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
import com.trainee.appinventiv.notesapp.databinding.FragmentSignUpBinding
import com.trainee.appinventiv.notesapp.model.request.UserRequest
import com.trainee.appinventiv.notesapp.ui.login.AuthViewModel
import com.trainee.appinventiv.notesapp.utils.NetworkResult
import com.trainee.appinventiv.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager:TokenManager
    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSignUpBinding.inflate(inflater , container , false)

        Log.e("hiiiiiii" , "${tokenManager.getToken()}" , )
        if (tokenManager.getToken()!=null){
            findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        binding.btnLogin.setOnClickListener {
            moveToLoginFragment()
            binding.txtError.text=""
        }
        binding.btnSignUp.setOnClickListener {
            if (validateUsers().first) {
                val getUser = getUserRequest()
                authViewModel.signUpUser(getUser)
            } else {
                showValidationError(validateUsers().second)
            }
        }

        onObserve()
    }

    private fun showValidationError(error: String) {
        binding.txtError.text = error
    }

    private fun onObserve() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner , Observer {
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    moveToMainFragment()
                }
                is NetworkResult.Error -> {
                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading -> {
                }
            }


        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getUserRequest(): UserRequest {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val userName = binding.txtUsername.text.toString()
        return UserRequest(email , password , userName)
    }

    private fun validateUsers(): Pair<Boolean , String> {
        val getUser = getUserRequest()
        return authViewModel.validCredential(
            getUser.email ,
            getUser.username ,
            getUser.password ,
            false
        )
    }

    private fun moveToMainFragment() {
        findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
    }

    private fun moveToLoginFragment() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }
}