package com.uas.practica08.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<User>>

    @Insert
    fun insert(user: User)

    // Entregable: Funcion Delete
    @Delete
    suspend fun delete(user: User)
}