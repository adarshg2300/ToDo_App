package com.trainee.appinventiv.notesapp.model.request

data class UserRequest(
    val email: String,
    val password: String,
    val username: String
)