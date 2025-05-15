package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.MemberResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface MemberApi {
    @GET("/member/api/member")
    suspend fun member(
        @Header("Authorization") accessToken: String
    ) : MemberResponse
}