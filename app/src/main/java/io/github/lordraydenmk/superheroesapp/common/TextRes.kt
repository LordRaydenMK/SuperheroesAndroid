package io.github.lordraydenmk.superheroesapp.common

import androidx.annotation.StringRes
import io.github.lordraydenmk.superheroesapp.R

sealed class TextRes

data class IdTextRes(@StringRes val id: Int) : TextRes()

data class ErrorTextRes(
    @StringRes val id: Int,
    @StringRes val retryTextId: Int = R.string.error_retry_text
) : TextRes()