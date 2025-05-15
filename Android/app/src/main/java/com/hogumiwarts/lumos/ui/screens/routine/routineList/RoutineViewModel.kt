package com.hogumiwarts.lumos.ui.screens.routine.routineList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.data.entity.remote.Response.RoutineData
import com.hogumiwarts.domain.model.RoutineResult
import com.hogumiwarts.domain.model.Routine
import com.hogumiwarts.domain.repository.RoutineRepository
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val tokenDataStore: TokenDataStore

) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineState())
    val uiState: StateFlow<RoutineState> = _uiState

    // 루틴 목록
    private val _routineList = MutableStateFlow<List<Routine>>(emptyList())
    val routineList: StateFlow<List<Routine>> = _routineList

    // 루틴 목록 불러오기
    fun getRoutineList() {
        viewModelScope.launch {
            val accessToken = tokenDataStore.getAccessToken().first()
            Timber.tag("RoutineViewModel").d("📦 액세스 토큰: $accessToken")

            when (val result = routineRepository.getRoutineList(accessToken)) {
                is RoutineResult.Success -> {
                    Timber.tag("RoutineViewModel").d("✅ 루틴 개수: ${result.routines.size}")
                    result.routines.forEach {
                        Timber.tag("RoutineViewModel")
                            .d("🔹 ${it.routineId} / ${it.routineName} / ${it.routineIcon}")
                    }
                    _routineList.value = result.routines
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }

                is RoutineResult.Failure -> {
                    Timber.tag("RoutineViewModel").d("RoutineResult 실패")

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

}