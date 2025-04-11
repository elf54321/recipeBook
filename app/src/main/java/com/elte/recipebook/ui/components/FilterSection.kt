package com.elte.recipebook.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(title: String, options: List<String>) {
    val selectedOptions = remember { mutableStateListOf<String>() }

    Column {
        Text(
            title,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option in selectedOptions
                OutlinedButton(
                    onClick = {
                        if (isSelected) selectedOptions.remove(option)
                        else selectedOptions.add(option)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) Color(0xFFFFC107) else Color.White,
                        contentColor = if (isSelected) Color.Black else Color(0xFFFFC107)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFFC107)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}
