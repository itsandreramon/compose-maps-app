package de.thb.ui.type

enum class EditState(val isInEditMode: Boolean) {
    Editing(true), Done(false)
}

fun toggleEditState(editState: EditState): EditState {
    return if (editState == EditState.Done) {
        EditState.Editing
    } else {
        EditState.Done
    }
}