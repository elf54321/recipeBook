package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.elte.recipebook.data.entities.MealPlan
import com.elte.recipebook.data.entities.MealPlanWithRecipes
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealPlan: MealPlan)

    @Transaction
    @Query("SELECT * FROM meal_plan")
    fun getMealPlansWithRecipes(): Flow<List<MealPlanWithRecipes>>
}