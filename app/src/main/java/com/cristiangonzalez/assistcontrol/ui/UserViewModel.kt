package com.cristiangonzalez.assistcontrol.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import com.cristiangonzalez.assistcontrol.data.UserRepository
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {
    suspend fun getUser() : UserEntity? {
        return userRepository.getUser()
    }

    suspend fun insertUser(user: UserEntity) {
        return userRepository.insertUser(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        return userRepository.deleteUser(user)
    }
}