package com.example.tictaktoe.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tictaktoe.data.Game
import com.example.tictaktoe.data.GameType

@Composable
fun ListOfGamesWidget(
    viewModel: TicTakToeViewModel,
    gameType: GameType,
    onGameClicked: (Game) -> Unit,
) {

    val header = when (gameType) {
        GameType.NEW -> "New games"
        GameType.NOT_FINISHED -> "Not finished games"
        GameType.FINISHED -> "Finished games"
    }

    val listOfGames by when (gameType) {
        GameType.NEW -> viewModel.newGames.collectAsState()
        GameType.NOT_FINISHED -> viewModel.currentGames.collectAsState()
        GameType.FINISHED -> viewModel.prevGames.collectAsState()
    }

    val username by viewModel.username.collectAsState()

    val emptyListStub = when (gameType) {
        GameType.NEW -> "No new games"
        GameType.NOT_FINISHED -> "No current games"
        GameType.FINISHED -> "No finished games"
    }

    val borderColor = when (gameType) {
        GameType.NEW -> Color(0x70e95858)
        else -> Color(0x50a47c45)
    }

    Text(
        text = header,
        fontWeight = FontWeight.Medium,
        fontStyle = FontStyle.Italic
    )
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        if (listOfGames.isNotEmpty()) {
            items(listOfGames) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onGameClicked(it) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Oponent: ")
                        }

                        append("${it.getOponent(username)}\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Started at: ")
                        }

                        append("${it.createdAt}\n")

                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Updated at: ")
                        }

                        append(it.updatedAt.toString())
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        shape = MaterialTheme.shapes.medium,
                        elevation = 5.dp,
                        border = BorderStroke(
                            width = 1.dp,
                            color = borderColor
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = text
                        )
                    }
                }
            }
        } else { // empty listOfGames
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = 5.dp,
                    border = BorderStroke(
                        width = 1.dp,
                        color = borderColor
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = emptyListStub,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ListOfGamesWidgetPreview() {
    ListOfGamesWidget(
        viewModel = PreviewViewModel(),
        gameType = GameType.NEW,
        onGameClicked = { }
    )
}