package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.MemberResult

// member 기능을 위한 인터페이스(회원 정보 조회 등)
interface MemberRepository {
    suspend fun member(accessToken: String): MemberResult
}