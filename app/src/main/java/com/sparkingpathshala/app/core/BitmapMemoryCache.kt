package com.sparkingpathshala.app.core

import android.graphics.Bitmap
import android.util.LruCache
import java.security.MessageDigest

/**
 * Thread-safe, bounded memory cache for decoded Bitmaps.
 * Enforces Google Play's 2026 performance thresholds by bounding foreground memory usage
 * and allowing complete eviction when the app transitions to background or cached states.
 */
object BitmapMemoryCache {

    private val maxCacheSizeKb: Int by lazy {
        val maxMemoryKb = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        // Allocate 1/8th of available runtime heap, with a hard maximum of 16 MB
        (maxMemoryKb / 8).coerceIn(4096, 16384)
    }

    private val cache: LruCache<String, Bitmap> by lazy {
        object : LruCache<String, Bitmap>(maxCacheSizeKb) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return (bitmap.byteCount / 1024).coerceAtLeast(1)
            }
        }
    }

    /**
     * Retrieves a cached Bitmap by its unique key.
     */
    fun get(key: String): Bitmap? {
        val cacheKey = hashKey(key)
        return cache.get(cacheKey)
    }

    /**
     * Stores a Bitmap in the memory cache.
     */
    fun put(key: String, bitmap: Bitmap) {
        if (bitmap.isRecycled) return
        val cacheKey = hashKey(key)
        cache.put(cacheKey, bitmap)
    }

    /**
     * Completely purges all Bitmaps from memory.
     * Guaranteed zero-retention when app is in non-visible (background/cached) states.
     */
    fun evictAll() {
        cache.evictAll()
    }

    /**
     * Trims the cache to a specific fraction (e.g. 50% under foreground memory pressure).
     */
    fun trimToFraction(fraction: Float) {
        val targetSize = (cache.maxSize() * fraction.coerceIn(0f, 1f)).toInt()
        cache.trimToSize(targetSize)
    }

    /**
     * Generates a compact 16-character MD5 hash for cache keys (especially for long Base64 strings).
     */
    private fun hashKey(rawKey: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val hashBytes = digest.digest(rawKey.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }.take(16)
        } catch (e: Exception) {
            rawKey.hashCode().toString()
        }
    }
}
