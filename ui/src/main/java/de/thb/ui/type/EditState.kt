package de.thb.ui.type

sealed class EditState(val isInEditMode: Boolean) {
    class Editing : EditState(true)
    class Done : EditState(false)
    class Adding : EditState(false)
}
