package de.thb.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import de.thb.ui.components.ScreenTitle

@Composable
fun ScreenTwo(onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        ScreenTitle(title = "Two")
        Button(onClick = onButtonClick) {
            Text("Navigate")
        }
    }
}
