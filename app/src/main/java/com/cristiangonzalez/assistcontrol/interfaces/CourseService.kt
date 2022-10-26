package com.cristiangonzalez.assistcontrol.interfaces

import com.cristiangonzalez.assistcontrol.models.CourseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseService {
    @GET("/api/student-courses-list/{student_id}")
    @Headers("Accept: application/json")
    suspend fun getCourses(@Path("student_id") type: String): Response<CourseResponse>
}