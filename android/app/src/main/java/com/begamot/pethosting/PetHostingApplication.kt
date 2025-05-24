package com.begamot.pethosting

import android.app.Application
import android.content.Context
import com.begamot.pethosting.data.api.TokenManager
import com.begamot.pethosting.data.api.WebSocketService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PetHostingApplication : Application() {

    @Inject
    lateinit var webSocketService: WebSocketService

    @Inject
    lateinit var tokenManager: TokenManager

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        if (tokenManager.isLoggedIn()) {
            webSocketService.connect()
        }
    }
}
