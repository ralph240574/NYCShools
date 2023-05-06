package com.ralphmueller.nycschools.ui.detail

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
data class DetailUiState(
    val school: School? = null,
    val loading: Boolean = false,
    val message: String? = null,
    val exception: Throwable? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    val schoolRepo: SchoolRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState(loading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()


    fun getSchool(dbn: String) {
        viewModelScope.launch {
            schoolRepo.getSchool(dbn = dbn)
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
                                school = result.data,
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
}


