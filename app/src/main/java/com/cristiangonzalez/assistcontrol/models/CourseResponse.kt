package com.cristiangonzalez.assistcontrol.models

data class CourseResponse(
    var result: String,
    var message: String,
    var records: ArrayList<Course>
)