package io.github.lordraydenmk.themoviedbapp.movies.moviedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.MovieLoading
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.MovieProblem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import okhttp3.HttpUrl.Companion.toHttpUrl

@OptIn(ExperimentalMaterial3Api::class)
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

@ExperimentalMaterial3Api
@Composable
private fun MovieDetailsAppBar(
    state: MovieDetailsViewState,
    actions: Channel<MovieDetailsAction>
) {
    TopAppBar(
        title = { Text(text = state.title) },
        modifier = Modifier.statusBarsPadding(),
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("MovieDetailsContent"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = content.movie.thumbnail.toString(),
            contentScale = ContentScale.Crop,
            contentDescription = content.movie.name,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
        )

        Text(
            text = content.movie.name,
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = content.movie.overview,
            style = MaterialTheme.typography.bodyLarge
        )
        RatingIndicator(
            voteAverage = content.movie.voteAverage,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun RatingIndicator(voteAverage: VoteAverage, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(64.dp)
    ) {
        CircularProgressIndicator(
            progress = { voteAverage.progress },
            modifier = Modifier.fillMaxSize(),
            strokeWidth = 4.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        Text(
            text = voteAverage.text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
private fun MovieDetailsPreview() {
    val movie = MovieDetailsViewEntity(
        "Awesome Moview",
        "This is some awesome moview overview. Bla bla bka",
        VoteAverage(0.745f, "7.5"),
        "https://image.tmdb.org/t/p/w500".toHttpUrl(),
    )
    MovieContent(Content(movie))
}
