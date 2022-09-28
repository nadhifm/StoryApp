package com.nadhifm.storyapp.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nadhifm.storyapp.databinding.LoadStateFooterItemBinding
import com.nadhifm.storyapp.utils.showErrorSnackbar

class FooterLoadStateAdapter(
    private val retry:() -> Unit
) : LoadStateAdapter<FooterLoadStateAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val view = LoadStateFooterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(parent, view, retry)
    }

    class ViewHolder(
        private val parent: ViewGroup,
        private val binding: LoadStateFooterItemBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.NotLoading -> {
                    binding.progressBar.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                }
                is LoadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.retryButton.visibility = View.GONE
                }
                is LoadState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.retryButton.visibility = View.VISIBLE
                    showErrorSnackbar(parent, loadState.error.message.toString())
                }
            }
        }
    }
}