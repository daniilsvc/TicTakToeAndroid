package com.example.tictaktoe.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.ui.theme.TicTakToeTheme
import com.parse.ParseUser

@Composable
fun MainMenu(
    navController: NavController,
    viewModel: TicTakToeViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val newGamesCount by viewModel.newGamesCount.collectAsState()
    val outRequestsCount by viewModel.outRequestsCount.collectAsState()
    val inRequestsCount by viewModel.inRequestsCount.collectAsState()

    val username by viewModel.username.collectAsState()

    val onStartNewGameClicked: () -> Unit = {
        navController.navigate("newgameselectfriend")
    }

    val onLogoutClicked: () -> Unit = {
        ParseUser.logOut()
        viewModel.onLogout()
        navController.popBackStack("signuplogin", false)
    }

    val onListOfFriendsClicked: () -> Unit = {
        navController.navigate("listfriends")
    }

    val onListOfGamesClicked: () -> Unit = {
        navController.navigate("listofgames")
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Main Menu")
        Text(text = "Username: $username")

        Spacer(modifier = Modifier.size(32.dp))

        Button(onClick = onStartNewGameClicked) {
            Text(text = "Start new game")
        }

        if (newGamesCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        modifier = Modifier.offset(x = -4.dp, y = 8.dp)
                    ) { Text(text = "$newGamesCount") }
                }
            ) {
                Button(onClick = onListOfGamesClicked) {
                    Text("List of games")
                }
            }
        } else {
            Button(onClick = onListOfGamesClicked) {
                Text(text = "List of games")
            }
        }

        if (inRequestsCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        modifier = Modifier.offset(x = -4.dp, y = 8.dp)
                    ) { Text(text = "$inRequestsCount") }
                }
            ) {
                Button(onClick = { navController.navigate("friendrequests") }) {
                    Text("Friend requests")
                }
            }
        } else {
            Button(onClick = { navController.navigate("friendrequests") }) {
                Text("Friend requests")
            }
        }

        Button(onClick = onListOfFriendsClicked) {
            Text(text = "List of friends")
        }

        Button(onClick = onLogoutClicked) {
            Text(text = "Log out")
        }
    }

    BackHandler() {
        onLogoutClicked()
    }
}

@Composable
fun CounterBadge(
    count: Int,
) {
    Box(
        modifier = Modifier.background(Color.Red, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$count")
    }
}

@Preview
@Composable
fun MainMenuPreview() {
    TicTakToeTheme() {
        MainMenu(
            viewModel = PreviewViewModel(),
            navController = rememberNavController()
        )
    }
}