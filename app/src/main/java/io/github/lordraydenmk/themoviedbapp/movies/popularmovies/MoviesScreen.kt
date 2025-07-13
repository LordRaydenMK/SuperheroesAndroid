package io.github.lordraydenmk.themoviedbapp.movies.popularmovies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.lordraydenmk.themoviedbapp.R
import io.github.lordraydenmk.themoviedbapp.movies.domain.MovieId
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.CopyrightView
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.MovieLoading
import io.github.lordraydenmk.themoviedbapp.movies.ui.common.MovieProblem
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

@Composable
fun SuperheroesScreen(
    stateFlow: Flow<SuperheroesViewState>,
    initialValue: SuperheroesViewState,
    actions: Channel<MoviesAction>
) {
    val state by stateFlow.collectAsStateWithLifecycle(initialValue)
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) })
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
            when (val s = state) {
                Loading -> MovieLoading()
                is Content -> Content(s) { actions.trySend(LoadDetails(it)) }
                is Problem -> MovieProblem(s.stringId) { actions.trySend(Refresh) }
            }
        }
    }
}

@Composable
private fun Content(content: Content, loadDetails: (MovieId) -> Unit) {
    Column(Modifier.testTag("SuperheroesContent")) {
        LazyVerticalGrid(GridCells.Adaptive(175.dp), Modifier.weight(1f)) {
            items(content.superheroes) {
                SuperHeroItem(it, loadDetails)
            }
        }
        CopyrightView(text = content.copyright)
    }
}

@Composable
fun SuperHeroItem(entity: SuperheroViewEntity, onClick: (MovieId) -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick(entity.id) }
            .aspectRatio(0.75f)
            .semantics(mergeDescendants = true) {},
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(entity.imageUrl)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.7f)),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = entity.name,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
