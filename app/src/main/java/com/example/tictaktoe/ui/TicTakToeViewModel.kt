package com.example.tictaktoe.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tictaktoe.application.TicTakToeApplication
import com.example.tictaktoe.data.AppRepository
import com.example.tictaktoe.data.Friend
import com.example.tictaktoe.data.Game
import com.example.tictaktoe.data.MockRepository
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.livequery.SubscriptionHandling
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date


private const val TAG = "TicTakToeViewModel"

open class TicTakToeViewModel(
    private val repository: AppRepository,
) : ViewModel() {

    private val _username = MutableStateFlow("")
    open val username = _username.asStateFlow()

    private val _friends = MutableStateFlow(mutableListOf<Friend>())
    open val friends = _friends.asStateFlow()

    private val _friendRequestsToYou = MutableStateFlow(mutableListOf<Friend>())
    open val friendRequestsToYou = _friendRequestsToYou.asStateFlow()

    private val _yourFriendRequests = MutableStateFlow(mutableListOf<Friend>())
    open val yourFriendRequests = _yourFriendRequests.asStateFlow()

    private val _inRequestsCount = MutableStateFlow(0)
    open val inRequestsCount = _inRequestsCount.asStateFlow()

    private val _outRequestsCount = MutableStateFlow(0)
    open val outRequestsCount = _outRequestsCount.asStateFlow()

    private val _newFriendCandidateList = MutableStateFlow<List<String>>(listOf())
    open val newFriendCandidateList = _newFriendCandidateList.asStateFlow()

    private val _newGamesCount = MutableStateFlow(0)
    open val newGamesCount = _newGamesCount.asStateFlow()

    private val _newFriendName = MutableStateFlow("")
    open val newFriendName = _newFriendName.asStateFlow()

    private val _selectedGameOption = MutableStateFlow("Noughts")
    open val selectedGameOption = _selectedGameOption.asStateFlow()

    private val _selectedUser = MutableStateFlow("")
    open val selectedUser = _selectedUser.asStateFlow()

    private val _newGames = MutableStateFlow<MutableList<Game>>(mutableListOf())
    open val newGames = _newGames.asStateFlow()

    private val _currentGames = MutableStateFlow<MutableList<Game>>(mutableListOf())
    open val currentGames = _currentGames.asStateFlow()

    private val _prevGames = MutableStateFlow<MutableList<Game>>(mutableListOf())
    open val prevGames = _prevGames.asStateFlow()

    private val _currentGame = MutableStateFlow<Game>(Game.emptyGame())
    open val currentGame = _currentGame.asStateFlow()

    fun onLoginSignup() {

        // prepare callbacks to handle Game data
        val gameCreateEnterHandler: (Game) -> Unit = { game ->
            if (game.state.equals("000000000")) {
                _newGames.value.add(game)
                _newGames.value.sortBy { it.updatedAt }
                _newGamesCount.value++
            } else if (game.getWinner().equals("")) {
                _currentGames.value.add(game)
                _currentGames.value.sortBy { it.updatedAt }
            } else {
                _prevGames.value.add(game)
                _prevGames.value.sortBy { it.updatedAt }
            }
        }

        val gameDeleteLeaveHandler: (Game) -> Unit = { game ->
            if (game.state.equals("000000000")) {
                _newGames.value.remove(game)
                _newGamesCount.value--
            } else if (game.getWinner().equals("")) {
                _currentGames.value.remove(game)
            } else {
                _prevGames.value.remove(game)
            }

        }

        val gameUpdateHandler: (Game) -> Unit = { game ->
            if (game.state.equals("000000000")) {
                _newGames.value.removeIf { it.gameId.equals(game.gameId) }
                _newGames.value.add(game)
                _newGames.value.sortBy { it.updatedAt }
            } else if (game.getWinner().equals("")) {
                _currentGames.value.removeIf { it.gameId.equals(game.gameId) }
                _currentGames.value.add(game)
                _currentGames.value.sortBy { it.updatedAt }
            } else {
                _prevGames.value.removeIf { it.gameId.equals(game.gameId) }
                _prevGames.value.add(game)
                _prevGames.value.sortBy { it.updatedAt }
            }
        }

        // prepare callbacks to handle Friend data
        val friendCreateEnterHandler: (Friend) -> Unit = { friend ->
            if (friend.requestByYou) {
                _yourFriendRequests.value.add(friend)
                _yourFriendRequests.value.sortBy { it.updatedAt }
                _outRequestsCount.value++
            } else if (friend.requestToYou) {
                _friendRequestsToYou.value.add(friend)
                _friendRequestsToYou.value.sortBy { it.updatedAt }
                _inRequestsCount.value++
            } else {
                _friends.value.add(friend)
                _friends.value.sortBy { it.updatedAt }
            }
        }

        val friendDeleteLeaveHandler: (Friend) -> Unit = { friend ->
            if (friend.requestByYou) {
                _yourFriendRequests.value.remove(friend)
                _outRequestsCount.value--
            } else if (friend.requestToYou) {
                _friendRequestsToYou.value.remove(friend)
                _inRequestsCount.value--
            } else {
                _friends.value.remove(friend)
            }
        }

        // subscribe to live query updates of games and friends
        repository.subscribeLiveQuery(
            username = username.value,
            gameCreateEnterHandler = gameCreateEnterHandler,
            gameDeleteLeaveHandler = gameDeleteLeaveHandler,
            gameUpdateHandler = gameUpdateHandler,
            friendCreateEnterHandler = friendCreateEnterHandler,
            friendDeleteLeaveHandler = friendDeleteLeaveHandler
        )

        // initial request list of games and friends
        viewModelScope.launch {
            val games = repository.requestGames(username.value)
            val friends = repository.requestFriends(username.value)

            for (game in games) {
                if (game.state.equals("000000000")) {
                    _newGames.value.add(game)
                } else if (game.getWinner().equals("")) {
                    _currentGames.value.add(game)
                } else {
                    _prevGames.value.add(game)
                }
            }

            for (friend in friends) {
                if (friend.requestByYou) {
                    _yourFriendRequests.value.add(friend)
                    _outRequestsCount.value++
                } else if (friend.requestToYou) {
                    _friendRequestsToYou.value.add(friend)
                    _inRequestsCount.value++
                } else {
                    _friends.value.add(friend)
                }
            }

            _newGames.value.sortBy { it.updatedAt }
            _prevGames.value.sortBy { it.updatedAt }
            _currentGames.value.sortBy { it.updatedAt }
            _newGamesCount.value = newGames.value.size
        }
    }

    fun onLogout() {
        repository.unsubscribeLiveQuery()
        _username.value = ""
        _newGames.value.clear()
        _currentGames.value.clear()
        _prevGames.value.clear()
        _friends.value.clear()
        _newGamesCount.value = 0
        _outRequestsCount.value = 0
        _inRequestsCount.value = 0
    }

    fun onGameEnter() {

        repository.subscribeGameUpdate(
            username = username.value,
            gameId = currentGame.value.gameId,
            updateCurrentGame = {
                _currentGame.value = Game.parseObjToGame(it)
            }
        )
    }

    fun onGameLeave() {
        repository.unsubscribeGameUpdate()
    }

    fun setUsername(username: String) {
        _username.value = username
    }

    fun removeFriend(friendName: String) = viewModelScope.launch {
        // TOOD: implement
    }

    fun getUsersBySubstring(sub: String) = viewModelScope.launch {
        if (sub.isEmpty()) {
            _newFriendCandidateList.value = emptyList()
        } else {
            val users = repository.getUsersBySubstring(sub)
            _newFriendCandidateList.value =
                users.filter { name ->
                    !friends.value.any { it.name.equals(name) }
                            && !name.equals(username.value)
                }
        }
    }

    fun setNewFriendName(newFriendName: String) {
        _newFriendName.value = newFriendName
    }

    fun addNewFriend() = viewModelScope.launch {
        repository.addNewFriendPair(username.value, selectedUser.value)
    }

    fun selectGameOption(option: String) {
        _selectedGameOption.value = option
    }

    fun selectUser(userName: String) {
        _selectedUser.value = userName
    }

    fun setGame(game: Game) {
        _currentGame.value = game
    }

    // functions for working with game field
    fun processGameClick(position: Offset, size: IntSize) {
        val cellIndex = getCellIndex(position, size)
        Log.d(TAG, "Index: $cellIndex")

        val userTurn = currentGame.value.whosTurn()
        Log.d(TAG, "User turn: $userTurn")
        Log.d(TAG, "current game userC=${currentGame.value.userC}")
        Log.d(TAG, "current game userN=${currentGame.value.userN}")
        Log.d(TAG, "username=${username.value}")

        // if not your turn - do nothing
        if (userTurn == "userC" && currentGame.value.userC != username.value
            || userTurn == "userN" && currentGame.value.userN != username.value
        )
            return

        // if game over (have winner or draw) - do nothing
        if (currentGame.value.getWinner().isNotEmpty())
            return

        // if cell is already occupied - do nothing
        if (currentGame.value.state.get(cellIndex) != '0')
            return

        // otherwise - make a move and update
        // Game entity in data layer (back4app)
        viewModelScope.launch {
            repository.updateGame(
                currentGame.value.gameId,
                currentGame.value.updateState(cellIndex)
            )
        }
    }

    private fun getCellIndex(position: Offset, size: IntSize): Int {
        val xpart = (position.x.toInt()) / (size.width / 3)
        val ypart = (position.y.toInt()) / (size.height / 3)
        val res = when {
            xpart == 0 && ypart == 0 -> 0
            xpart == 1 && ypart == 0 -> 1
            xpart == 2 && ypart == 0 -> 2
            xpart == 0 && ypart == 1 -> 3
            xpart == 1 && ypart == 1 -> 4
            xpart == 2 && ypart == 1 -> 5
            xpart == 0 && ypart == 2 -> 6
            xpart == 1 && ypart == 2 -> 7
            xpart == 2 && ypart == 2 -> 8
            else -> throw IllegalArgumentException("getCellIndex - position not within board size")
        }

        return res
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val rep = (this[APPLICATION_KEY] as TicTakToeApplication).repository
                TicTakToeViewModel(rep)
            }
        }
    }
}

