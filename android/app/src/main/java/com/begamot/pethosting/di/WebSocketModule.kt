package com.begamot.pethosting.di

import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.api.WebSocketService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WebSocketModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideWebSocketService(
        okHttpClient: OkHttpClient,
        tokenManager: TokenManager,
        gson: Gson
    ): WebSocketService {
        return WebSocketService(okHttpClient, tokenManager, gson)
    }
}
