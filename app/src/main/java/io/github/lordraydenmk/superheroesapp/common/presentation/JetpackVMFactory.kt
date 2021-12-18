package io.github.lordraydenmk.superheroesapp.common.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class JetpackVMFactory<VS : Any, E : Any>(
    private val initialState: VS
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        JetpackViewModel<VS, E>(initialState) as T
}