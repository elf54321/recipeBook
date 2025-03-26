package com.elte.recipebook.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unit_of_measure")
data class UnitOfMeasure (
    @PrimaryKey
    val iD: Int,
    var name: String
)