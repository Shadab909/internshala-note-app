package com.internshala.notes.presentation.screen.settings_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.internshala.notes.databinding.FragmentSettingsBinding
import timber.log.Timber

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment.
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val gsc = GoogleSignIn.getClient(requireActivity(), gso)

        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())

        if (account != null) {
            binding.userName.text = account.displayName
            binding.userEmail.text = account.email
        }

        binding.signOutWithGoogleLayout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    gsc.signOut().addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Navigate to the login screen.
                            val action = SettingsFragmentDirections.actionSettingsFragmentToLoginFragment()
                            findNavController().navigate(action)
                        } else {
                            // Log the error.
                            Timber.e(it.exception)
                        }
                    }
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.backImageView.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}