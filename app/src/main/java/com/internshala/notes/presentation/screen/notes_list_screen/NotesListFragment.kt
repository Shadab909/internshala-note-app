package com.internshala.notes.presentation.screen.notes_list_screen

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.internshala.notes.common.Resource
import com.internshala.notes.common.Utils.getNoteListViewModeFactory
import com.internshala.notes.databinding.FragmentNotesListBinding
import com.internshala.notes.domain.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class NotesListFragment : Fragment(), NoteItemClickListener {
    private lateinit var binding: FragmentNotesListBinding
    private lateinit var viewModel: NoteListViewModel
    private lateinit var noteListAdapter: NoteListAdapter
    private lateinit var originalNotesList : List<Note>
    private lateinit var navArgs: NotesListFragmentArgs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("onCreateView")
        // Inflate the layout for this fragment
        binding = FragmentNotesListBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            this,
            getNoteListViewModeFactory(requireActivity().application)
        )[NoteListViewModel::class.java]
        noteListAdapter = NoteListAdapter(this)
        originalNotesList = emptyList()
        navArgs = NotesListFragmentArgs.fromBundle(requireArguments())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        if (navArgs.loggedIn) {
            viewModel.makeUserLoggedIn()
        }
        binding.addNoteFab.setOnClickListener {
            val action = NotesListFragmentDirections.actionNotesListFragmentToNoteDetailsFragment(null)
            findNavController().navigate(action)
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getAllNotes().collectLatest { resource ->
                // Update the UI.
                when (resource) {
                    is Resource.Success -> {
                        // Update the UI with the list of notes.
                        val notes: List<Note> = resource.data
                        if (notes.isEmpty()) {
                            // Show placeholder text that there are no notes.
                            Timber.d("No notes found.")
                            withContext(Dispatchers.Main) {
                                binding.homePlaceholder.visibility = View.VISIBLE
                                binding.notesListRv.adapter = noteListAdapter
                                noteListAdapter.submitList(emptyList())
                                originalNotesList = emptyList()
                            }
                        } else {
                            // Show the list of notes.
                            Timber.d("Total notes found: ${notes.size}")
                            withContext(Dispatchers.Main) {
                                binding.homePlaceholder.visibility = View.GONE
                                binding.notesListRv.adapter = noteListAdapter
                                noteListAdapter.submitList(notes)
                                originalNotesList = notes
                            }
                        }
                    }

                    is Resource.Loading -> {
                        // Show a message that the notes are loading.
                        Timber.d("Loading notes.")
                    }

                    is Resource.Error -> {
                        // Show an error message.
                        Timber.d("Error loading notes due to ${resource.exception}")
                    }
                }
            }
        }

        binding.noteSearchEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed.
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                if (searchText.isEmpty()) {
                    noteListAdapter.submitList(originalNotesList)
                } else {
                    val filteredList = originalNotesList.filter {
                        it.title.contains(searchText, ignoreCase = true) ||
                                it.content.contains(searchText, ignoreCase = true)
                    }
                    noteListAdapter.submitList(filteredList)
                }
            }
        })

        binding.settingsBtn.setOnClickListener {
            val action = NotesListFragmentDirections.actionNotesListFragmentToSettingsFragment()
            findNavController().navigate(action)
        }
    }

    override fun onNoteLongClick(noteId: Long) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.delete(noteId)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onNoteClick(note: Note) {
        val action = NotesListFragmentDirections.actionNotesListFragmentToNoteDetailsFragment(note)
        findNavController().navigate(action)
    }
}