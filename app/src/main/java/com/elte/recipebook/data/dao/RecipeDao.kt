package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.elte.recipebook.data.entities.Ingredient
import com.elte.recipebook.data.entities.Recipe
import com.elte.recipebook.data.entities.RecipeIngredientCrossRef

@Dao
interface RecipeDao {

    /**
     * Insert a Recipe and return its new primary key
     * so we can populate the cross-ref table afterward.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: Recipe): Long

    /**
     * Link one Recipe to one Ingredient in the junction table.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecipeIngredientCrossRef(crossRef: RecipeIngredientCrossRef)

    /** Get all recipes (just the Recipe rows). */
    @Query("SELECT * FROM recipe")
    suspend fun getAllRecipes(): List<Recipe>

    /** Get a single Recipe by its ID. */
    @Query("SELECT * FROM recipe WHERE id = :id")
    suspend fun getRecipeById(id: Int): Recipe

    /** Delete a Recipe (and—via CASCADE—all its RecipeIngredientCrossRef rows). */
    @Query("DELETE FROM recipe WHERE id = :id")
    suspend fun deleteRecipeById(id: Int)

    /**
     * Fetch only the Ingredients for a given Recipe.
     * Useful if you need the list of Ingredient entities by recipeId.
     */
    @Query("""
      SELECT i.* 
      FROM ingredient AS i
      INNER JOIN recipe_ingredient_cross_ref AS rc
        ON rc.ingredientId = i.id
      WHERE rc.recipeId = :recipeId
    """)
    suspend fun getIngredientsByRecipeId(recipeId: Int): List<Ingredient>


    @Query("DELETE FROM recipe_ingredient_cross_ref WHERE recipeId = :recipeId")
    suspend fun deleteCrossRefsByRecipeId(recipeId: Int)

    @Update
    suspend fun update(recipe: Recipe)

}