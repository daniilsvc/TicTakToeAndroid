package com.example.tictaktoe.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.data.Game
import com.example.tictaktoe.ui.theme.TicTakToeTheme
import kotlin.math.sqrt

private const val TAG = "GamePlayer"

@Composable
fun GamePlayer(
    viewModel: TicTakToeViewModel,
    navController: NavController,
) {

    val currentGame by viewModel.currentGame.collectAsState()

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(450.dp)
//            .border(width = 4.dp, color = Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { position ->
                    Log.d(TAG, "Field clicked: $position")
                    viewModel.processGameClick(position, size)
                    true
                })
            }) {
            // Initialize field's grid
            val gridColor = Color(0xffa47c45)
            drawLine(
                start = Offset(size.width / 3, 0f),
                end = Offset(size.width / 3, size.height),
                color = gridColor,
                strokeWidth = 10f
            )

            drawLine(
                start = Offset(2 * size.width / 3, 0f),
                end = Offset(2 * size.width / 3, size.height),
                color = gridColor,
                strokeWidth = 10f
            )

            drawLine(
                start = Offset(0f, size.height / 3),
                end = Offset(size.width, size.height / 3),
                color = gridColor,
                strokeWidth = 10f
            )

            drawLine(
                start = Offset(0f, 2 * size.height / 3),
                end = Offset(size.width, 2 * size.height / 3),
                color = gridColor,
                strokeWidth = 10f
            )

            // Initial state
            for ((index, value) in currentGame.state.withIndex()) {
                when (value) {
                    '1' -> drawCross(this, size, index)
                    '2' -> drawNought(this, size, index)
                    else -> Unit
                }
            }
        }

        Spacer(Modifier.height(64.dp))
        Text(text = statusLine(currentGame))
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            viewModel.onGameLeave()
            navController.popBackStack()
        }) {
            Text(text = "Back")
        }
    }
}


fun statusLine(game: Game): String {
    val winner = game.getWinner()

    if (winner.equals("userN")) {
        return "Winner: ${game.userN}"
    }

    if (winner.equals("userC")) {
        return "Winner: ${game.userC}"
    }

    if (winner.equals("draw")) {
        return "A draw"
    }

    val userTurn = game.whosTurn()

    if (userTurn.equals("userC")) {
        return "Turn: ${game.userC} (crosses)"
    } else {
        return "Turn ${game.userN} (noughts)"
    }
}

fun drawNought(drawScope: DrawScope, size: Size, index: Int) {
    val baseX = (index % 3) * size.width / 3
    val baseY = (index / 3) * size.height / 3
    val color = Color(0xff72e132)

    drawScope.drawCircle(
        radius = sqrt(
            size.width * size.width
                    + size.height * size.height
        ) / 12,
        center = Offset(
            x = baseX + size.width / 6,
            y = baseY + size.height / 6
        ),
        color = color,
        style = Stroke(width = 10f)
    )
}

fun drawCross(drawScope: DrawScope, size: Size, index: Int) {
    val baseX = (index % 3) * size.width / 3
    val baseY = (index / 3) * size.height / 3
    val color = Color(0xffe95858)

    drawScope.drawLine(
        start = Offset(
            x = baseX + (size.width / 12),
            y = baseY + (size.height / 12)
        ),

        end = Offset(
            x = baseX + (size.width / 4),
            y = baseY + (size.height / 4)
        ),

        strokeWidth = 10f,
        color = color
    )

    drawScope.drawLine(
        start = Offset(
            x = baseX + (size.width / 4),
            y = baseY + (size.height / 12)
        ),

        end = Offset(
            x = baseX + (size.width / 12),
            y = baseY + (size.height / 4)
        ),

        strokeWidth = 10f,
        color = color
    )
}

@Preview
@Composable
fun GamePlayerPreview() {
    TicTakToeTheme() {
        GamePlayer(
            viewModel = PreviewViewModel(),
            navController = rememberNavController()
        )
    }
}