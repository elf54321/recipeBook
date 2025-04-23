package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.elte.recipebook.data.entities.MealPlan
import com.elte.recipebook.data.entities.MealPlanRecipeCrossRef
import com.elte.recipebook.data.entities.MealPlanWithRecipes
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealPlan: MealPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlanRecipeCrossRef(mealPlanRecipeCrossRef: MealPlanRecipeCrossRef)

    @Transaction
    @Query("SELECT * FROM meal_plan WHERE date = :date")
    fun getMealPlanWithRecipesForDate(date: Date): Flow<List<MealPlanWithRecipes>>

    @Query("""
    DELETE FROM MealPlanRecipeCrossRef
    WHERE mealPlanId = :mealPlanId AND recipeId = :recipeId
""")
    suspend fun deleteMealFromMealPlan(mealPlanId: Int, recipeId: Int)


}
