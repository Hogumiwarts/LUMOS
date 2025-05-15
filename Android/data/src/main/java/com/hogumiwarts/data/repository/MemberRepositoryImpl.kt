package com.hogumiwarts.data.repository

import com.hogumiwarts.data.entity.remote.Response.MemberResponse
import com.hogumiwarts.data.source.remote.MemberApi
import com.hogumiwarts.domain.model.MemberResult
import com.hogumiwarts.domain.repository.MemberRepository
import javax.inject.Inject

class MemberRepositoryImpl @Inject constructor(
    private val memberApi: MemberApi
) : MemberRepository {
    // 회원 정보 조회
    override suspend fun member(accessToken: String): MemberResult {
        return try {
            val response = memberApi.member("Bearer $accessToken")

            MemberResult.Success(response.data.name)
        } catch (e: Exception) {
            // 실패
            MemberResult.Failure("회원 정보 조회 실패")
        }
    }
}