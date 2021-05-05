package de.thb.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import de.thb.ui.components.ScreenTitle

data class ScreenOneState(
    val count: Int = 0
) : MavericksState

class ScreenOneViewModel(
    initialState: ScreenOneState
) : MavericksViewModel<ScreenOneState>(initialState) {

    fun increment() = setState {
        copy(count = count + 1)
    }
}

@Composable
fun ScreenOne() {
    val viewModel: ScreenOneViewModel = mavericksViewModel()
    val count by viewModel.collectAsState(ScreenOneState::count)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        ScreenTitle(title = "One")

        Button(onClick = viewModel::increment) {
            Text("Click")
        }

        Spacer(Modifier.padding(vertical = 16.dp))

        Text("Clicked $count times")
    }
}
