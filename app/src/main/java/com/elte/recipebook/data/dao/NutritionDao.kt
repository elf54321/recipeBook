package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elte.recipebook.data.entities.Nutrition
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(nutrition: Nutrition): Long

    @Query("SELECT * FROM nutrition")
    fun getAllNutrition(): Flow<List<Nutrition>>

    @Query("SELECT * FROM nutrition WHERE id = :id LIMIT 1")
    suspend fun getNutritionById(id: Int): Nutrition?
}