package com.example.studysmart.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.ui.graphics.Color
import com.example.studysmart.presentation.theme.Green
import com.example.studysmart.presentation.theme.Orange
import com.example.studysmart.presentation.theme.Red
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


enum class Priority(val title: String, val color: Color, val value: Int){
    LOW(title = "Low", color = Green, value = 0 ),
    MEDIUM(title = "Medium", color = Orange, value = 1 ),
    HIGH(title = "High", color = Red, value = 2 );

    companion object{
        fun fromInt(value: Int) = values().firstOrNull{ it.value == value} ?: MEDIUM
    }
}
fun Long?.changeMillisToDateString(): String {
    val date: LocalDate = this?.let{
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}



@OptIn(ExperimentalMaterial3Api::class)
object CurrentOrFutureSelectableDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val currentDateMillis =
            LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return utcTimeMillis >= currentDateMillis
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year >= LocalDate.now().year
    }
}

