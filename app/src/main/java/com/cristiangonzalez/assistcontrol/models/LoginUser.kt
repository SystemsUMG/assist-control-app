package com.cristiangonzalez.assistcontrol.models

data class LoginUser (
    var user_id: String,
    var name: String,
    var type: String,
    var token: String
)