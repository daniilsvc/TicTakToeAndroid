package com.example.tictaktoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun  SelectUserList(
    users: List<String>,
    selectedUser: String,
    buttonText: String,
    onButtonClicked: () -> Unit,
    onCardClick: (String) -> Unit
) {
    LazyColumn() {
        items(users.toList()) { oponentName ->
            Card(
                Modifier
                    .padding(8.dp)
                    .background(
                        if (oponentName.equals(selectedUser))
                            Color(0x802654df) else Color.White
                    )
                    .clickable {
                        onCardClick(oponentName)
                    }
            ) {
                Text(
                    modifier = Modifier.background(
                        if (oponentName.equals(selectedUser))
                            Color(0x802654df) else Color.White
                    ),
                    text = oponentName
                )
            }
        }
    }

    Button(onClick = onButtonClicked) {
        Text(text = buttonText)
    }
}