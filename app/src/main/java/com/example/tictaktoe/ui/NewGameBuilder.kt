package com.example.tictaktoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.ui.theme.TicTakToeTheme

@Composable
fun NewGameBuilder(
    viewModel: TicTakToeViewModel,
    navController: NavController,
) {
    val options = listOf("Noughts", "Crosses")
    val selectedOption by viewModel.selectedGameOption.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val friends by viewModel.friends.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "You play with:")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            options.forEach { text ->
                RadioButton(selected = text.equals(selectedOption),
                    onClick = {
                        viewModel.selectGameOption(text)
                    }
                )
                Text(text = text)
            }
        }

        Spacer(Modifier.size(8.dp))

        Text(text = "Select oponent (friends only):")
        SelectUserList(
            users = friends.toList().map { it.name },
            selectedUser = selectedUser,
            buttonText = "Start new game",
            onButtonClicked = {
//                viewModel.createNewGame()
//                navController.navigate("")
            },
            onCardClick = { viewModel.selectUser(it) }
        )
    }
}

@Preview
@Composable
fun NewGameBuilderPreview() {
    TicTakToeTheme() {
        NewGameBuilder(
            viewModel = PreviewViewModel(),
            navController = rememberNavController()
        )
    }
}