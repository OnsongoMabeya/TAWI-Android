package dev.mabeya.tawi.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.mabeya.tawi.R
import dev.mabeya.tawi.ui.theme.TAWITheme

/**
 * Test tags for the Phase 1 placeholder screen, shared between the screen and its UI tests so the
 * two cannot drift apart.
 */
object PlaceholderScreenTags {
    const val ROOT = "placeholder_root"
    const val HEADLINE = "placeholder_headline"
    const val TAGLINE = "placeholder_tagline"
}

/**
 * Phase 1 placeholder screen. It exists only to prove the Kotlin/Compose toolchain builds and the
 * app launches; the real Listener experience (live captions, waveform, phoneme highlighting)
 * arrives in later phases.
 */
@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag(PlaceholderScreenTags.ROOT),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.placeholder_headline),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(PlaceholderScreenTags.HEADLINE)
        )
        Text(
            text = stringResource(R.string.placeholder_tagline),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 12.dp)
                .testTag(PlaceholderScreenTags.TAGLINE)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    TAWITheme {
        PlaceholderScreen()
    }
}
