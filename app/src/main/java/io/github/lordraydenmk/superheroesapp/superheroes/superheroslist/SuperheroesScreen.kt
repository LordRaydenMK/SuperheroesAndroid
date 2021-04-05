package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import io.github.lordraydenmk.superheroesapp.common.presentation.Screen
import io.github.lordraydenmk.superheroesapp.common.setTextResource
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroesScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import reactivecircus.flowbinding.android.view.clicks

class SuperheroesScreen(
    container: ViewGroup,
    lifecycleScope: CoroutineScope
) : Screen<SuperheroesAction, SuperheroesViewState> {

    private val binding =
        SuperheroesScreenBinding.inflate(LayoutInflater.from(container.context), container)

    private val superheroesAdapter = SuperheroesAdapter(lifecycleScope)

    init {
        with(binding.rvSuperheroes) {
            layoutManager = GridLayoutManager(context, 2)
            adapter = superheroesAdapter
        }
    }

    override val actions: Flow<SuperheroesAction> = merge(
        binding.tvError.clicks().flowOn(Dispatchers.Main).map { Refresh },
        superheroesAdapter.actions.map { LoadDetails(it) }
    )

    @Suppress("RedundantSuspendModifier") // updating the UI is a side effect
    override suspend fun bind(viewState: SuperheroesViewState) {
        binding.groupSuperheroesContent.isVisible = viewState is Content
        binding.progressSuperheroes.isVisible = viewState is Loading
        binding.tvError.isVisible = viewState is Problem

        when (viewState) {
            Loading -> {
                // no-op
            }
            is Content -> {
                superheroesAdapter.submitList(viewState.superheroes)
                binding.copyrightLayout.tvCopyright.text = viewState.copyright
            }
            is Problem -> bindErrorView(viewState)
        }
    }

    private fun bindErrorView(viewState: Problem) = with(binding) {
        tvError.setTextResource(viewState.stringId)
        tvError.isClickable = viewState.isRecoverable
    }
}