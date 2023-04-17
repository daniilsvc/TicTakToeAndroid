package com.example.tictaktoe.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(
    viewModel: TicTakToeViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signuplogin") {
        composable("signuplogin") {
            LoginSugnup(
                viewModel = viewModel,
                navController = navController)
        }

        composable("mainmenu") { backStackEntry ->
            MainMenu(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("listfriends") {
            ListFriends(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("addnewfriend") { backStackEntry ->
            AddNewFriend(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("newgameselectfriend") {
            NewGameBuilder(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("listofgames") {
            ListOfGames(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("gameplayer") {
            GamePlayer(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("listofprevgames") {
            ListOfPrevGames(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}