package com.cristiangonzalez.assistcontrol.models

import com.cristiangonzalez.assistcontrol.database.entities.UserEntity

data class Login(
    var result: String,
    var message: String,
    var records: UserEntity?
)
