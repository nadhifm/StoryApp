package com.nadhifm.storyapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.nadhifm.storyapp.domain.model.Story

class MyDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}