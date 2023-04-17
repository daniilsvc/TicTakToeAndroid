package com.example.tictaktoe.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun FriendRequests(
    viewModel: TicTakToeViewModel,
    navController: NavController,
) {
}

@Preview
@Composable
fun FriendRequestsPreview() {
    FriendRequests(
        viewModel = PreviewViewModel(),
        navController = rememberNavController()
    )
}