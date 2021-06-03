package de.thb.ui.components

import androidx.compose.foundation.background
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import de.thb.ui.type.RulonaAppBarAction

@Composable
fun RulonaAppBar(
    title: String,
    back: RulonaAppBarAction.Back? = null,
    actions: List<RulonaAppBarAction> = listOf()
) {
    Surface(elevation = 4.dp) {
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .background(MaterialTheme.colors.background)
        ) {
            TopAppBar(
                title = { Text(title) },
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                navigationIcon = {
                    if (back != null) {
                        IconButton(onClick = back.onClick) {
                            Icon(back.icon, back.contentDescription)
                        }
                    }
                },
                actions = {
                    for (action in actions) {
                        IconButton(onClick = action.onClick) {
                            Icon(action.icon, action.contentDescription)
                        }
                    }
                }
            )
        }
    }
}
