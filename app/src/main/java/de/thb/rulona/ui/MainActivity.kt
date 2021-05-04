package de.thb.rulona.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import de.thb.ui.theme.RulonaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RulonaTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Text("Hello, world!")
                }
            }
        }
    }
}