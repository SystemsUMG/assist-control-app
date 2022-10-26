package com.cristiangonzalez.assistcontrol.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_table",
    indices = [Index(value = ["email"], unique = true)] //Email unico
)
//Campos de tabla
data class UserEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "token") val token: String
)