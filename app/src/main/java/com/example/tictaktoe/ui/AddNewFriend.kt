package com.example.tictaktoe.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.ui.theme.TicTakToeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "AddNewFriend"

@Composable
fun AddNewFriend(
    navController: NavController,
    viewModel: TicTakToeViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val username by viewModel.username.collectAsState()
    val newFriendName by viewModel.newFriendName.collectAsState()
    val friendCandidates by viewModel.newFriendCandidateList.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Add new friend")
        Text(text = "Username: ${username}")
        Spacer(modifier = Modifier.size(64.dp))
        Text(text = "Enter friend's name")
        TextField(
            value = newFriendName,
            onValueChange = { viewModel.setNewFriendName(it) }
        )

        SelectUserList(
            users = friendCandidates,
            selectedUser = selectedUser,
            buttonText = "Add new friend",
            onButtonClicked = {
                viewModel.addNewFriend()
                navController.popBackStack()
            },
            onCardClick = { viewModel.selectUser(it) }
        )
    }

    LaunchedEffect(key1 = newFriendName, block = {
        viewModel.getUsersBySubstring(newFriendName)
    })
}

@Composable
fun FriendCandidateCard(
    name: String,
    onRowClicked: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier.clickable(
                onClick = { onRowClicked(name) },
                indication = rememberRipple(
                    bounded = true,
                    color = MaterialTheme.colors.onSecondary
                ),
                interactionSource = interactionSource
            )
        ) {
            Text(text = name)
        }
    }
}

@Preview
@Composable
fun AddNewFriendPreview() {
    TicTakToeTheme() {
        AddNewFriend(
            viewModel = PreviewViewModel(),
            navController = rememberNavController()
        )
    }
}