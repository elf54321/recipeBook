package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elte.recipebook.data.entities.Ingredient
import com.elte.recipebook.data.entities.Nutrition
import com.elte.recipebook.data.entities.Recipe

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe)

    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<Recipe>

    @Query("SELECT * FROM recipe WHERE id = :id")
    suspend fun getRecipeById(id: Int): Recipe

    @Query("DELETE FROM recipe WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    @Query("SELECT * FROM ingredient WHERE iD IN (SELECT ingredientId FROM RecipeIngredientCrossRef WHERE recipeId = :recipeId)")
    suspend fun getIngredientsByRecipeId(recipeId: Int): List<Ingredient>

    @Query("SELECT * FROM nutrition WHERE iD = :nutritionId")
    suspend fun getNutritionById(nutritionId: Int): Nutrition

}