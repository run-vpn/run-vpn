package com.runvpn.app.core.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

//actual object TimeUtils {
    actual fun getDateTimeFormatted(daysToAdd: Int, pattern: String): String {

        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DATE, daysToAdd)

        val dateFormat = SimpleDateFormat(pattern, Locale.US)

        return dateFormat.format(cal.time);
    }
//}
