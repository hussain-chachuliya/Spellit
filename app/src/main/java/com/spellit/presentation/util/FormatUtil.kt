package com.spellit.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FormatUtil {
    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    /** Formats epoch millis as `01 Sep 2026`. */
    fun formatDate(epochMillis: Long): String = dateFormatter.format(Date(epochMillis))

    /** Formats epoch millis as 12-hour time with AM/PM, e.g. `03:05 PM`. */
    fun formatTime(epochMillis: Long): String = timeFormatter.format(Date(epochMillis))

    /** Formats a duration in seconds as a playful countdown string. */
    fun formatCountdown(millis: Long): String {
        val totalSeconds = (millis.coerceAtLeast(0L) + 999L) / 1000L
        val seconds = (totalSeconds % 60).toString().padStart(2, '0')
        val minutes = totalSeconds / 60
        return if (minutes > 0) "$minutes:$seconds" else "0:$seconds"
    }

    fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
}

fun formatCountdown(millis: Long): String = FormatUtil.formatCountdown(millis)