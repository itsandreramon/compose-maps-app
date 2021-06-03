package de.thb.ui.components.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import de.thb.ui.theme.corner_size_medium
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium

@Composable
fun RulonaSearchItem(
    title: String,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onBookmarkClicked: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(corner_size_medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier
                .clickable { onClick() }
                .padding(margin_medium)
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .height(margin_large)
                    .fillMaxWidth()
            ) {
                Text(text = title)

                Image(
                    imageVector = if (isBookmarked) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Filled.BookmarkBorder
                    },
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onBookmarkClicked() }
                )
            }
        }
    }
}

@Composable
@Preview
fun RulonaSearchItemPreview() {
    RulonaSearchItem(
        title = "Berlin",
        isBookmarked = true,
        onClick = {},
        onBookmarkClicked = {}
    )
}
