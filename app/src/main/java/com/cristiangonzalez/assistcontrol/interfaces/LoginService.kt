package com.cristiangonzalez.assistcontrol.interfaces

import com.cristiangonzalez.assistcontrol.models.*
import retrofit2.Response
import retrofit2.http.*

interface LoginService {
    @POST("/api/login")
    @Headers("Accept: application/json")
    suspend fun login(@Body body:User): Response<Login>
}