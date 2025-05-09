package com.elte.recipebook.data.database

import android.content.Context
import androidx.room.Room
import com.elte.recipebook.data.dao.IngredientDao
import com.elte.recipebook.data.dao.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "recipe_database.db").
        fallbackToDestructiveMigration().build()

    @Provides
    fun provideRecipeDao(db: AppDatabase): RecipeDao = db.recipeDao()

    @Provides
    fun provideIngredientDao(db: AppDatabase): IngredientDao =
        db.ingredientDao()
}
