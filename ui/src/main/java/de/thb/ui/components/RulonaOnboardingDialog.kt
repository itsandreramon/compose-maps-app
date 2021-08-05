package de.thb.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium

@Composable
fun RulonaOnboardingDialog(onDismissRequest: () -> Unit, onSkipClicked: () -> Unit) {
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
                    Text(
                        text = "Alles auf einen Blick",
                        style = MaterialTheme.typography.h5
                    )

                    Spacer(modifier = Modifier.height(margin_medium))

                    Text(
                        style = MaterialTheme.typography.body1,
                        text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod"
                    )

                    Spacer(modifier = Modifier.height(margin_large.times(2)))

                    Button(onClick = {}) {
                        Text(text = "Weiter")
                    }

                    Spacer(modifier = Modifier.height(margin_medium))

                    TextButton(onClick = onSkipClicked) {
                        Text(
                            text = "Überspringen",
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    )
}