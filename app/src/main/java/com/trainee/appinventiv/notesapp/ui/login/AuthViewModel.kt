package com.trainee.appinventiv.notesapp.ui.login

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainee.appinventiv.notesapp.model.request.UserRequest
import com.trainee.appinventiv.notesapp.model.response.UserResponse
import com.trainee.appinventiv.notesapp.repository.UserRepo
import com.trainee.appinventiv.notesapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepo: UserRepo) : ViewModel() {

     val userResponseLiveData:LiveData<NetworkResult<UserResponse>>
     get()= userRepo.userResponseLiveData

    fun signUpUser(userRequest: UserRequest) {
        viewModelScope.launch {
            userRepo.signUpUser(userRequest)
        }
    }

    fun signIn(userRequest: UserRequest) {
        viewModelScope.launch (Dispatchers.IO){
            userRepo.signInUser(userRequest)
        }
    }

    fun validCredential(email :String , userName:String , password:String,isLogin:Boolean):Pair<Boolean,String>{
        var result =Pair(true,"")
        if(TextUtils.isEmpty(email) || (!isLogin && TextUtils.isEmpty(userName)) || TextUtils.isEmpty(password)){
            result = Pair(false, "Please provide the credentials")
        }
        else if(TextUtils.isEmpty(password) && password.length < 5){
            result = Pair(false,"Password should be greater then 5")
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            result = Pair(false , "please enter valid email")
        }
        return result
    }
}