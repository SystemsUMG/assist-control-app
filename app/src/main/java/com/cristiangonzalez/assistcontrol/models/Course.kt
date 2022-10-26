package com.cristiangonzalez.assistcontrol.models

data class Course (
    var id: String,
    var student_id: String,
    var tc_assigned_id: String,
    var teacher: String,
    var schedule: String,
    var course: String
)