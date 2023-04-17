package com.example.tictaktoe.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tictaktoe.ui.theme.TicTakToeTheme
import com.parse.ParseUser

private const val TAG: String = "LoginSignup"

@Composable
fun LoginSugnup(
    navController: NavController,
    viewModel: TicTakToeViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val username by viewModel.username.collectAsState()
    var passwordText by remember { mutableStateOf("") }

    val handleEmptyUserPasswordPair = {
        Toast.makeText(
            context,
            "Username and passowrd are required",
            Toast.LENGTH_SHORT
        ).show()
    }

    val onSignupClicked: () -> Unit = {
        if (username.isEmpty() || passwordText.isEmpty()) {
            handleEmptyUserPasswordPair()
        }

        val user = ParseUser()
        user.username = username
        user.setPassword(passwordText)
        user.signUpInBackground { e ->
            if (e != null) {
                e.printStackTrace()
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                viewModel.onLoginSignup()
                navController.navigate("mainmenu")
            }
        }
    }

    val onLoginClicked: () -> Unit = {
        if (username.isEmpty() || passwordText.isEmpty()) {
            handleEmptyUserPasswordPair()
        }

        ParseUser.logInInBackground(username, passwordText) { parseUser, e ->
            if (parseUser == null) {
                ParseUser.logOut()
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.onLoginSignup()
                navController.navigate("mainmenu")
            }
        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "TicTakToe Game")

        Spacer(modifier = Modifier.size(8.dp))

        TextField(
            modifier = Modifier.widthIn(0.dp, 264.dp),
            value = username,
            onValueChange = { viewModel.setUsername(it) },
            label = { Text("Username") }
        )

        Spacer(modifier = Modifier.size(8.dp))

        TextField(
            modifier = Modifier.widthIn(0.dp, 264.dp),
            value = passwordText,
            onValueChange = { passwordText = it },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.size(16.dp))

        Row {
            Button(onClick = onSignupClicked) {
                Text(text = "Signup")
            }

            Spacer(modifier = Modifier.size(8.dp))

            Button(onClick = onLoginClicked) {
                Text(text = "Login")
            }
        }
    }
}

@Preview
@Composable
fun LoginSignupPreview() {
    TicTakToeTheme {
        LoginSugnup(
            viewModel = PreviewViewModel(),
            navController = rememberNavController())
    }
}