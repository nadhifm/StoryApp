package com.nadhifm.storyapp.presentation.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.domain.use_case.GetStoriesUseCase
import com.nadhifm.storyapp.utils.Resource
import com.nadhifm.storyapp.utils.bitmapFromURL
import com.nadhifm.storyapp.utils.resizeBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking


internal class StackRemoteViewsFactory(
    private val mContext: Context,
    private val getStoriesUseCase: GetStoriesUseCase
) : RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        runBlocking(Dispatchers.IO) {
            getStoriesUseCase()
                .onEach { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { stories ->
                                for (story in stories) {
                                    val bitmap = bitmapFromURL(mContext, story.photoUrl)
                                    val newBitmap = resizeBitmap(bitmap, 500, 500)
                                    mWidgetItems.add(newBitmap)
                                }
                            }
                        }
                        else -> {}
                    }
                }
                .launchIn(this)
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

}