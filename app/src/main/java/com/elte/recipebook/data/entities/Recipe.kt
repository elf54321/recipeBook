package com.elte.recipebook.data.entities

// Stores a foreign key (nutritionId) referencing Nutrition
// The enum field typeOfMeal will be handled with a type converter.

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.elte.recipebook.data.Equipment
import com.elte.recipebook.data.PriceCategory
import com.elte.recipebook.data.TypeOfMeal

@Entity(
    tableName = "recipe",
    foreignKeys = [
        ForeignKey(
            entity = Nutrition::class,
            parentColumns = ["id"],
            childColumns = ["nutritionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ Index("nutritionId") ]
)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val description: String,
    val imageUri: String? = null,
    val source: String? = null,
    val portion: Double,

    val typeOfMeal: TypeOfMeal,
    val priceCategory: PriceCategory,

    // TypeConverter for List<Equipment>
    var equipment: Array<Equipment> = arrayOf(),

    val nutritionId: Int // → Nutrition.id Foreign Key
)
