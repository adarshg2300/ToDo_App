package com.trainee.appinventiv.notesapp.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.trainee.appinventiv.notesapp.api.UserApi
import com.trainee.appinventiv.notesapp.model.request.UserRequest
import com.trainee.appinventiv.notesapp.model.response.UserResponse
import com.trainee.appinventiv.notesapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepo @Inject constructor(private val userApi: UserApi){

    private val  _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    val userResponseLiveData :LiveData<NetworkResult<UserResponse>>
      get() = _userResponseLiveData

    suspend fun signUpUser(userRequest: UserRequest){
      val response=  userApi.singUp(userRequest)
       // Log.e("response11",  response.body().toString() )
        apiResponse(response)
    }

    suspend fun signInUser(userRequest: UserRequest){
        val response =    userApi.signIn(userRequest)
        // Log.e("response11",  response.body().toString() )
        apiResponse(response)
    }

    private fun apiResponse(response: Response<UserResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val obj = JSONObject(response.errorBody()!!.charStream().readText())
            _userResponseLiveData.postValue(NetworkResult.Error(obj.getString("message")))
        } else {
            _userResponseLiveData.postValue(NetworkResult.Error("Something Went  wrong"))
        }
    }



}