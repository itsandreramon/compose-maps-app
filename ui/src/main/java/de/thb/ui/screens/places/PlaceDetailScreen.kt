package de.thb.ui.screens.places

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import de.thb.core.data.filters.local.FiltersLocalDataSource
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.domain.FilterEntity
import de.thb.core.domain.PlaceEntity
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.places.RulonaFilterList
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.AddFilterUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.EditFiltersUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.RemoveFilterUseCase
import de.thb.ui.screens.places.PlaceDetailUiState.EditFiltersUiState
import de.thb.ui.screens.places.PlaceDetailUiState.OverviewUiState
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_material_red_600
import de.thb.ui.type.EditState
import de.thb.ui.type.RulonaAppBarAction.Back
import de.thb.ui.type.RulonaAppBarAction.Notify
import de.thb.ui.type.RulonaAppBarAction.Share
import de.thb.ui.util.setStatusBarIconColorInSideEffect
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class PlaceDetailUiState {
    data class OverviewUiState(
        val place: PlaceEntity? = null,
        val filters: List<FilterEntity> = listOf(),
    ) : PlaceDetailUiState()

    data class EditFiltersUiState(
        val notAddedFilters: List<FilterEntity> = listOf(),
        val addedFilters: List<FilterEntity> = listOf(),
    ) : PlaceDetailUiState()
}

data class PlaceDetailsState(
    val placeUuid: String? = null,
    val uiState: PlaceDetailUiState = OverviewUiState(),
) : MavericksState

