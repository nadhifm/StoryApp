package com.nadhifm.storyapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val createdAt: String,
    val description: String,
    val id: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val photoUrl: String
) : Parcelable
