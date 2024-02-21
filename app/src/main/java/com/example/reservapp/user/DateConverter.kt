package com.example.reservapp.user

import com.example.reservapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Clase encargada de convertir fechas.
 */
class DateConverter {
    /**
     * Convertimos el string de fecha a tipo LocalDate para comparar y mostrar solo fechas pasadas
     */
    private val datePattern = "d-M-yyyy"
    fun convertStringToDate (date: String): LocalDate {
        val format = DateTimeFormatter.ofPattern(datePattern)
        return LocalDate.parse(date, format)
    }

    /**
     * Fecha que tiene un parametro fecha de tipo String.
     * Formatea el string pasado como argumnento a un patrón concreto (d-M-yyyy).
     * Devuelve un String con la fecha formateada.
     */
    fun convertStringToDateString(date: String): String {
        val format = DateTimeFormatter.ofPattern(datePattern)
        val dateFormatted = LocalDate.parse(date, format)
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(dateFormatted)
    }
}