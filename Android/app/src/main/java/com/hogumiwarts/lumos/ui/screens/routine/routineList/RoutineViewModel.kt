package com.hogumiwarts.lumos.ui.screens.routine.routineList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.data.entity.remote.Request.RefreshRequest
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.domain.model.routine.RoutineResult
import com.hogumiwarts.domain.model.routine.Routine
import com.hogumiwarts.domain.model.routine.RoutineDetailData
import com.hogumiwarts.domain.repository.RoutineRepository
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val tokenDataStore: TokenDataStore,
    private val authApi: AuthApi

) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineState())
    val uiState: StateFlow<RoutineState> = _uiState

    // 루틴 목록
    private val _routineList = MutableStateFlow<List<Routine>>(emptyList())
    val routineList: StateFlow<List<Routine>> = _routineList

    private val _routineDetail = MutableStateFlow<RoutineDetailData?>(null)
    val routineDetail: StateFlow<RoutineDetailData?> = _routineDetail

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

                is RoutineResult.Unauthorized -> {
                    Timber.tag("RoutineViewModel").d("❗ 401 발생 - 토큰 갱신 시도")
                    refreshAndRetry()
                }

                is RoutineResult.DetailSuccess -> {
                    Timber.tag("RoutineViewModel").d("📋 루틴 상세 응답: ${result.detail}")
                    _routineDetail.value = result.detail
                }

                is RoutineResult.CreateSuccess -> TODO()
            }
        }
    }

    private fun refreshAndRetry() {
        viewModelScope.launch {
            try {
                val refreshToken = tokenDataStore.getRefreshToken().first()
                Timber.tag("routine").d("🔐 리프레시 토큰: $refreshToken")

                val response = authApi.refresh(RefreshRequest(refreshToken))
                val newAccessToken = response.data.accessToken
                val name = tokenDataStore.getUserName().firstOrNull() ?: ""

                tokenDataStore.saveTokens(newAccessToken, refreshToken, name)

                Timber.tag("RoutineViewModel").d("🔄 토큰 갱신 성공, 루틴 재요청 시도")
                getRoutineList()

            } catch (e: Exception) {
                Timber.tag("RoutineViewModel").e(e, "❌ 토큰 갱신 실패")
                _uiState.value = _uiState.value.copy(errorMessage = "로그인 정보가 만료되었습니다.")
            }
        }
    }


}