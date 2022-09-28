package com.nadhifm.storyapp.presentation.auth.register

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.FragmentRegisterBinding
import com.nadhifm.storyapp.utils.Resource
import com.nadhifm.storyapp.utils.createLoadingDialog
import com.nadhifm.storyapp.utils.showErrorSnackbar
import com.nadhifm.storyapp.utils.showSuccessSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = createLoadingDialog(requireContext(), layoutInflater)

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.buttonRegister.setOnClickListener {
            register()
        }

        observeRegisterResult()
    }

    private fun register() {
        with(binding) {
            if (tilRegisterEmail.error != null) {
                edRegisterEmail.requestFocus()
            } else if (tilRegisterPassword.error != null){
                edRegisterPassword.requestFocus()
            } else {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()

                if (name.isEmpty()) {
                    edRegisterName.requestFocus()
                    tilRegisterName.error = getString(R.string.input_your_name)
                }else if (email.isEmpty()) {
                    edRegisterEmail.requestFocus()
                    tilRegisterEmail.error = getString(R.string.input_your_email)
                } else if (password.isEmpty()) {
                    edRegisterPassword.requestFocus()
                    tilRegisterPassword.error = getString(R.string.input_your_password)
                } else {
                    viewModel.register(name, email, password)
                }
            }
        }
    }

    private fun observeRegisterResult() {
        viewModel.registerResult
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
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}