package com.nadhifm.storyapp.di

import android.content.Context
import com.nadhifm.storyapp.BuildConfig
import com.nadhifm.storyapp.data.local.data_store.DataStoreManager
import com.nadhifm.storyapp.data.paging.StoryPagingSource
import com.nadhifm.storyapp.data.remote.network.APIService
import com.nadhifm.storyapp.data.repository.AppRepositoryImpl
import com.nadhifm.storyapp.domain.repository.AppRepository
import com.nadhifm.storyapp.utils.Constans.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            OkHttpClient.Builder().build()
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): APIService = retrofit.create(APIService::class.java)

    @Singleton
    @Provides
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }

    @Singleton
    @Provides
    fun provideStoryPagingSource(apiService: APIService, dataStoreManager: DataStoreManager): StoryPagingSource {
        return StoryPagingSource(apiService, dataStoreManager)
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        apiService: APIService,
        dataStoreManager: DataStoreManager,
        storyPagingSource: StoryPagingSource
    ): AppRepository = AppRepositoryImpl(apiService, dataStoreManager, storyPagingSource)
}