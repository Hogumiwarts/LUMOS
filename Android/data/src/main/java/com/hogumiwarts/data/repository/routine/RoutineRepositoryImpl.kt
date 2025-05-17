package com.hogumiwarts.data.repository.routine

import com.hogumiwarts.data.entity.remote.Request.CommandRequest
import com.hogumiwarts.data.entity.remote.Request.RoutineCreateRequest
import com.hogumiwarts.data.entity.remote.Request.RoutineDeviceRequest
import com.hogumiwarts.data.source.remote.RoutineApi
import com.hogumiwarts.domain.model.routine.RoutineResult
import com.hogumiwarts.domain.repository.RoutineRepository
import javax.inject.Inject
import com.hogumiwarts.data.mapper.toDomain
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CreateRoutineParam
import retrofit2.HttpException


class RoutineRepositoryImpl @Inject constructor(
    private val routineApi: RoutineApi
) : RoutineRepository {
    override suspend fun getRoutineList(accessToken: String): RoutineResult {
        return try {
            val response = routineApi.getRoutineList("Bearer $accessToken")
            val routines = response.data.toDomain()

            RoutineResult.Success(routines)

        } catch (e: HttpException) {
            if (e.code() == 401) {
                RoutineResult.Unauthorized
            } else {
                RoutineResult.Failure(e.message ?: "루틴 리스트 불러오기 실패")
            }
        } catch (e: Exception) {
            RoutineResult.Failure(e.message ?: "루틴 리스트 불러오기 실패")
        }
    }

    // 루틴 상세 조회
    override suspend fun getRoutineDetail(accessToken: String, routineId: Int): RoutineResult {
        return try {
            val response = routineApi.getRoutineDetail("Bearer $accessToken", routineId)
            RoutineResult.DetailSuccess(response.data.toDomain())
        } catch (e: HttpException) {
            if (e.code() == 401) {
                RoutineResult.Unauthorized
            } else {
                RoutineResult.Failure(e.message ?: "루틴 상세 조회 실패")
            }
        } catch (e: Exception) {
            RoutineResult.Failure(e.message ?: "루틴 상세 조회 실패")
        }
    }

    // 루틴 생성
    fun CreateRoutineParam.toRequest(): RoutineCreateRequest {
        return RoutineCreateRequest(
            routineName = routineName,
            routineIcon = routineIcon,
            gestureId = gestureId,
            devices = devices.map {
                RoutineDeviceRequest(
                    deviceId = it.deviceId,
                    commands = it.commands.first().toRequestCommand()
                )
            }
        )
    }

    fun CommandData.toRequestCommand(): CommandRequest {
        return CommandRequest(
            component = component,
            capability = capability,
            command = command,
            arguments = arguments
        )
    }

    override suspend fun createRoutine(
        result: CreateRoutineParam,
        accessToken: String
    ): RoutineResult {
        return try {
            val request = result.toRequest()
            val response = routineApi.makeRoutine("Bearer $accessToken", request)
            RoutineResult.CreateSuccess(response.toDomain())
        } catch (e: HttpException) {
            if (e.code() == 401) {
                RoutineResult.Unauthorized
            } else {
                RoutineResult.Failure(e.message ?: "루틴 생성 실패")
            }
        } catch (e: Exception) {
            RoutineResult.Failure(e.message ?: "루틴 생성 실패")
        }
    }

}