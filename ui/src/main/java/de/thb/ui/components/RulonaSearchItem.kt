package de.thb.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp

@Composable
fun RulonaSearchItem(
    title: String,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onBookmarkClicked: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier
                .clickable { onClick() }
                .padding(16.dp)
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
