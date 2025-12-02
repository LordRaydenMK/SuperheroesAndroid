package io.github.lordraydenmk.themoviedbapp.movies.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.lordraydenmk.themoviedbapp.common.ErrorTextRes
import io.github.lordraydenmk.themoviedbapp.common.IdTextRes
import io.github.lordraydenmk.themoviedbapp.common.TextRes

@Composable
fun MovieLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun MovieProblem(
    textRes: TextRes,
    onClick: (() -> Unit) = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("SuperheroProblem"), contentAlignment = Alignment.Center
    ) {
        when (textRes) {
            is ErrorTextRes -> Text(
                text = stringResource(id = textRes.id, stringResource(id = textRes.retryTextId)),
                modifier = Modifier.clickable(onClick = onClick),
                textAlign = TextAlign.Center
            )
            is IdTextRes -> Text(
                text = stringResource(id = textRes.id),
                textAlign = TextAlign.Center
            )
        }
    }
}