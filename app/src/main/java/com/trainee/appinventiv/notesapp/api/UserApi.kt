package com.trainee.appinventiv.notesapp.api

import com.trainee.appinventiv.notesapp.model.request.UserRequest
import com.trainee.appinventiv.notesapp.model.response.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {

    @POST("/users/signup")
  suspend  fun singUp(@Body userRequest: UserRequest):Response<UserResponse>

    @POST("/users/signin")
  suspend  fun signIn(@Body userRequest: UserRequest):Response<UserResponse>
}