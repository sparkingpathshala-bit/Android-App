package com.sparkingpathshala.app.core

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference

/**
 * Unified application memory manager implementing ComponentCallbacks2.
 * Coordinates proactive memory reduction across dynamic memory (anonymous RSS + swap)
 * and enforces strict zero-retention bitmap memory policies in non-visible app states.
 */
object AppMemoryManager : ComponentCallbacks2 {

    private var okHttpClientRef: WeakReference<OkHttpClient>? = null

    /**
     * Initializes the memory manager and registers for system memory events.
     */
    fun init(application: Application, okHttpClient: OkHttpClient) {
        okHttpClientRef = WeakReference(okHttpClient)
        application.registerComponentCallbacks(this)
    }

    @Suppress("DEPRECATION")
    override fun onTrimMemory(level: Int) {
        when (level) {
            // Level 20: The app's UI has become completely hidden (user pressed Home, switched apps, or screen turned off).
            // Google Play 2026 Threshold: Zero bitmaps must be held in non-visible states.
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
                purgeNonVisibleMemory(triggerGc = false)
            }

            // Foreground memory pressure (app is visible, but device is constrained)
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> {
                BitmapMemoryCache.trimToFraction(0.5f)
            }

            // Foreground memory critically low
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                purgeAllCaches()
            }

            // Background memory pressure (app is in background LRU list and system needs RAM)
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
                purgeNonVisibleMemory(triggerGc = true)
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("onTrimMemory(level)"))
    override fun onLowMemory() {
        // Universal emergency callback when the entire system is low on memory
        purgeNonVisibleMemory(triggerGc = true)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // No-op
    }

    /**
     * Purges all bitmap memory and idle socket buffers when transitioning to non-visible states.
     */
    private fun purgeNonVisibleMemory(triggerGc: Boolean) {
        // 1. Evict all cached Bitmaps - satisfies Google Play zero background bitmap retention
        BitmapMemoryCache.evictAll()

        // 2. Evict idle HTTP connection pools - frees anonymous RSS buffers and open socket descriptors
        try {
            okHttpClientRef?.get()?.connectionPool?.evictAll()
        } catch (e: Exception) {
            // Ignore
        }

        // 3. Hint garbage collector if under background memory pressure to return pages to the OS
        if (triggerGc) {
            System.gc()
        }
    }

    /**
     * Evicts foreground caches during active memory pressure.
     */
    private fun purgeAllCaches() {
        BitmapMemoryCache.evictAll()
        try {
            okHttpClientRef?.get()?.connectionPool?.evictAll()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
