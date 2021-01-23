package io.github.lordraydenmk.superheroesapp.common

import android.widget.TextView
import androidx.annotation.StringRes
import io.github.lordraydenmk.superheroesapp.R

sealed class TextRes

data class IdTextRes(@StringRes val id: Int) : TextRes()

data class ErrorTextRes(
    @StringRes val id: Int,
    @StringRes val retryTextId: Int = R.string.error_retry_text
) : TextRes()

fun TextView.setTextResource(textRes: TextRes) =
    when (textRes) {
        is IdTextRes -> setText(textRes.id)
        is ErrorTextRes ->
            setText(resources.getString(textRes.id, resources.getString(textRes.retryTextId)))
    }
