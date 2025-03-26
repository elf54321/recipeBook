package com.elte.recipebook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elte.recipebook.data.entities.UnitOfMeasure
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitOfMeasureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unitOfMeasure: UnitOfMeasure)

    @Query("SELECT * FROM unit_of_measure")
    fun getAllUnitsOfMeasure(): Flow<List<UnitOfMeasure>>

    @Query("SELECT * FROM unit_of_measure WHERE iD = :id LIMIT 1")
    suspend fun getUnitOfMeasureById(id: Int): UnitOfMeasure?
}