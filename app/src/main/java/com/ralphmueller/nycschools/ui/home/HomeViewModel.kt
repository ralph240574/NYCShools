package com.ralphmueller.nycschools.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphmueller.nycschools.data.Result
import com.ralphmueller.nycschools.data.SchoolRepo
import com.ralphmueller.nycschools.model.School
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * UI state for the Home screen
 */
data class HomeUiState(
    val schools: List<School> = emptyList(),
    val loading: Boolean = false,
    val message: String? = null,
    val exception: Throwable? = null
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    val schoolRepo: SchoolRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(loading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshSchools()
    }

    fun refreshSchools(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            schoolRepo.getSchools(forceRefresh = forceRefresh)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> _uiState.update {
                            it.copy(
                                loading = true,
                                message = null,
                                exception = null,
                            )
                        }

                        is Result.Success -> _uiState.update {
                            it.copy(
                                loading = false,
                                schools = result.data,
                                message = "success",
                                exception = null,
                            )
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    loading = false,
                                    message = "error",
                                    exception = result.exception,
                                )
                            }
                        }
                    }
                }
        }
    }

    fun resetError() {
        _uiState.update {
            it.copy(
                message = null,
                exception = null,
            )
        }
    }
}