class PreviewViewModel() : TicTakToeViewModel(MockRepository()) {
    override val username = MutableStateFlow("daniil")
    override val friends = MutableStateFlow(
        mutableListOf(
            Friend("friend one", false, false, null),
            Friend("friend two", false, false, null),
            Friend("friend three", false, false, null),
            Friend("friend four", false, false, null)
        )
    )

    override val friendRequestsToYou = MutableStateFlow(
        mutableListOf(
            Friend("friend one", true, false, null),
            Friend("friend two", true, false, null),
            Friend("friend three", true, false, null),
            Friend("friend four", true, false, null)
        )
    )

    override val yourFriendRequests = MutableStateFlow(
        mutableListOf(
            Friend("friend one", false, true, null),
            Friend("friend two", false, true, null),
            Friend("friend three", false, true, null),
            Friend("friend four", false, true, null)
        )
    )

    override val currentGame = MutableStateFlow(
        Game("", "", "", "120122001", null, null)
    )

    override val newGames = MutableStateFlow(
        mutableListOf<Game>(
            Game(
                gameId = "gameId1", userN = "user1", userC = "daniil",
                state = "000000000", createdAt = null, updatedAt = null
            ),

            Game(
                gameId = "gameId2", userN = "user2", userC = "daniil",
                state = "000000000", createdAt = null, updatedAt = null
            ),

            Game(
                gameId = "gameId3", userN = "daniil", userC = "user3",
                state = "000000000", createdAt = null, updatedAt = null
            ),
        )
    )

