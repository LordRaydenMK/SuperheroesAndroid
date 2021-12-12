package io.github.lordraydenmk.superheroesapp.superheroes.superherodetails

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import io.github.lordraydenmk.superheroesapp.appModule
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.presentation.Screen
import io.github.lordraydenmk.superheroesapp.superheroes.domain.SuperheroId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class SuperheroDetailsScreen(
    container: ViewGroup,
    private val superheroId: SuperheroId
) : Screen<SuperheroDetailsAction, SuperheroDetailsViewState> {

    private val composeView = ComposeView(container.context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    }
    private val imageLoader: ImageLoader = container.context.appModule()

    private val _state: MutableStateFlow<SuperheroDetailsViewState> = MutableStateFlow(Loading)

    private val _actions = Channel<SuperheroDetailsAction>(Channel.UNLIMITED)
    override val actions: Flow<SuperheroDetailsAction> = _actions.receiveAsFlow()

    init {
        container.addView(composeView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        composeView.setContent {
            CompositionLocalProvider(LocalImageLoader provides imageLoader) {
                SuperheroDetailsScreen(_state, superheroId, _actions)
            }
        }
    }

    override suspend fun bind(viewState: SuperheroDetailsViewState) {
        _state.value = viewState
    }
}

@Composable
fun SuperheroDetailsScreen(
    stateFlow: StateFlow<SuperheroDetailsViewState>,
    superheroId: SuperheroId,
    actions: Channel<SuperheroDetailsAction>
) {
    val state by stateFlow.collectAsState()

    Column {
        TopAppBar(
            title = {
                Text(
                    text = when (val viewState = state) {
                        is Content -> viewState.superhero.name
                        else -> ""
                    }
                )
            },
            navigationIcon = {
                IconButton(onClick = { actions.trySend(Up).getOrThrow() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "")
                }
            }
        )

        when (val viewState = state) {
            is Content -> SuperheroContent(content = viewState)
            Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Center) {
                CircularProgressIndicator()
            }
            is Problem -> SuperheroProblem(problem = viewState) {
                actions.trySend(Refresh(superheroId))
            }
        }
    }
}

@Composable
fun SuperheroContent(content: Content) {
    val imageLoader = LocalImageLoader.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .testTag("SuperheroDetailsContent")
    ) {
        Image(
            painter = rememberImagePainter(content.superhero.thumbnail, imageLoader),
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
        Text(
            text = content.attribution,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        )
    }
}

@Composable
fun SuperheroProblem(problem: Problem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("SuperheroDetailsProblem"), contentAlignment = Center
    ) {
        val clickable = if (problem.isRecoverable) Modifier.clickable { onClick() }
        else Modifier
        when (val stringId = problem.stringId) {
            is ErrorTextRes -> Text(
                text = stringResource(
                    id = stringId.id,
                    stringResource(id = stringId.retryTextId),
                ),
                modifier = clickable,
                textAlign = TextAlign.Center
            )
            is IdTextRes -> Text(
                text = stringResource(id = stringId.id),
                modifier = clickable,
                textAlign = TextAlign.Center
            )
        }
    }
}