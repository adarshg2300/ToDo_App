package com.trainee.appinventiv.notesapp.api

import android.net.NetworkRequest
import com.trainee.appinventiv.notesapp.model.request.NoteRequest
import com.trainee.appinventiv.notesapp.model.response.NoteResponse
import retrofit2.Response
import retrofit2.http.*

interface NotesApi {

    @GET("/note")
    suspend fun getNotes():Response<List<NoteResponse>>

    @POST("/note")
    suspend fun createNote(@Body noteRequest: NoteRequest):Response<NoteResponse>

    @PUT("/note/{noteId}")
    suspend fun updateNote(@Path("noteId") noteId:String , @Body noteRequest: NoteRequest):Response<NoteResponse>

    @DELETE("/note/{noteId}")
    suspend fun deleteNode(@Path("noteId") noteId: String):Response<NoteResponse>



//    @POST("/note")
//    suspend fun createNote(@Body noteRequest: NoteRequest): Response<NoteResponse>
//
//    @GET("/note")
//    suspend fun getNotes(): Response<List<NoteResponse>>
//
//    @DELETE("/note/{noteId}")
//    suspend fun deleteNote(@Path("noteId") noteId: String) : Response<NoteResponse>
//
//    @PUT("/note/{noteId}")
//    suspend fun updateNote(
//        @Path("noteId") noteId: String,
//        @Body noteRequest: NoteRequest
//    ): Response<NoteResponse>

}