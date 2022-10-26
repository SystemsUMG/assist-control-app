package com.cristiangonzalez.assistcontrol.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity

@Dao
interface UserDao {
    //Consulta email(usuario) existe
    @Query("SELECT * FROM user_table WHERE email = :email")
    suspend fun findUser(email: String): UserEntity?

    //Insertar usuario
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(users: UserEntity)
}