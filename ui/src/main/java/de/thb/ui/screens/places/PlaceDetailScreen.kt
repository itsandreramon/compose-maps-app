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
import de.thb.core.data.sources.categories.CategoriesRepsitory
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.place.PlaceEntity
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.places.RulonaFilterList
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.AddFilterUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.EditFiltersUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.RemoveFilterUseCase
import de.thb.ui.screens.places.PlaceDetailUiState.EditCategoriesUiState
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
        val categories: List<CategoryEntity> = listOf(),
    ) : PlaceDetailUiState()

    data class EditCategoriesUiState(
        val notAddedCategories: List<CategoryEntity> = listOf(),
        val addedCategories: List<CategoryEntity> = listOf(),
    ) : PlaceDetailUiState()
}

data class PlaceDetailsState(
    val placeId: String? = null,
    val uiState: PlaceDetailUiState = OverviewUiState(),
) : MavericksState

class PlaceDetailsViewModel(
    initialState: PlaceDetailsState,
) : MavericksViewModel<PlaceDetailsState>(initialState), KoinComponent {

    private val placesRepository by inject<PlacesRepository>()
    private val categoriesRepository by inject<CategoriesRepsitory>()

    init {
        onEach { state ->
            if (state.placeId != null) {
                placesRepository.getById(state.placeId).collect { place ->
                    when (val uiState = state.uiState) {
                        is OverviewUiState -> {
                            setState { copy(uiState = uiState.copy(place = place)) }
                        }
                        is EditCategoriesUiState -> {
                            // ...
                        }
                    }
                }
            }
        }

        stateFlow.combine(categoriesRepository.getAll()) { state, filters ->
            when (val uiState = state.uiState) {
                is OverviewUiState -> {
                    val addedFilters = filters
                        .filter { it.added == true }
                        .sortedBy { it.name }

                    setState { copy(uiState = uiState.copy(categories = addedFilters)) }
                }
                is EditCategoriesUiState -> {
                    val (addedFilters, notAddedFilters) = filters
                        .sortedBy { it.name }
                        .partition { it.added == true }

                    setState {
                        copy(
                            uiState = uiState.copy(
                                notAddedCategories = notAddedFilters,
                                addedCategories = addedFilters,
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
            is RemoveFilterUseCase -> removeFilter(useCase.category)
            is AddFilterUseCase -> addFilter(useCase.category)
        }
    }

    private fun setScreenEditState(editState: EditState) {
        when (editState) {
            is EditState.Editing -> setState { copy(uiState = EditCategoriesUiState()) }
            else -> setState { copy(uiState = OverviewUiState()) }
        }
    }

    private fun removeFilter(category: CategoryEntity) {
        viewModelScope.launch {
            val updatedFilter = category.copy(added = false)
            categoriesRepository.insert(updatedFilter)
        }
    }

    private fun addFilter(category: CategoryEntity) {
        viewModelScope.launch {
            val updatedFilter = category.copy(added = true)
            categoriesRepository.insert(updatedFilter)
        }
    }

    fun setPlaceUuid(uuid: String) {
        setState { copy(placeId = uuid) }
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
                categories = uiState.categories,
                onBackClicked = onBackClicked,
                onShareClicked = {},
                onNotifyClicked = {},
                onEditStateChanged = { editState ->
                    viewModel.action(EditFiltersUseCase(editState))
                }
            )
        }
        is EditCategoriesUiState -> {
            PlaceDetailsEditFilters(
                addedCategories = uiState.addedCategories,
                notAddedCategories = uiState.notAddedCategories,
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
    addedCategories: List<CategoryEntity>,
    notAddedCategories: List<CategoryEntity>,
    onBackClicked: () -> Unit,
    onFilterRemoved: (CategoryEntity) -> Unit,
    onFilterAdded: (CategoryEntity) -> Unit,
) {
    Column {
        RulonaAppBar(
            title = "Filter",
            back = Back(onBackClicked),
            actions = listOf()
        )
        Column(Modifier.padding(horizontal = margin_medium)) {
            AnimatedVisibility(addedCategories.isNotEmpty()) {
                Column {
                    RulonaFilterList(
                        title = "Meine Filter",
                        categories = addedCategories,
                        isEditable = false,
                        editState = EditState.Editing(),
                        onEditStateChanged = {},
                        onRemoveClicked = onFilterRemoved,
                    )

                    Spacer(modifier = Modifier.padding(top = margin_large))
                }
            }

            AnimatedVisibility(notAddedCategories.isNotEmpty()) {
                RulonaFilterList(
                    title = "Alle Kategorien",
                    categories = notAddedCategories,
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
    categories: List<CategoryEntity>,
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
                    categories = categories,
                    onEditStateChanged = onEditStateChanged
                )
            }
        }
    }
}
