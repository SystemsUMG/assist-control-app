package com.cristiangonzalez.assistcontrol.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import com.cristiangonzalez.assistcontrol.data.UserRepository
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {
    suspend fun findUser(email: String) : UserEntity? {
        return userRepository.findUser(email)
    }

    suspend fun insertUser(user: UserEntity) {
        return userRepository.insertUser(user)
    }
}