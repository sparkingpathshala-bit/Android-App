package com.sparkingpathshala.app.utils

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility object for device-aware and regional timezone date/time formatting.
 * Defaults to Asia/Kolkata (IST), but automatically adapts to the user's device system timezone.
 */
object DateUtils {

    val defaultTimeZone: TimeZone
        get() = try {
            TimeZone.getDefault() ?: TimeZone.getTimeZone("Asia/Kolkata")
        } catch (_: Exception) {
            TimeZone.getTimeZone("Asia/Kolkata")
        }

    private val isoPatterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "dd-MM-yyyy hh:mm a",
        "yyyy-MM-dd hh:mm a",
        "dd/MM/yyyy",
        "dd-MM-yyyy",
        "yyyy-MM-dd"
    )

    fun parseIsoStringToDate(isoString: String?): Date? {
        if (isoString.isNullOrBlank()) return null
        val trimmed = isoString.trim()

        for (pattern in isoPatterns) {
            try {
                val inputFormat = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = if (pattern.contains("'Z'") || pattern.endsWith("Z")) {
                        TimeZone.getTimeZone("UTC")
                    } else {
                        defaultTimeZone
                    }
                    isLenient = false
                }
                val pos = ParsePosition(0)
                val date = inputFormat.parse(trimmed, pos)
                if (date != null && pos.index == trimmed.length) {
                    return date
                }
            } catch (_: Exception) {
                // Try next pattern
            }
        }
        return null
    }

    fun formatToUserLocalTime(
        isoString: String?,
        outputPattern: String = "dd MMM yyyy, hh:mm a"
    ): String {
        if (isoString.isNullOrBlank()) return "N/A"
        val date = parseIsoStringToDate(isoString) ?: return isoString
        return try {
            val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault()).apply {
                timeZone = defaultTimeZone
            }
            outputFormat.format(date)
        } catch (_: Exception) {
            isoString
        }
    }

    fun formatTimestamp(
        timestamp: Long,
        outputPattern: String = "dd MMM yyyy, hh:mm a"
    ): String {
        return try {
            val sdf = SimpleDateFormat(outputPattern, Locale.getDefault()).apply {
                timeZone = defaultTimeZone
            }
            sdf.format(Date(timestamp))
        } catch (_: Exception) {
            "N/A"
        }
    }

    fun isDatePassed(dateString: String?): Boolean {
        val date = parseIsoStringToDate(dateString) ?: return false
        return date.before(Date())
    }

    fun getTodayDateString(pattern: String = "yyyy-MM-dd"): String {
        return SimpleDateFormat(pattern, Locale.US).apply {
            timeZone = defaultTimeZone
        }.format(Date())
    }
}
