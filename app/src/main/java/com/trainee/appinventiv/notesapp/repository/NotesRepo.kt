package com.trainee.appinventiv.notesapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trainee.appinventiv.notesapp.api.NotesApi
import com.trainee.appinventiv.notesapp.model.request.NoteRequest
import com.trainee.appinventiv.notesapp.model.response.NoteResponse
import com.trainee.appinventiv.notesapp.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NotesRepo @Inject constructor(private val notesApi: NotesApi) {
    private val _notesLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val notesLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = _notesLiveData

    private val _statusLiveData = MutableLiveData<NetworkResult<Pair<Boolean , String>>>()
    val statusLiveData get() = _statusLiveData

    suspend fun createNotes(noteRequest: NoteRequest) {

        val response = notesApi.createNote(noteRequest)
        Log.e("creatednotes" , "createNotes${response.body()}" , )
        handleResponse(response , "Node is created")
    }

    suspend fun getNotes() {
        val response = notesApi.getNotes()
        if (response.isSuccessful && response.body() != null) {
            _notesLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val obj = JSONObject(response.errorBody()!!.charStream().readText())
            _notesLiveData.postValue(NetworkResult.Error(obj.getString("message")))
        } else {
            _notesLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }

    suspend fun updateNote(notesId: String , notesRequest: NoteRequest) {
        val response = notesApi.updateNote(notesId , notesRequest)
        handleResponse(response , "Notes Updated")

    }

    suspend fun deleteNote(notesId: String) {

        val response = notesApi.deleteNode(notesId)
        handleResponse(response , "Notes deleted")

    }

    private fun handleResponse(response: Response<NoteResponse> , message: String) {
        if (response.isSuccessful && response.body() != null) {
            _statusLiveData.postValue(NetworkResult.Success(Pair(true , message)))
        } else {
            _statusLiveData.postValue(NetworkResult.Success(Pair(false , "Something went wrong")))
        }
    }


}