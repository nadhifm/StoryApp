package com.nadhifm.storyapp.presentation.detail_story

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.FragmentDetailStoryBinding
import com.nadhifm.storyapp.utils.formatDate

class DetailStoryFragment : Fragment() {
    private lateinit var binding: FragmentDetailStoryBinding
    private val args: DetailStoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        postponeEnterTransition()
        startEnterTransitionAfterLoadingImage(args.story.photoUrl, binding.ivPhoto)

        binding.tvName.text = args.story.name
        binding.tvCreatedDate.text = formatDate(args.story.createdAt)
        binding.tvDescription.text = args.story.description
    }

    private fun startEnterTransitionAfterLoadingImage(
        imageAddress: String,
        imageView: ImageView
    ) {
        Glide.with(this)
            .load(imageAddress)
            .placeholder(R.color.gray)
            .dontAnimate()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(imageView)
    }
}