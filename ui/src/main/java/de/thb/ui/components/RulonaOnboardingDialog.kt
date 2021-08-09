package de.thb.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import de.thb.ui.R
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.util.state
import kotlinx.coroutines.launch

@Composable
fun RulonaOnboardingDialog(onDismissRequest: () -> Unit, onSkipClicked: () -> Unit) {
    val maxPages = 2
    var activePage by state { 0 }

    val isLastPage = remember(activePage) {
        activePage == (maxPages - 1)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(margin_large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RulonaOnboardingPager(maxPages, activePage, onActivePageChanged = {
                        if (it != activePage) {
                            activePage = it
                        }
                    })

                    if (!isLastPage) {
                        Button(onClick = {
                            val newActivePage = (activePage + 1)
                                .coerceIn(0, maxPages - 1) // we start at 0

                            activePage = newActivePage
                        }) {
                            Text(text = "Weiter")
                        }

                        Spacer(Modifier.height(margin_medium))

                        TextButton(onClick = onDismissRequest) {
                            Text(
                                text = "Überspringen",
                                style = MaterialTheme.typography.body1
                            )
                        }
                    } else {
                        Button(onClick = onDismissRequest) {
                            Text(text = "Starten")
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun RulonaOnboardingPager(maxPages: Int, activePage: Int = 0, onActivePageChanged: (Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = maxPages)
    val scope = rememberCoroutineScope()

    LaunchedEffect(activePage) {
        scope.launch { pagerState.scrollToPage(activePage) }
    }

    var latestCurrentPage by state { activePage }

    LaunchedEffect(latestCurrentPage) {
        // only call when distinct
        onActivePageChanged(latestCurrentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = margin_medium)
    ) {
        HorizontalPager(state = pagerState) { page ->
            latestCurrentPage =
                currentPage // fixes a bug that caused onActivePageChanged to be called twice

            when (page) {
                0 -> {
                    RulonaOnboardingLayout(
                        title = "Finde Orte und Regeln",
                        text = "Finde deinen Wohnort und sehe auf einen Blick, welche Regeln und Vorschriften für dich gelten.",
                        imageResId = R.drawable.onboarding_1,
                    )
                }
                1 -> {
                    RulonaOnboardingLayout(
                        title = "Plane deine Route",
                        text = "Komme sorgenfrei und sicher an dein Ziel. Rulona plant deine Route für dich und leitet dich um, um Risiko-Gebiete zu vermeiden.",
                        imageResId = R.drawable.onboarding_2,
                    )
                }
                else -> {
                    // we are in an invalid state, reset to page 0
                    scope.launch { pagerState.scrollToPage(0) }
                }
            }
        }

        Spacer(Modifier.height(margin_medium))

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = margin_medium),
        )
    }
}

@Composable
fun RulonaOnboardingImage(@DrawableRes drawableResId: Int) {
    Box(Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun RulonaOnboardingLayout(title: String, text: String, @DrawableRes imageResId: Int) {
    Column {
        RulonaOnboardingImage(imageResId)

        Spacer(Modifier.height(margin_medium))

        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(margin_medium))

        Text(
            style = MaterialTheme.typography.body1,
            text = text
        )
    }
}

@Preview
@Composable
fun RulonaOnboardingDialogLayoutPreview() {
    RulonaOnboardingLayout("Beispiel-Text", "Lorem Ipsum", R.drawable.onboarding_1)
}