package com.nadhifm.storyapp.presentation.profile

import android.content.Intent
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
import com.nadhifm.storyapp.databinding.FragmentProfileBinding
import com.nadhifm.storyapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getName()

        binding.linearLayoutLogout.setOnClickListener {
            viewModel.logout()
        }
        binding.linearLayoutChangeLanguage.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setClassName("com.android.settings", "com.android.settings.LanguageSettings")
            startActivity(intent)
        }

        observeName()
        observeLogoutResult()
    }

    private fun observeName() {
        viewModel.name
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                binding.tvName.text = it
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun observeLogoutResult() {
        viewModel.logoutResult
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { result ->
                when(result) {
                    is Resource.Success -> {
                        findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                    }
                    else -> {}
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}