package de.thb.ui.screens.places

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import de.thb.core.data.sources.rules.RulesRepository
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.rule.RuleWithCategoryEntity
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.places.RulonaRulesList
import de.thb.ui.components.places.rulonaCategoriesList
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class PlaceDetailUiState {
    object OverviewUiState : PlaceDetailUiState()
    object EditCategoriesUiState : PlaceDetailUiState()
}

data class PlaceDetailsState(
    val place: PlaceEntity? = null,
    val rules: List<RuleWithCategoryEntity> = listOf(),
    val notAddedCategories: List<CategoryEntity> = listOf(),
    val addedCategories: List<CategoryEntity> = listOf(),
    val uiState: PlaceDetailUiState = OverviewUiState,
) : MavericksState

class PlaceDetailsViewModel(
    initialState: PlaceDetailsState,
) : MavericksViewModel<PlaceDetailsState>(initialState), KoinComponent {

    private var loadRulesJob: Job? = null
    private var loadPlacesJob: Job? = null

    private val placesRepository by inject<PlacesRepository>()
    private val categoriesRepository by inject<CategoriesRepsitory>()
    private val rulesRepository by inject<RulesRepository>()

    init {
        categoriesRepository.getAll()
            .onEach { categories ->
                val (addedCategories, notAddedCategories) = categories
                    .sortedBy { it.name }
                    .partition { it.added == true }

                setState {
                    copy(
                        notAddedCategories = notAddedCategories,
                        addedCategories = addedCategories
                    )
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
            is EditState.Editing -> setState { copy(uiState = EditCategoriesUiState) }
            else -> setState { copy(uiState = OverviewUiState) }
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

    fun loadPlace(placeId: String) {
        // make sure to only have a single job
        // active to update the state
        loadPlacesJob?.let { job ->
            if (!job.isCancelled) job.cancel()
        }

        loadPlacesJob = placesRepository.getById(placeId)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { place -> setState { copy(place = place) } }
            .launchIn(viewModelScope)
    }

    fun loadRules(placeId: String) {
        // make sure to only have a single job
        // active to update the state
        loadRulesJob?.let { job ->
            if (!job.isCancelled) job.cancel()
        }

        loadRulesJob = rulesRepository.getByPlaceId(placeId)
            .onEach { rules ->
                val addedCategories = rules
                    .filter { it.category.added == true }
                    .sortedBy { it.category.name }

                setState { copy(rules = addedCategories) }
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

    LaunchedEffect(placeId) {
        viewModel.loadPlace(placeId)
        viewModel.loadRules(placeId)
    }

    val place by viewModel.collectAsState(PlaceDetailsState::place)
    val rules by viewModel.collectAsState(PlaceDetailsState::rules)
    val addedCategories by viewModel.collectAsState(PlaceDetailsState::addedCategories)
    val notAddedCategories by viewModel.collectAsState(PlaceDetailsState::notAddedCategories)

    val placeDetailUiState = viewModel.collectAsState(PlaceDetailsState::uiState)

    when (val uiState = placeDetailUiState.value) {
        is OverviewUiState -> {
            PlaceDetailsOverview(
                place = place,
                rules = rules,
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
                addedCategories = addedCategories,
                notAddedCategories = notAddedCategories,
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

        LazyColumn {
            if (addedCategories.isNotEmpty()) {
                rulonaCategoriesList(
                    title = "Meine Kategorien",
                    categories = addedCategories,
                    isEditable = false,
                    editState = EditState.Editing(),
                    onEditStateChanged = {},
                    onRemoveClicked = onCategoryRemoved,
                )

                item { Spacer(modifier = Modifier.height(margin_large)) }
            }

            rulonaCategoriesList(
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

@Composable
fun PlaceDetailsOverview(
    place: PlaceEntity?,
    rules: List<RuleWithCategoryEntity>,
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

                val alpha by animateFloatAsState(
                    targetValue = if (rules.isNotEmpty()) 1f else 0f,
                    animationSpec = tween(500)
                )

                Box(Modifier.alpha(alpha)) {
                    RulonaRulesList(
                        title = "Meine Kategorien",
                        rules = rules,
                        onEditStateChanged = onEditStateChanged
                    )
                }
            }
        }
    }
}
