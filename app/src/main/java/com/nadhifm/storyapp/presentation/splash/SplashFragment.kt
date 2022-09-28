package com.nadhifm.storyapp.presentation.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.FragmentSplashBinding
import com.nadhifm.storyapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.checkToken()

        observeCheckTokenResult()
    }

    private fun observeCheckTokenResult() {
        viewModel.checkTokenResult
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { result ->
                when(result) {
                    is Resource.Error -> {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}