package com.cristiangonzalez.assistcontrol.data

import com.cristiangonzalez.assistcontrol.database.daos.UserDao
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity
import javax.inject.Inject

//Inyeccion de metodos DAO
class UserRepository @Inject constructor(private val userDao: UserDao) {

    suspend fun getUser() = userDao.getUser()

    suspend fun insertUser(user: UserEntity) = userDao.insert(user)

    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

}