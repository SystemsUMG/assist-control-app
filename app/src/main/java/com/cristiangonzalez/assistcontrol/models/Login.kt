package com.cristiangonzalez.assistcontrol.models

data class Login(
    var result: String,
    var message: String,
    var records: LoginUser?
)
