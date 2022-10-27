package com.cristiangonzalez.assistcontrol.models

data class ResponseAttendance(
    var result: String,
    var message: String,
    var records: ArrayList<String>?
)
