package com.example.tictaktoe.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

private const val TAG = "FriendRequests"

@Composable
fun FriendRequests(
    viewModel: TicTakToeViewModel,
    navController: NavController,
) {

    val incomingRequests by viewModel.friendRequestsToYou.collectAsState()
    val outgoingRequests by viewModel.yourFriendRequests.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Incomming friend requests",
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(incomingRequests) { friend ->
                Row(modifier = Modifier.padding(16.dp)) {
                    Card(
                        modifier = Modifier
                            .weight(1.5f)
                            .heightIn(min = 32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = friend.name,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(32.dp)
                            .clickable {
                                Log.d(TAG, "Accept clicked")
                            },
                        elevation = 5.dp,
                        backgroundColor = Color(0x3000ff00)
                            .compositeOver(Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Accept",
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(Modifier.width(4.dp))

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 32.dp)
                            .clickable {
                                Log.d(TAG, "Reject clicked ")
                            },
                        elevation = 5.dp,
                        backgroundColor = Color(0x30ff0000)
                            .compositeOver(Color.White)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Reject",
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Your pending friend requests",
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(outgoingRequests) { friend ->
                val text1 = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append("To: ")
                    }

                    append("${friend.name} ")
                }
                
                val text2 = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append("Sent at: ")
                    }

                    append(friend?.updatedAt?.toString() ?: "")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Card(modifier = Modifier.weight(0.3f)) {
                        Box() {
                            Text(text = text1)
                        }
                    }

                    Spacer(Modifier.width(8.dp))

                    Card(modifier = Modifier.weight(0.3f)) {
                        Box() {
                            Text(text = text2)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back")
        }
    }
}

@Preview
@Composable
fun FriendRequestsPreview() {
    FriendRequests(
        viewModel = PreviewViewModel(),
        navController = rememberNavController()
    )
}