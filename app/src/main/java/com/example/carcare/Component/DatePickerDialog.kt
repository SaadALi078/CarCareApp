package com.example.carcare.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import java.util.*

@SuppressLint("SimpleDateFormat")
fun showDatePickerDialog(
    context: Context,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            onDateSelected(calendar.time)
        },
        year,
        month,
        day
    )

    datePickerDialog.setOnDismissListener { onDismiss() }
    datePickerDialog.show()
}