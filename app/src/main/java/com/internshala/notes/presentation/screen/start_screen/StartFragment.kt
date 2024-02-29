package com.internshala.notes.presentation.screen.start_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.internshala.notes.R
import com.internshala.notes.common.Utils.getStartViewModelFactory
import timber.log.Timber


class StartFragment : Fragment() {
    private lateinit var viewModel: StartViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(
            this,
            getStartViewModelFactory(requireActivity().application)
        )[StartViewModel::class.java]
        return inflater.inflate(R.layout.fragment_start, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isUserLoggedIn()){
            // Navigate to notes list screen.
            val action = StartFragmentDirections.actionStartFragmentToNotesListFragment(false)
            findNavController().navigate(action)
        } else {
            // Navigate to login screen.
            val action = StartFragmentDirections.actionStartFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }

}