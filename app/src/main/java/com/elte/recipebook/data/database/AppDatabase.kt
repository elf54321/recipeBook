package com.elte.recipebook.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.elte.recipebook.data.dao.*
import com.elte.recipebook.data.entities.*

@Database(
    entities = [
        Nutrition::class,
        Ingredient::class,
        Recipe::class,
        IngredientInformation::class,
        MealPlan::class,
        MealPlanRecipeCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun ingredientInformationDao(): IngredientInformationDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealPlanDao(): MealPlanDao

    // NEW DAOs
    abstract fun nutritionDao(): NutritionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
