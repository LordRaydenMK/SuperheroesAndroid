package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.CopyrightView
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.MovieLoading
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.MovieProblem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

@Composable
fun MovieDetailsScreen(
    stateFlow: Flow<MovieDetailsViewState>,
    initialState: MovieDetailsViewState,
    movieId: MovieId,
    actions: Channel<MovieDetailsAction>
) {
    val state by stateFlow.collectAsStateWithLifecycle(initialState)

    Column {
        MovieDetailsAppBar(state, actions)

        when (val viewState = state) {
            is Content -> MovieContent(content = viewState)
            Loading -> MovieLoading()
            is Problem -> MovieProblem(textRes = viewState.stringId) {
                actions.trySend(Refresh(movieId))
            }
        }
    }
}

@Composable
private fun MovieDetailsAppBar(
    state: MovieDetailsViewState,
    actions: Channel<MovieDetailsAction>
) {
    TopAppBar(
        title = { Text(text = state.title) },
        navigationIcon = {
            IconButton(onClick = { actions.trySend(Up).getOrThrow() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
            }
        }
    )
}

@Composable
fun MovieContent(content: Content) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .testTag("MovieDetailsContent")
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
        Spacer(modifier = Modifier.weight(1f))
        CopyrightView(text = content.attribution)
    }
}