class PlaceDetailsViewModel(
    initialState: PlaceDetailsState,
) : MavericksViewModel<PlaceDetailsState>(initialState), KoinComponent {

    private val placesLocalDataSource by inject<PlacesLocalDataSource>()
    private val filtersLocalDataSource by inject<FiltersLocalDataSource>()

    init {
        onEach { state ->
            if (state.placeUuid != null) {
                placesLocalDataSource.getByUuid(state.placeUuid).collect { place ->
                    when (val uiState = state.uiState) {
                        is OverviewUiState -> {
                            setState { copy(uiState = uiState.copy(place = place)) }
                        }
                        is EditFiltersUiState -> {
                            // ...
                        }
                    }
                }
            }
        }

        stateFlow.combine(filtersLocalDataSource.getAll()) { state, filters ->
            when (val uiState = state.uiState) {
                is OverviewUiState -> {
                    val addedFilters = filters
                        .filter { it.added == true }
                        .sortedBy { it.name }

                    setState { copy(uiState = uiState.copy(filters = addedFilters)) }
                }
                is EditFiltersUiState -> {
                    val (addedFilters, notAddedFilters) = filters
                        .sortedBy { it.name }
                        .partition { it.added == true }

                    setState {
                        copy(
                            uiState = uiState.copy(
                                notAddedFilters = notAddedFilters,
                                addedFilters = addedFilters,
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: PlaceDetailScreenUseCase) {
        when (useCase) {
            is EditFiltersUseCase -> setScreenEditState(useCase.editState)
            is RemoveFilterUseCase -> removeFilter(useCase.filter)
            is AddFilterUseCase -> addFilter(useCase.filter)
        }
    }

    private fun setScreenEditState(editState: EditState) {
        when (editState) {
            is EditState.Editing -> setState { copy(uiState = EditFiltersUiState()) }
            else -> setState { copy(uiState = OverviewUiState()) }
        }
    }

    private fun removeFilter(filter: FilterEntity) {
        viewModelScope.launch {
            val updatedFilter = filter.copy(added = false)
            filtersLocalDataSource.insert(updatedFilter)
        }
    }

    private fun addFilter(filter: FilterEntity) {
        viewModelScope.launch {
            val updatedFilter = filter.copy(added = true)
            filtersLocalDataSource.insert(updatedFilter)
        }
    }

    fun setPlaceUuid(uuid: String) {
        setState { copy(placeUuid = uuid) }
    }
}

@Composable
fun PlaceDetailsScreen(
    placeUuid: String,
    viewModel: PlaceDetailsViewModel = mavericksViewModel(),
    onBackClicked: () -> Unit,
) {
    val placeDetailUiState = viewModel.collectAsState(PlaceDetailsState::uiState)

    LaunchedEffect(placeUuid) {
        viewModel.setPlaceUuid(placeUuid)
    }

    setStatusBarIconColorInSideEffect(darkIcons = false)

    when (val uiState = placeDetailUiState.value) {
        is OverviewUiState -> {
            PlaceDetailsOverview(
                place = uiState.place,
                filters = uiState.filters,
                onBackClicked = onBackClicked,
                onShareClicked = {},
                onNotifyClicked = {},
                onEditStateChanged = { editState ->
                    viewModel.action(EditFiltersUseCase(editState))
                }
            )
        }
        is EditFiltersUiState -> {
            PlaceDetailsEditFilters(
                addedFilters = uiState.addedFilters,
                notAddedFilters = uiState.notAddedFilters,
                onBackClicked = {
                    viewModel.action(EditFiltersUseCase(EditState.Done()))
                },
                onFilterRemoved = { filter ->
                    viewModel.action(RemoveFilterUseCase(filter))
                },
                onFilterAdded = { filter ->
                    viewModel.action(AddFilterUseCase(filter))
                }
            )
        }
    }
}

@Composable
fun PlaceDetailsEditFilters(
    addedFilters: List<FilterEntity>,
    notAddedFilters: List<FilterEntity>,
    onBackClicked: () -> Unit,
    onFilterRemoved: (FilterEntity) -> Unit,
    onFilterAdded: (FilterEntity) -> Unit,
) {
    Column {
        RulonaAppBar(
            title = "Filter",
            back = Back(onBackClicked),
            actions = listOf()
        )
        Column(Modifier.padding(horizontal = margin_medium)) {
            AnimatedVisibility(addedFilters.isNotEmpty()) {
                Column {
                    RulonaFilterList(
                        title = "Meine Filter",
                        filters = addedFilters,
                        isEditable = false,
                        editState = EditState.Editing(),
                        onEditStateChanged = {},
                        onRemoveClicked = onFilterRemoved,
                    )

                    Spacer(modifier = Modifier.padding(top = margin_large))
                }
            }

            AnimatedVisibility(notAddedFilters.isNotEmpty()) {
                RulonaFilterList(
                    title = "Alle Kategorien",
                    filters = notAddedFilters,
                    isEditable = false,
                    editState = EditState.Adding(),
                    onEditStateChanged = {},
                    onRemoveClicked = onFilterRemoved,
                    onAddClicked = onFilterAdded,
                )
            }
        }
    }
}

@Composable
fun PlaceDetailsOverview(
    place: PlaceEntity?,
    filters: List<FilterEntity>,
    onBackClicked: () -> Unit,
    onNotifyClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onEditStateChanged: (EditState) -> Unit,
) {
    if (place != null) {
        Column {
            RulonaAppBar(
                title = place.name,
                back = Back(onBackClicked),
                actions = listOf(
                    Notify(onNotifyClicked),
                    Share(onShareClicked)
                )
            )
            Column(
                modifier = Modifier
                    .padding(top = margin_large)
                    .padding(horizontal = margin_medium)
            ) {
                Column(Modifier.padding(margin_medium)) {
                    Row {
                        Text(text = "Inzidenz")

                        Image(
                            imageVector = Icons.Default.ArrowDropUp,
                            colorFilter = ColorFilter.tint(rulona_material_red_600),
                            contentDescription = null
                        )

                        Text(text = "${place.incidence}")
                    }

                    Text(
                        text = buildAnnotatedString {
                            append("Die offiziellen Regeln für ${place.name} lassen sich ")
                            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append("hier")
                            }
                            append(" einsehen.")
                        },
                        modifier = Modifier.padding(top = margin_medium)
                    )
                }

                RulonaFilterList(
                    title = "Meine Filter",
                    filters = filters,
                    onEditStateChanged = onEditStateChanged
                )
            }
        }
    }
}
