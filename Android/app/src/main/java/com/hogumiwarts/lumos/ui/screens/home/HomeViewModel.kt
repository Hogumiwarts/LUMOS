package com.hogumiwarts.lumos.ui.screens.home

import androidx.lifecycle.ViewModel
import com.hogumiwarts.domain.model.ApiResult
import com.hogumiwarts.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ContainerHost<HomeState, HomeSideEffect>, ViewModel() {

    override val container = container<HomeState, HomeSideEffect>(HomeState())

    fun onIntent(action: HomeIntent) = intent {
        when (action) {
            is HomeIntent.LoadWeather -> {
                fetchWeather(lat = action.latitude, lon = action.longitude)
            }
        }
    }


    /*
     * 실제 API 호출 + 상태 갱신 로직
     */
    private fun fetchWeather(lat: Double, lon: Double) = intent {
        // 1) 로딩 시작
        reduce { state.copy(isLoading = true, errorMessage = null) }

        // 2) UseCase 호출 후 결과 처리
        getWeatherUseCase(lat, lon).collect { result ->
            when (result) {
                is ApiResult.Loading -> {
                    // 로딩 중
                    reduce { state.copy(isLoading = true) }
                }

                is ApiResult.Success -> {
                    Timber.tag("Weather").i(
                        """
                        ✅ 날씨 정보 불러오기
                        ${result.message}
                        ${result.data}
                        """.trimIndent()
                    )
                    // 성공
                    reduce { state.copy(isLoading = false, weatherInfo = result.data, errorMessage = null) }
                }

                is ApiResult.Error -> {
                    // HTTP 에러
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(HomeSideEffect.ShowError(result.message ?: "서버 오류"))
                }

                is ApiResult.Fail -> {
                    // 네트워크 예외
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(HomeSideEffect.ShowError(result.message ?: "네트워크 연결 오류"))
                }
            }
        }
    }
}