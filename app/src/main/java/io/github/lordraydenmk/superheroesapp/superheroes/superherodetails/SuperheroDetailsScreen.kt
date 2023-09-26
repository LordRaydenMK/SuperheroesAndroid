package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import io.github.lordraydenmk.superheroesapp.superheroes.ui.common.CopyrightView
import io.github.lordraydenmk.superheroesapp.superheroes.ui.common.SuperheroProblem
import io.github.lordraydenmk.superheroesapp.superheroes.ui.common.SuperherosLoading
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

@Composable
fun SuperheroDetailsScreen(
    stateFlow: Flow<SuperheroDetailsViewState>,
    initialState: SuperheroDetailsViewState,
    superheroId: SuperheroId,
    actions: Channel<SuperheroDetailsAction>
) {
    val state by stateFlow.collectAsStateWithLifecycle(initialState)

    Column {
        SuperheroAppBar(state, actions)

        when (val viewState = state) {
            is Content -> SuperheroContent(content = viewState)
            Loading -> SuperherosLoading()
            is Problem -> SuperheroProblem(textRes = viewState.stringId) {
                actions.trySend(Refresh(superheroId))
            }
        }
    }
}

@Composable
private fun SuperheroAppBar(
    state: SuperheroDetailsViewState,
    actions: Channel<SuperheroDetailsAction>
) {
    TopAppBar(
        title = { Text(text = state.title) },
        navigationIcon = {
            IconButton(onClick = { actions.trySend(Up).getOrThrow() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "")
            }
        }
    )
}

@Composable
fun SuperheroContent(content: Content) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .testTag("SuperheroDetailsContent")
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(content.superhero.thumbnail)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = content.superhero.name,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
        )
        Text(
            text = stringResource(
                id = content.superhero.comics.stringId,
                content.superhero.comics.replacement
            )
        )
        Text(
            text = stringResource(
                id = content.superhero.series.stringId,
                content.superhero.series.replacement
            )
        )
        Text(
            text = stringResource(
                id = content.superhero.events.stringId,
                content.superhero.events.replacement
            )
        )
        Text(
            text = stringResource(
                id = content.superhero.stories.stringId,
                content.superhero.stories.replacement
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        CopyrightView(text = content.attribution)
    }
}