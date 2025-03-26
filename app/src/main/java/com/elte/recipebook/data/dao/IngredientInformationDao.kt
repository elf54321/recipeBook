package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elte.recipebook.data.entities.IngredientInformation
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientInformationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: IngredientInformation)

    @Query("SELECT * FROM ingredient_information")
    fun getAllIngredientInformation(): Flow<List<IngredientInformation>>
}