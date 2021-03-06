package io.github.lordraydenmk.superheroesapp.superheroes.superheroslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import io.github.lordraydenmk.superheroesapp.common.presentation.Screen
import io.github.lordraydenmk.superheroesapp.common.setTextResource
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroesScreenBinding
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.asObservable
import kotlinx.coroutines.rx2.rxCompletable
import kotlinx.coroutines.withContext
import reactivecircus.flowbinding.android.view.clicks

class SuperheroesScreen(container: ViewGroup) : Screen<SuperheroesAction, SuperheroesViewState> {

    private val binding =
        SuperheroesScreenBinding.inflate(LayoutInflater.from(container.context), container)

    private val superheroesAdapter = SuperheroesAdapter()

    init {
        with(binding.rvSuperheroes) {
            layoutManager = GridLayoutManager(context, 2)
            adapter = superheroesAdapter
        }
    }

    override val actions: Observable<SuperheroesAction> = merge(
        binding.tvError.clicks().flowOn(Dispatchers.Main).map { Refresh },
        superheroesAdapter.actions.asFlow().map { LoadDetails(it) }
    ).asObservable()

    override fun bind(viewState: SuperheroesViewState): Completable =
        rxCompletable { bindX(viewState) }

    @Suppress("RedundantSuspendModifier") // updating the UI is a side effect
    private suspend fun bindX(viewState: SuperheroesViewState) = withContext(Dispatchers.Main) {
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