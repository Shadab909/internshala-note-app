package com.internshala.notes.presentation.screen.sign_in_screen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.internshala.notes.databinding.FragmentSignInBinding
import timber.log.Timber

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var gsc : GoogleSignInClient
    private lateinit var googleSignInResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        googleSignInResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK){
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Timber.d("firebaseAuthWithGoogle: ${account.id}")
                    findNavController().navigate(SignInFragmentDirections.actionLoginFragmentToNotesListFragment(true))
                } catch (e: ApiException) {
                    Timber.e("Google sign in failed: ${e.statusCode}")
                    Toast.makeText(requireContext(), "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)
        binding.signInWithGoogleLayout.setOnClickListener {
            Timber.d("signInWithGoogleButton clicked")
            val signInIntent = gsc.signInIntent
            googleSignInResultLauncher.launch(signInIntent)
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }
}