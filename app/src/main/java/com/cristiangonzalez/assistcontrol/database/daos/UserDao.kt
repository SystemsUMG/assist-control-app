package com.cristiangonzalez.assistcontrol.database.daos

import androidx.room.*
import com.cristiangonzalez.assistcontrol.database.entities.UserEntity

@Dao
interface UserDao {
    //Consulta usuario
    @Query("SELECT * FROM user_table")
    suspend fun getUser(): UserEntity?

    //Insertar usuario
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(users: UserEntity)

    //Eliminar usuario
    @Delete
    suspend fun deleteUser(user: UserEntity)

}