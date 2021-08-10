package de.thb.rulona.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import com.google.accompanist.insets.ProvideWindowInsets
import de.thb.core.prefs.PrefsStore
import de.thb.ui.components.RulonaOnboardingDialog
import de.thb.ui.theme.RulonaTheme
import de.thb.ui.util.state
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val prefsStore: PrefsStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDecorFitsSystemWindows(window, false)

        setContent {
            RulonaTheme {
                var dialogVisible by state { false }

                LaunchedEffect(dialogVisible) {
                    if (dialogVisible) {
                        prefsStore.setHasSeenOnboarding(true)
                    } else {
                        dialogVisible = !prefsStore.getHasSeenOnboarding().first()
                    }
                }

                if (dialogVisible) {
                    RulonaOnboardingDialog(onDismissRequest = { dialogVisible = false })
                }

                ProvideWindowInsets {
                    AppContainer()
                }
            }
        }
    }
}
