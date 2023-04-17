package com.example.tictaktoe.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.data.Game
import com.example.tictaktoe.data.GameType
import com.example.tictaktoe.ui.theme.TicTakToeTheme

@Composable
fun ListOfGames(
    viewModel: TicTakToeViewModel,
    navController: NavController,
) {
    val username by viewModel.username.collectAsState()
    val newGames by viewModel.newGames.collectAsState()
    val currentGames by viewModel.currentGames.collectAsState()

    val onGameClicked: (Game) -> Unit = {
        viewModel.setGame(it)
        viewModel.onGameEnter()
        navController.navigate("gameplayer")
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ListOfGamesWidget(
            viewModel = viewModel,
            gameType = GameType.NEW,
            onGameClicked = onGameClicked
        )

        Spacer(Modifier.size(8.dp))

        ListOfGamesWidget(
            viewModel = viewModel,
            gameType = GameType.NOT_FINISHED,
            onGameClicked = onGameClicked
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = { navController.navigate("listofprevgames") }) {
                Text(text = "Previous Games")
            }
        }
    }
}

@Preview
@Composable
fun ListOfGamesPreview() {
    TicTakToeTheme() {
        ListOfGames(
            viewModel = PreviewViewModel(),
            navController = rememberNavController()
        )
    }
}