package de.thb.ui.type

sealed class EditState(val isInEditMode: Boolean) {
    class Editing : EditState(true)
    class Done : EditState(false)
}

fun toggleEditState(editState: EditState): EditState {
    return when (editState) {
        is EditState.Done -> EditState.Editing()
        else -> EditState.Done()
    }
}
