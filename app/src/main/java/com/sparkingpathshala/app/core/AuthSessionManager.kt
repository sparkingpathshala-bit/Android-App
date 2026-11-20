package com.sparkingpathshala.app.core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Coordinates app-wide authentication session lifecycle events.
 * Dispatches session expiration signals across UI and background layers.
 */
object AuthSessionManager {

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    /**
     * Emits a session expired event to trigger global logout and navigation back to Login screen.
     */
    fun notifySessionExpired() {
        _sessionExpiredEvent.tryEmit(Unit)
    }
}
