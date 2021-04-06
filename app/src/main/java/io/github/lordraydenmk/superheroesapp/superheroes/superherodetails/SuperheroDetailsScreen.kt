package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import coil.load
import io.github.lordraydenmk.superheroesapp.R
import io.github.lordraydenmk.superheroesapp.common.presentation.Screen
import io.github.lordraydenmk.superheroesapp.common.setTextResource
import io.github.lordraydenmk.superheroesapp.databinding.SuperheroDetailsScreenBinding
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.appcompat.navigationClicks

class SuperheroDetailsScreen(
    container: ViewGroup,
    superheroId: SuperheroId
) : Screen<SuperheroDetailsAction, SuperheroDetailsViewState> {

    private val binding =
        SuperheroDetailsScreenBinding.inflate(LayoutInflater.from(container.context), container)

    override val actions: Flow<SuperheroDetailsAction> = merge(
        binding.toolbar.navigationClicks().map { Up },
        binding.superheroContent.tvError.clicks()
            .map { Refresh(superheroId) }
    )

    override suspend fun bind(viewState: SuperheroDetailsViewState) {
        with(binding.superheroContent) {
            progress.isVisible = viewState is Loading
            tvError.isVisible = viewState is Problem
            layoutContent.isVisible = viewState is Content
            binding.copyrightLayout.tvCopyright.isVisible = viewState is Content

            when (viewState) {
                Loading -> Unit
                is Content -> bindContent(viewState)
                is Problem -> bindError(viewState)
            }
        }
    }

    private fun bindContent(viewState: Content) = with(binding) {
        val superhero = viewState.superhero
        toolbar.title = superhero.name
        imgSuperhero.load(superhero.thumbnail) {
            placeholder(R.drawable.ic_hourglass_bottom_black)
            error(R.drawable.ic_baseline_broken_image)
            crossfade(true)
        }
        imgSuperhero.contentDescription = superhero.name
        with(superheroContent) {
            val resources = tvComicsCount.resources
            tvComicsCount.text = superhero.comics.string(resources)
            tvStoriesCount.text = superhero.stories.string(resources)
            tvEventsCount.text = superhero.events.string(resources)
            tvSeriesCount.text = superhero.series.string(resources)
        }
        copyrightLayout.tvCopyright.text = viewState.attribution
    }

    private fun bindError(problem: Problem) = with(binding.superheroContent) {
        tvError.setTextResource(problem.stringId)
        tvError.isClickable = problem.isRecoverable
    }
}