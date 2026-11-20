package com.sparkingpathshala.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import androidx.core.graphics.scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Common file resolution utilities for document, image, and PDF operations.
 * Hardened against OutOfMemoryError and oversized file crashes on budget Android devices.
 */
object FileUtils {

    const val MAX_FILE_SIZE_MB: Float = 20.0f

    fun getUriFileName(context: Context, uri: Uri): String {
        var name = "attachment"
        try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        name = it.getString(index)
                    }
                }
            }
        } catch (_: Exception) {}
        return name
    }

    fun getFileSizeMB(context: Context, uri: Uri): Float {
        try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.SIZE)
                    if (index != -1) {
                        val bytes = it.getLong(index)
                        return bytes.toFloat() / (1024 * 1024)
                    }
                }
            }
        } catch (_: Exception) {}
        return 0f
    }

    suspend fun readUriToBase64(
        context: Context,
        uri: Uri,
        mimePrefix: String? = null
    ): Result<Pair<String, String>> = withContext(Dispatchers.IO) {
        try {
            val sizeMb = getFileSizeMB(context, uri)
            if (sizeMb > MAX_FILE_SIZE_MB) {
                return@withContext Result.failure(
                    IllegalArgumentException("Selected file (%.1f MB) exceeds the maximum limit of %.0f MB.".format(sizeMb, MAX_FILE_SIZE_MB))
                )
            }

            val fileName = getUriFileName(context, uri)
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Unable to open file stream"))
            val bytes = inputStream.use { it.readBytes() }
            val encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val finalData = if (mimePrefix != null) "$mimePrefix$encoded" else encoded
            Result.success(Pair(fileName, finalData))
        } catch (t: Throwable) {
            if (t is OutOfMemoryError) {
                System.gc()
                Result.failure(Exception("File is too large to process in device memory. Please select a smaller file."))
            } else {
                Result.failure(t)
            }
        }
    }

    suspend fun compressImageUriToBase64(
        context: Context,
        uri: Uri,
        maxDim: Int = 1024,
        quality: Int = 80
    ): Pair<String, String>? = withContext(Dispatchers.IO) {
        try {
            val sizeMb = getFileSizeMB(context, uri)
            if (sizeMb > MAX_FILE_SIZE_MB) return@withContext null

            val fileName = getUriFileName(context, uri)

            // Step 1: Decode bounds only to calculate inSampleSize without allocating heap
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            var stream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            BitmapFactory.decodeStream(stream, null, options)
            stream.close()

            if (options.outWidth <= 0 || options.outHeight <= 0) {
                // Not a decodable image or SVG/PDF; read directly with size safeguard
                val rawResult = readUriToBase64(context, uri)
                return@withContext rawResult.getOrNull()
            }

            // Step 2: Downsample with inSampleSize
            options.inSampleSize = calculateInSampleSize(options, maxDim, maxDim)
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565

            stream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val sampledBitmap = BitmapFactory.decodeStream(stream, null, options)
            stream.close() ?: return@withContext null

            if (sampledBitmap == null) {
                val rawResult = readUriToBase64(context, uri)
                return@withContext rawResult.getOrNull()
            }

            // Step 3: Exact scaling if still larger than maxDim
            val currentWidth = sampledBitmap.width
            val currentHeight = sampledBitmap.height
            val scale = if (currentWidth > maxDim || currentHeight > maxDim) {
                maxDim.toFloat() / maxOf(currentWidth, currentHeight)
            } else {
                1.0f
            }

            val finalBitmap = if (scale < 1.0f) {
                val scaled = sampledBitmap.scale(
                    (currentWidth * scale).toInt(),
                    (currentHeight * scale).toInt(),
                    true
                )
                if (scaled != sampledBitmap) {
                    sampledBitmap.recycle()
                }
                scaled
            } else {
                sampledBitmap
            }

            // Step 4: Compress to JPEG
            val outputStream = ByteArrayOutputStream()
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val compressedBytes = outputStream.toByteArray()
            outputStream.close()
            finalBitmap.recycle()

            val base64 = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
            Pair(fileName, base64)
        } catch (t: Throwable) {
            if (t is OutOfMemoryError) {
                System.gc()
            }
            null
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }
}
