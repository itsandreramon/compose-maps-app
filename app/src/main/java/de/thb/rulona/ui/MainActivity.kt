package de.thb.rulona.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import com.google.accompanist.insets.ProvideWindowInsets
import de.thb.ui.theme.RulonaTheme
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.util.state

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDecorFitsSystemWindows(window, false)

        setContent {
            RulonaTheme {
                var dialogVisible by state { true }

                LaunchedEffect(dialogVisible) {
                    if (dialogVisible) {
                        // TODO save onboarding has been seen
                    }
                }

                if (dialogVisible) {
                    Dialog(
                        onDismissRequest = { dialogVisible = false },
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

                                    TextButton(onClick = { dialogVisible = false }) {
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

                ProvideWindowInsets {
                    AppContainer()
                }
            }
        }
    }
}
