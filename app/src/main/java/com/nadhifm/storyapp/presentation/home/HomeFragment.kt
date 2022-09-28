package com.nadhifm.storyapp.presentation.home

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.FragmentHomeBinding
import com.nadhifm.storyapp.utils.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStories()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llSeeLocation.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
        }

        setupRecycleView()
        observeStories()
    }

    private fun observeStories() {
        viewModel.stories
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach(storyAdapter::submitData)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupRecycleView() {
        storyAdapter = StoryAdapter { story, extras ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailStoryFragment(story)
            findNavController().navigate(
                action,
                extras
            )
        }
        binding.rvStory.apply {
            adapter = storyAdapter.withLoadStateFooter(
                 footer = FooterLoadStateAdapter { storyAdapter.retry() }
            )
            this.layoutManager = LinearLayoutManager(requireContext())
            postponeEnterTransition()
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        storyAdapter.addLoadStateListener { loadState ->
            when (loadState.source.refresh) {
                is LoadState.NotLoading -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvStory.visibility = View.VISIBLE
                }
                is LoadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvStory.visibility = View.GONE
                }
                is LoadState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvStory.visibility = View.GONE
                    val errorState = loadState.source.refresh as LoadState.Error
                    showErrorSnackbar(binding.root, errorState.error.message.toString())
                }
            }
        }

    }
}                                                           