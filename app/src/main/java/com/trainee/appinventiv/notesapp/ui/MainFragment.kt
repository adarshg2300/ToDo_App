package com.trainee.appinventiv.notesapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.trainee.appinventiv.notesapp.R
import com.trainee.appinventiv.notesapp.databinding.FragmentMainBinding
import com.trainee.appinventiv.notesapp.model.response.NoteResponse
import com.trainee.appinventiv.notesapp.ui.note.NoteAdapter
import com.trainee.appinventiv.notesapp.ui.note.NotesViewModel
import com.trainee.appinventiv.notesapp.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

   private var _binding:FragmentMainBinding?=null
    private val binding get() = _binding

    private val notesViewModel by viewModels<NotesViewModel>()

    private lateinit var adapter: NoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater , container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
     _binding= FragmentMainBinding.inflate(inflater, container , false)
        adapter = NoteAdapter(::onNoteClicked)
        return binding?.root
    }

    override fun onViewCreated(view: View , savedInstanceState: Bundle?) {
        super.onViewCreated(view , savedInstanceState)
        bindObservers()
        notesViewModel.getNotes()
        binding?.noteList?.layoutManager=StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding?.noteList?.adapter=adapter
        binding?.addNote?.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_notesFragment)
        }
    }

    private fun bindObservers() {
        notesViewModel.notesLiveData.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkResult.Success->{
                    adapter.submitList(it.data)
                }
                is NetworkResult.Error -> {
                    Toast.makeText(requireContext() , it.message , Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {}
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    private fun onNoteClicked(noteResponse: NoteResponse){
        val bundle = Bundle()
        bundle.putString("note", Gson().toJson(noteResponse))
        Toast.makeText(requireContext() , "${noteResponse.title} ${noteResponse.description}" , Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_mainFragment_to_notesFragment , bundle)
    }
}