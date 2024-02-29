package com.internshala.notes.presentation.screen.note_details_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.internshala.notes.R
import com.internshala.notes.common.Utils.getDateTime
import com.internshala.notes.common.Utils.getNoteDetailsViewModelFactory
import com.internshala.notes.databinding.FragmentUpsertNoteBinding
import com.internshala.notes.domain.model.Note
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.properties.Delegates


class UpsertNoteFragment : Fragment() {
    private lateinit var binding: FragmentUpsertNoteBinding
    private lateinit var viewModel: UpsertNoteViewModel
    private lateinit var navArgs: UpsertNoteFragmentArgs
    private var noteId by Delegates.notNull<Long>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Timber.d("onCreateView")
        binding = FragmentUpsertNoteBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            this,
            getNoteDetailsViewModelFactory(requireActivity().application)
        )[UpsertNoteViewModel::class.java]
        navArgs = UpsertNoteFragmentArgs.fromBundle(requireArguments())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")

        initDetails()

        binding.backImage.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            Timber.d("Upsert button clicked.")
            val isAdd = navArgs.note == null
            // Get the note details from the UI and save it to the database.
            val title = binding.etTitle.text.toString().trim()
            val content = binding.etDescription.text.toString().trim()
            if (title.isEmpty()) {
                Timber.d("Note title is empty.")
                binding.etTitle.error = "Note title is required."
                return@setOnClickListener
            }
            if (content.isEmpty()) {
                Timber.d("Note description is empty.")
                binding.etDescription.error = "Note description is required."
                return@setOnClickListener
            }
            val note : Note = if (isAdd){
                Note(
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
            }else{
                Note(
                    id = noteId,
                    title = title,
                    content = content,
                    timestamp = System.currentTimeMillis()
                )
            }
            Timber.d("Note to be upsert: $note")
            viewLifecycleOwner.lifecycleScope.launch {
                if (isAdd){
                    viewModel.insert(note)
                } else {
                    viewModel.update(note)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isNoteUpsertSuccessFully.collectLatest { isNoteInserted ->
                if (isNoteInserted) {
                    Timber.d("Note upsert successfully.")
                    findNavController().popBackStack()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.noteNotUpsertError.collectLatest { exception ->
                Timber.e("Error in upsert note due to $exception")
                Toast.makeText(requireContext(), "Error in ${if (navArgs.note == null) "adding" else "updating"} note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initDetails() {
        if (navArgs.note == null) {
            Timber.d("Add note action.")
            binding.btnSave.text = getString(R.string.save)
            binding.editTime.text = getString(R.string.create_at, getDateTime())
        } else {
            Timber.d("Update note action.")
            binding.btnSave.text = getString(R.string.update)
            val note = navArgs.note
            noteId = note?.id?:0
            Timber.d("NoteId to be updated: ${note?.id}")
            binding.etTitle.setText(note?.title)
            binding.etDescription.setText(note?.content)
            binding.editTime.text = getString(R.string.created_at, getDateTime(note?.timestamp?:0))
        }
    }
}