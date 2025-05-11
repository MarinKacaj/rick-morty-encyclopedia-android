package com.marin.rickmortyencyclopedia.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

/**
 * Created by Backbase RnD BV on 11/05/2025.
 */
object DateTimeReformatter {

    private val readFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
    private val writeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun reformat(inputDate: String): String {
        val date: TemporalAccessor = LocalDate.parse(inputDate, readFormatter)
        return writeFormatter.format(date)
    }
}