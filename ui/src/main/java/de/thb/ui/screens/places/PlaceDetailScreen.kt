package de.thb.ui.screens.places

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import de.thb.core.data.sources.categories.CategoriesRepsitory
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.data.sources.rules.RulesRepository
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceTrend
import de.thb.core.domain.place.placeTrendFromInt
import de.thb.core.domain.rule.RuleWithCategoryEntity
import de.thb.core.util.RuleUtils
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.places.PlaceTrendIndicator
import de.thb.ui.components.places.rulonaCategoriesList
import de.thb.ui.components.places.rulonaRulesList
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.AddCategoryUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.EditCategoriesUseCase
import de.thb.ui.screens.places.PlaceDetailScreenUseCase.RemoveCategoryUseCase
import de.thb.ui.screens.places.PlaceDetailUiState.EditCategoriesUiState
import de.thb.ui.screens.places.PlaceDetailUiState.OverviewUiState
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.type.EditState
import de.thb.ui.type.RulonaAppBarAction.Back
import de.thb.ui.type.RulonaAppBarAction.Bookmark
import de.thb.ui.type.RulonaAppBarAction.Share
import de.thb.ui.util.IntentManager
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
    val myRules: List<RuleWithCategoryEntity> = listOf(),
    val allRules: List<RuleWithCategoryEntity> = listOf(),
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
            is PlaceDetailScreenUseCase.BookmarkPlaceUseCase -> bookmarkPlace(useCase.place)
        }
    }

    private fun bookmarkPlace(place: PlaceEntity) {
        viewModelScope.launch {
            placesRepository.insert(place.copy(isBookmarked = true))
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
                val (myRules, allRules) = rules
                    .filter { it.category != null }
                    .sortedBy { it.category!!.name }
                    .partition { it.category!!.added == true }

                setState { copy(myRules = myRules, allRules = allRules) }
            }.launchIn(viewModelScope)
    }
}

@Composable
fun PlaceDetailsScreen(
    placeId: String,
    viewModel: PlaceDetailsViewModel = mavericksViewModel(),
    onBackClicked: () -> Unit,
) {
    LaunchedEffect(placeId) {
        viewModel.loadPlace(placeId)
        viewModel.loadRules(placeId)
    }

    val context = LocalContext.current

    val place by viewModel.collectAsState(PlaceDetailsState::place)
    val myRules by viewModel.collectAsState(PlaceDetailsState::myRules)
    val allRules by viewModel.collectAsState(PlaceDetailsState::allRules)
    val addedCategories by viewModel.collectAsState(PlaceDetailsState::addedCategories)
    val notAddedCategories by viewModel.collectAsState(PlaceDetailsState::notAddedCategories)

    val placeDetailUiState = viewModel.collectAsState(PlaceDetailsState::uiState)

    val activity = LocalContext.current as Activity

    when (placeDetailUiState.value) {
        is OverviewUiState -> {
            PlaceDetailsOverview(
                place = place,
                myRules = myRules,
                allRules = allRules,
                onBackClicked = onBackClicked,
                onShareClicked = {
                    place?.let { place ->
                        IntentManager.createSharePlaceIntent(
                            activity = activity,
                            placeId = place.id,
                        ).startChooser()
                    }
                },
                onBookmarkClicked = {
                    place?.let { place ->
                        viewModel.action(
                            PlaceDetailScreenUseCase.BookmarkPlaceUseCase(place)
                        )

                        Toast.makeText(
                            context,
                            "${place.name} zu Meinen Orten hinzugefügt.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
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
    myRules: List<RuleWithCategoryEntity>,
    allRules: List<RuleWithCategoryEntity>,
    onBackClicked: () -> Unit,
    onBookmarkClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onEditStateChanged: (EditState) -> Unit,
) {
    if (place != null) {
        Column {
            RulonaAppBar(
                title = place.name,
                back = Back(onBackClicked),
                actions = listOf(
                    Bookmark(onBookmarkClicked),
                    Share(onShareClicked)
                )
            )

            val myRulesWithCategoriesGrouped = remember(myRules) {
                RuleUtils.groupRulesByCategory(myRules)
            }

            val allRulesWithCategoriesGrouped = remember(allRules) {
                RuleUtils.groupRulesByCategory(allRules)
            }

            LazyColumn {
                item {
                    PlaceDetailsHeader(
                        trend = placeTrendFromInt(place.trend),
                        incidence = place.incidence.toString(),
                        name = place.name,
                        website = place.website,
                    )
                }

                rulonaRulesList(
                    title = "Meine Kategorien",
                    rulesWithCategoriesGrouped = myRulesWithCategoriesGrouped,
                    onEditStateChanged = onEditStateChanged,
                )

                if (myRules.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(margin_large)) }
                }

                if (allRules.isNotEmpty()) {
                    rulonaRulesList(
                        title = "Alle Regeln für ${place.name}",
                        isEditable = false,
                        rulesWithCategoriesGrouped = allRulesWithCategoriesGrouped,
                        onEditStateChanged = onEditStateChanged,
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceDetailsHeader(
    trend: PlaceTrend,
    incidence: String,
    name: String,
    website: String,
) {
    Column(Modifier.padding(margin_medium)) {
        Row {
            PlaceTrendIndicator(trend)

            Text(text = "7-Tage-Inzidenz", modifier = Modifier.weight(0.8f))

            Text(text = incidence)
        }

        val text = "Die offiziellen Regeln für $name lassen sich hier einsehen."
        val uriHandler = LocalUriHandler.current

        val annotatedLinkString = buildAnnotatedString {
            append(text)

            addStringAnnotation(
                tag = "URL",
                annotation = website,
                start = text.indexOf("hier"),
                end = text.indexOf("hier") + 4,
            )

            addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = text.indexOf("hier"),
                end = text.indexOf("hier") + 4
            )
        }

        ClickableText(
            text = annotatedLinkString,
            style = MaterialTheme.typography.body1,
            onClick = {
                annotatedLinkString
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            },
            modifier = Modifier.padding(top = margin_medium)
        )
    }
}
