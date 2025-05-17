package com.hogumiwarts.lumos.ui.screens.routine.routineDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.routine.RoutineResult
import com.hogumiwarts.domain.repository.RoutineRepository
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.hogumiwarts.lumos.mapper.toRoutineItem

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val tokenRepository: TokenDataStore
) : ViewModel() {
    private val _state = MutableStateFlow<RoutineDetailState>(RoutineDetailState.Loading)
    val state: StateFlow<RoutineDetailState> = _state


    fun loadRoutine(routineId: String?) {
        viewModelScope.launch {
            _state.value = RoutineDetailState.Loading

            val accessToken = tokenRepository.getAccessToken().first()
            if (routineId == null) {
                _state.value = RoutineDetailState.Error("⚠️ 잘못된 요청입니다.")
                return@launch
            }

            when (val result = routineRepository.getRoutineDetail(accessToken, routineId.toInt())) {
                is RoutineResult.DetailSuccess -> {
                    val detail = result.detail
                    _state.value = RoutineDetailState.Success(
                        routine = detail.toRoutineItem(),
                        devices = detail.devices
                    )
                }

                is RoutineResult.Unauthorized -> {
                    _state.value = RoutineDetailState.Error(" ⚠️ 로그인이 만료되었습니다.")
                }

                is RoutineResult.Failure -> {
                    _state.value = RoutineDetailState.Error(result.message)
                }

                else -> Unit
            }


        }
    }

    fun setError(message: String) {
        _state.value = RoutineDetailState.Error(message)
    }
}