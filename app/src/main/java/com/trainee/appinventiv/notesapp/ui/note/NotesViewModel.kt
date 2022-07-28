package com.trainee.appinventiv.notesapp.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainee.appinventiv.notesapp.model.request.NoteRequest
import com.trainee.appinventiv.notesapp.repository.NotesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val notesRepo: NotesRepo) : ViewModel() {

    val notesLiveData get() = notesRepo.notesLiveData
    val statusLiveData get() = notesRepo.statusLiveData

    fun createNotes(noteRequest: NoteRequest) {
        viewModelScope.launch {
            notesRepo.createNotes(noteRequest)
        }
    }

    fun getNotes() {
        viewModelScope.launch{
            notesRepo.getNotes()
        }
    }

    fun updateNotes(id: String , noteRequest: NoteRequest) {
        viewModelScope.launch {
            notesRepo.updateNote(id , noteRequest)
        }
    }

    fun deleteNotes(id: String) {
        viewModelScope.launch {
            notesRepo.deleteNote(id)
        }
    }

}