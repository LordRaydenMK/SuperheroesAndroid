package io.github.lordraydenmk.superheroesapp.superheroes.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.github.lordraydenmk.superheroesapp.common.ErrorTextRes
import io.github.lordraydenmk.superheroesapp.common.IdTextRes
import io.github.lordraydenmk.superheroesapp.common.TextRes

@Composable
fun SuperherosLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun SuperheroProblem(
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

@Composable
fun CopyrightView(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    )
}