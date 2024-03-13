package es.jvbabi.vplanplus.feature.home.feature_search.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.usecase.general.Identity
import es.jvbabi.vplanplus.feature.home.feature_search.domain.usecase.SearchUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCases: SearchUseCases
) : ViewModel() {
    val state = mutableStateOf(SearchState())

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                listOf(
                    searchUseCases.getCurrentIdentityUseCase(),
                    searchUseCases.isSyncRunningUseCase()
                )
            ) { data ->
                val currentIdentity = data[0] as Identity?
                val isSyncRunning = data[1] as Boolean

                state.value.copy(
                    identity = currentIdentity,
                    isSyncRunning = isSyncRunning
                )
            }.collect {
                state.value = it
            }
        }
    }

    fun onSearchActiveChange(expanded: Boolean) {
        state.value = state.value.copy(expanded = expanded)
        if (!expanded) {
            state.value = state.value.copy(results = emptyList(), query = "")
        }
    }

    fun onQueryChange(query: String) {
        state.value = state.value.copy(query = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            state.value = state.value.copy(results = emptyList())
            return
        }
        state.value = state.value.copy(isSearchRunning = true)
        searchJob = viewModelScope.launch {
            state.value = state.value.copy(
                results = searchUseCases.searchUseCase(query),
                isSearchRunning = false
            )
        }
    }
}

data class SearchState(
    val query: String = "",
    val expanded: Boolean = false,
    val identity: Identity? = null,
    val isSyncRunning: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val isSearchRunning: Boolean = false
)

data class SearchResult(
    val name: String,
    val school: String,
    val lessons: List<Lesson>?
)