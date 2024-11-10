package io.github.lordraydenmk.superheroesapp.common

import android.content.res.Resources
import androidx.annotation.StringRes

data class PlaceholderString<A>(@StringRes val stringId: Int, val replacement: A) {

    fun string(res: Resources): String = res.getString(stringId, replacement)
}