    override val currentGames = MutableStateFlow(
        mutableListOf<Game>(
            Game(
                gameId = "gameId3", userN = "user1", userC = "daniil",
                state = "000000012", createdAt = null, updatedAt = null
            ),

            Game(
                gameId = "gameId4", userN = "user1", userC = "daniil",
                state = "022101000", createdAt = null, updatedAt = null
            ),

            Game(
                gameId = "gameId5", userN = "daniil", userC = "user2",
                state = "000000120", createdAt = null, updatedAt = null
            ),
        )
    )

    override val prevGames = MutableStateFlow(
        mutableListOf<Game>(
            Game(
                gameId = "gameId6", userN = "user2", userC = "daniil",
                state = "000000000", createdAt = null, updatedAt = null
            ),

            Game(
                gameId = "gameId7", userN = "user1", userC = "daniil",
                state = "000000000", createdAt = null, updatedAt = null
            ),

            Game(
                gameId = "gameId8", userN = "user1", userC = "daniil",
                state = "000000000", createdAt = null, updatedAt = null
            ),
        )
    )

    override val selectedUser = MutableStateFlow("friend two")
    override val newFriendCandidateList = MutableStateFlow(
        listOf(
            "name1", "name2", "name3",
            "name4", "name5", "name6"
        )
    )

    override val newGamesCount = MutableStateFlow(12)
    override val newFriendName = MutableStateFlow("as")
}