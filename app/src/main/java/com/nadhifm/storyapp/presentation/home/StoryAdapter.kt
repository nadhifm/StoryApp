package com.nadhifm.storyapp.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.StoryItemBinding
import com.nadhifm.storyapp.domain.model.Story

class StoryAdapter(
    private val clickListener: (Story, FragmentNavigator.Extras) -> Unit
) : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Story>() {
        override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(private val  binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story, clickListener: (Story, FragmentNavigator.Extras) -> Unit) {
            with(binding) {
                ivPhoto.transitionName = "photo_$story.id"
                tvName.text = story.name
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.color.gray)
                    .into(ivPhoto)

                root.setOnClickListener {
                    val extras = FragmentNavigatorExtras(ivPhoto to "photo_big")
                    clickListener(story, extras)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        story?.let {
            holder.bind(it, clickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = StoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }
}