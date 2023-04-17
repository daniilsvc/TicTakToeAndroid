package com.example.tictaktoe.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.data.Game
import com.example.tictaktoe.data.GameType
import kotlinx.coroutines.channels.TickerMode

@Composable
fun ListOfPrevGames(
    viewModel: TicTakToeViewModel,
    navController: NavController,
) {

    val onGameClicked: (Game) -> Unit = {
        viewModel.setGame(it)
        viewModel.onGameEnter()
        navController.navigate("gameplayer")
    }

    val username by viewModel.username.collectAsState()
    val prevGames by viewModel.prevGames.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListOfGamesWidget(
            viewModel = viewModel,
            gameType = GameType.FINISHED,
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

            Button(onClick = { }) {
                Text(text = "Statistics")
            }
        }
    }
}

@Preview
@Composable
fun ListOfPrevGamesPreview() {
    ListOfPrevGames(
        viewModel = PreviewViewModel(),
        navController = rememberNavController()
    )
}