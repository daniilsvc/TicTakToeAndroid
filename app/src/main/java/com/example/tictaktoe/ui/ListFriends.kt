package com.example.tictaktoe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.R
import com.example.tictaktoe.ui.theme.TicTakToeTheme

private const val TAG = "ListFriends"

@Composable
fun ListFriends(
    viewModel: TicTakToeViewModel,
    navController: NavController,
) {

    val friends by viewModel.friends.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "List of friends",
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Italic
        )
        LazyColumn() {
            items(friends.toList()) { friend ->
                FriendCard(
                    friendName = friend.name,
                    onRemoveClicked = { viewModel.removeFriend(friend.name) }
                )
            }
        }

        Row() {
            Button(onClick = { }) {
                Text(text = "Back")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { navController.navigate("addnewfriend") }) {
                Text(text = "Add new friend")
            }
        }
    }
}

@Composable
fun FriendCard(
    friendName: String,
    onRemoveClicked: (Int) -> Unit,
) {

    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Row {
            Text(text = friendName)
        }
    }
}

@Preview
@Composable
fun ListFriendsPreview() {
    TicTakToeTheme() {
        ListFriends(
            viewModel = PreviewViewModel(), navController = rememberNavController()
        )
    }
}