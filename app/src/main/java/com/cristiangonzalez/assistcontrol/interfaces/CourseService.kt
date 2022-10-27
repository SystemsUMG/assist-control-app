package com.cristiangonzalez.assistcontrol.interfaces

import com.cristiangonzalez.assistcontrol.models.*
import retrofit2.Response
import retrofit2.http.*

interface CourseService {
    @GET("/api/student-courses-list/{student_id}")
    @Headers("Accept: application/json")
    suspend fun getCourses(@Path("student_id") type: String): Response<CourseResponse>

    @POST("/api/attendances")
    @Headers("Accept: application/json")
    suspend fun recordAttendance(@Body body: RecordAttendance): Response<ResponseAttendance>

    @GET("/api/percentages/{student_id}")
    @Headers("Accept: application/json")
    suspend fun getStatistics(@Path("student_id") type: String): Response<StatisticResponse>
}