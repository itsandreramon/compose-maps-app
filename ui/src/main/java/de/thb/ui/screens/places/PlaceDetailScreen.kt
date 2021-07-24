package de.thb.ui.screens.places

import android.util.Log
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
import androidx.compose.runtime.SideEffect
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
import de.thb.ui.components.places.RulonaCategoriesList
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.AddCategoryUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.EditCategoriesUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.RemoveCategoryUseCase
import de.thb.ui.screens.places.PlaceDetailUiState.EditCategoriesUiState
import de.thb.ui.screens.places.PlaceDetailUiState.OverviewUiState
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_material_red_600
import de.thb.ui.type.EditState
import de.thb.ui.type.RulonaAppBarAction.Back
import de.thb.ui.type.RulonaAppBarAction.Notify
import de.thb.ui.type.RulonaAppBarAction.Share
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    val uiState: PlaceDetailUiState = OverviewUiState(),
) : MavericksState

class PlaceDetailsViewModel(
    initialState: PlaceDetailsState,
) : MavericksViewModel<PlaceDetailsState>(initialState), KoinComponent {

    companion object {
        const val TAG = "PlaceDetailsViewModel"
    }

    private val placesRepository by inject<PlacesRepository>()
    private val categoriesRepository by inject<CategoriesRepsitory>()

    init {
        stateFlow.combine(categoriesRepository.getAll()) { state, categories ->
            when (val uiState = state.uiState) {
                is OverviewUiState -> {
                    val addedCategories = categories
                        .filter { it.added == true }
                        .sortedBy { it.name }

                    if (uiState.place != null) {
                        setState { copy(uiState = uiState.copy(categories = addedCategories)) }
                    }
                }
                is EditCategoriesUiState -> {
                    val (addedCategories, notAddedCategories) = categories
                        .sortedBy { it.name }
                        .partition { it.added == true }

                    setState {
                        copy(
                            uiState = uiState.copy(
                                notAddedCategories = notAddedCategories,
                                addedCategories = addedCategories,
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: PlaceDetailScreenUseCase) {
        when (useCase) {
            is EditCategoriesUseCase -> setScreenEditState(useCase.editState)
            is RemoveCategoryUseCase -> removeCategory(useCase.category)
            is AddCategoryUseCase -> addCategory(useCase.category)
        }
    }

    private fun setScreenEditState(editState: EditState) {
        when (editState) {
            is EditState.Editing -> setState { copy(uiState = EditCategoriesUiState()) }
            else -> setState { copy(uiState = OverviewUiState()) }
        }
    }

    private fun removeCategory(category: CategoryEntity) {
        viewModelScope.launch {
            val updatedCategory = category.copy(added = false)
            categoriesRepository.insert(updatedCategory)
        }
    }

    private fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            val updatedCategory = category.copy(added = true)
            categoriesRepository.insert(updatedCategory)
        }
    }

    fun loadPlace(id: String) {
        placesRepository.getById(id)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { place ->
                withState { state ->
                    when (val uiState = state.uiState) {
                        is OverviewUiState -> {
                            setState { copy(uiState = uiState.copy(place = place)) }
                        }
                        is EditCategoriesUiState -> {
                            // ...
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}

@Composable
fun PlaceDetailsScreen(
    placeId: String,
    viewModel: PlaceDetailsViewModel = mavericksViewModel(),
    onBackClicked: () -> Unit,
) {
    Log.e("Recomposition", "PlaceDetailsScreen")

    val placeDetailUiState = viewModel.collectAsState(PlaceDetailsState::uiState)

    when (val uiState = placeDetailUiState.value) {
        is OverviewUiState -> {
            if (uiState.place == null) {
                // try reloading if place is set to null
                SideEffect { viewModel.loadPlace(placeId) }
            }

            PlaceDetailsOverview(
                place = uiState.place,
                categories = uiState.categories,
                onBackClicked = onBackClicked,
                onShareClicked = {},
                onNotifyClicked = {},
                onEditStateChanged = { editState ->
                    viewModel.action(EditCategoriesUseCase(editState))
                }
            )
        }
        is EditCategoriesUiState -> {
            PlaceDetailsEditCategories(
                addedCategories = uiState.addedCategories,
                notAddedCategories = uiState.notAddedCategories,
                onBackClicked = {
                    viewModel.action(EditCategoriesUseCase(EditState.Done()))
                },
                onCategoryRemoved = { category ->
                    viewModel.action(RemoveCategoryUseCase(category))
                },
                onCategoryAdded = { category ->
                    viewModel.action(AddCategoryUseCase(category))
                }
            )
        }
    }
}

@Composable
fun PlaceDetailsEditCategories(
    addedCategories: List<CategoryEntity>,
    notAddedCategories: List<CategoryEntity>,
    onBackClicked: () -> Unit,
    onCategoryRemoved: (CategoryEntity) -> Unit,
    onCategoryAdded: (CategoryEntity) -> Unit,
) {
    Column {
        RulonaAppBar(
            title = "Kategorien",
            back = Back(onBackClicked),
            actions = listOf()
        )
        Column(Modifier.padding(horizontal = margin_medium)) {
            AnimatedVisibility(addedCategories.isNotEmpty()) {
                Column {
                    RulonaCategoriesList(
                        title = "Meine Kategorien",
                        categories = addedCategories,
                        isEditable = false,
                        editState = EditState.Editing(),
                        onEditStateChanged = {},
                        onRemoveClicked = onCategoryRemoved,
                    )

                    Spacer(modifier = Modifier.padding(top = margin_large))
                }
            }

            AnimatedVisibility(notAddedCategories.isNotEmpty()) {
                RulonaCategoriesList(
                    title = "Alle Kategorien",
                    categories = notAddedCategories,
                    isEditable = false,
                    editState = EditState.Adding(),
                    onEditStateChanged = {},
                    onRemoveClicked = onCategoryRemoved,
                    onAddClicked = onCategoryAdded,
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

                RulonaCategoriesList(
                    title = "Meine Kategorien",
                    categories = categories,
                    onEditStateChanged = onEditStateChanged
                )
            }
        }
    }
}
