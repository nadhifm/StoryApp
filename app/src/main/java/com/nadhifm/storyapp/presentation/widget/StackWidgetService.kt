package com.nadhifm.storyapp.presentation.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.nadhifm.storyapp.domain.use_case.GetStoriesUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StackWidgetService : RemoteViewsService() {
    @Inject
    lateinit var getStoriesUseCase: GetStoriesUseCase

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, getStoriesUseCase)
    }
}

