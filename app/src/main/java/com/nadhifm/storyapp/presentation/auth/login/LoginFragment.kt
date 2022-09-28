package com.nadhifm.storyapp.presentation.auth.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.FragmentLoginBinding
import com.nadhifm.storyapp.utils.Resource
import com.nadhifm.storyapp.utils.createLoadingDialog
import com.nadhifm.storyapp.utils.showErrorSnackbar
import com.nadhifm.storyapp.utils.showSuccessSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingDialog = createLoadingDialog(requireContext(), layoutInflater)

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLogin.setOnClickListener {
            login()
        }

        observeLoginResult()
    }

    private fun login() {
        with(binding) {
            if (tilLoginEmail.error != null) {
                edLoginEmail.requestFocus()
            } else if (tilLoginPassword.error != null) {
                edLoginPassword.requestFocus()
            } else {
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()

                if (email.isEmpty()) {
                    edLoginEmail.requestFocus()
                    tilLoginEmail.error = getString(R.string.input_your_email)
                } else if (password.isEmpty()) {
                    edLoginPassword.requestFocus()
                    tilLoginPassword.error = getString(R.string.input_your_password)
                } else {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun observeLoginResult() {
        viewModel.loginResult
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { result ->
                when(result) {
                    is Resource.Error -> {
                        loadingDialog.dismiss()
                        showErrorSnackbar(binding.root, result.message.toString())
                    }
                    is Resource.Loading -> {
                        loadingDialog.show()
                    }
                    is Resource.Success -> {
                        loadingDialog.dismiss()
                        showSuccessSnackbar(binding.root, result.data.toString())
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}