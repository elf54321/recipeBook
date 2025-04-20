package com.elte.recipebook

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.elte.recipebook.data.TypeOfMeal
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

import com.elte.recipebook.data.entities.*
import com.elte.recipebook.data.dao.*
import com.elte.recipebook.data.database.*

class MealPlanWithRecipesTest {
    private lateinit var db: AppDatabase
    private lateinit var mealPlanDao: MealPlanDao
    private lateinit var recipeDao: RecipeDao
    private lateinit var nutritionDao: NutritionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // only for testing
            .build()
        mealPlanDao = db.mealPlanDao()
        recipeDao = db.recipeDao()
        nutritionDao = db.nutritionDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testMealPlanWithRecipes() = runBlocking {

        val nutrition = Nutrition(
            iD = 1,
            energy = 200.0,
            fat = 10.0,
            protein = 15.0,
            carbohydrate = 30.0,
            sugar = 5.0,
            salt = 0.5,
            fiber = 2.0
        )
        nutritionDao.insert(nutrition)

        // Create and insert a Recipe
        val recipe = Recipe(
            iD = 1,
            name = "Test Recipe",
            source = "UnitTest",
            instructions = "Mix ingredients",
            portion = 2.0,
            typeOfMeal = TypeOfMeal.DINNER,
            priceCategory = "Budget",
            nutritionId = 1
        )
        recipeDao.insert(recipe)

        // Create and insert a MealPlan
        val mealPlan = MealPlan(
            iD = 1,
            date = java.util.Date(),
            comment = "Test MealPlan"
        )
        mealPlanDao.insert(mealPlan)

        // Insert a junction record linking mealPlan and recipe
        val crossRef = MealPlanRecipeCrossRef(
            mealPlanId = mealPlan.iD,
            recipeId = recipe.iD
        )
        db.runInTransaction {
            db.mealPlanDao().apply {
                // You might need to add a dedicated DAO method to insert a cross-ref,
                // or insert directly using the database's insertion method.
                db.compileStatement(
                    "INSERT INTO MealPlanRecipeCrossRef (mealPlanId, recipeId) VALUES (?,?)"
                ).apply {
                    bindLong(1, crossRef.mealPlanId.toLong())
                    bindLong(2, crossRef.recipeId.toLong())
                    executeInsert()
                }
            }
        }

        // Retrieve the MealPlan with its associated recipes
        val mealPlansWithRecipes = mealPlanDao.getMealPlansWithRecipes().first()
        assertEquals(1, mealPlansWithRecipes.size)
        assertEquals(1, mealPlansWithRecipes[0].recipes.size)
        assertEquals("Test Recipe", mealPlansWithRecipes[0].recipes[0].name)
    }
}