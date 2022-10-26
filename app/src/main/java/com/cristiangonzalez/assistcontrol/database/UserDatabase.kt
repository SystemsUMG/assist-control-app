package com.cristiangonzalez.assistcontrol.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cristiangonzalez.assistcontrol.database.daos.UserDao
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity

@Database(entities = [UserEntity::class], version = 2)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao():UserDao
}