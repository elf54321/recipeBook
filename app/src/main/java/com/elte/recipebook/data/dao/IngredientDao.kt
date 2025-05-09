package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elte.recipebook.data.entities.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: Ingredient): Long

    @Query("SELECT * FROM ingredient")
    fun getAllIngredients(): Flow<List<Ingredient>>
}