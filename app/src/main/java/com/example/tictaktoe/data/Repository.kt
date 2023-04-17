package com.example.tictaktoe.data

import android.util.Log
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.livequery.ParseLiveQueryClient
import com.parse.livequery.SubscriptionHandling
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

private const val TAG = "Repository"

interface AppRepository {
    suspend fun getUsersBySubstring(sub: String): List<String>
    suspend fun removeFriend(username: String, friend: String)
    suspend fun addNewFriendPair(username: String, friend: String)
    fun subscribeLiveQuery(
        username: String,
        gameCreateEnterHandler: (Game) -> Unit,
        gameDeleteLeaveHandler: (Game) -> Unit,
        gameUpdateHandler: (Game) -> Unit,
        friendCreateEnterHandler: (Friend) -> Unit,
        friendDeleteLeaveHandler: (Friend) -> Unit,
    )

    fun unsubscribeLiveQuery()
    fun subscribeGameUpdate(
        username: String,
        gameId: String,
        updateCurrentGame: (ParseObject) -> Unit,
    )

    fun unsubscribeGameUpdate()
    suspend fun requestFriends(username: String): List<Friend>
    suspend fun requestGames(username: String): List<Game>
    suspend fun updateGame(gameId: String, state: String)
}

class TicTakToeRepository : AppRepository {

    private val liveQueryClient = ParseLiveQueryClient.Factory.getClient()
    private var mainQueryGames: ParseQuery<ParseObject>? = null
    private var mainQueryFriends: ParseQuery<ParseObject>? = null
    private var gameUpdateQuery: ParseQuery<ParseObject>? = null

    override fun subscribeLiveQuery(
        username: String,
        gameCreateEnterHandler: (Game) -> Unit,
        gameDeleteLeaveHandler: (Game) -> Unit,
        gameUpdateHandler: (Game) -> Unit,
        friendCreateEnterHandler: (Friend) -> Unit,
        friendDeleteLeaveHandler: (Friend) -> Unit,
    ) {
        val query1Games = ParseQuery.getQuery<ParseObject>("Games")
            .whereEqualTo("userN", username)
        val query2Games = ParseQuery.getQuery<ParseObject>("Games")
            .whereEqualTo("userC", username)

        mainQueryGames = ParseQuery.or(listOf(query1Games, query2Games))

        val query1Friends = ParseQuery.getQuery<ParseObject>("Friends")
            .whereEqualTo("user1", username)
        val query2Friends = ParseQuery.getQuery<ParseObject>("Friends")
            .whereEqualTo("user2", username)

        mainQueryFriends = ParseQuery.or(listOf(query1Friends, query2Friends))

        val subGames = liveQueryClient?.subscribe(mainQueryGames)
        subGames?.handleSubscribe {
            subGames.handleEvents { _, event, pobj ->
                when (event) {
                    SubscriptionHandling.Event.CREATE,
                    SubscriptionHandling.Event.ENTER,
                    ->
                        gameCreateEnterHandler(Game.parseObjToGame(pobj))

                    SubscriptionHandling.Event.DELETE,
                    SubscriptionHandling.Event.LEAVE,
                    ->
                        gameDeleteLeaveHandler(Game.parseObjToGame(pobj))

                    SubscriptionHandling.Event.UPDATE ->
                        gameUpdateHandler(Game.parseObjToGame(pobj))
                }
            }
        }

        val subFriends = liveQueryClient?.subscribe(mainQueryFriends)
        subFriends?.handleSubscribe {
            subFriends.handleEvents { _, event, pobj ->
                when (event) {
                    SubscriptionHandling.Event.CREATE,
                    SubscriptionHandling.Event.ENTER,
                    -> friendCreateEnterHandler(Friend.parseObjectToFriend(username, pobj))

                    SubscriptionHandling.Event.DELETE,
                    SubscriptionHandling.Event.LEAVE,
                    -> friendDeleteLeaveHandler(Friend.parseObjectToFriend(username, pobj))

                    else -> {}
                }
            }
        }
    }

    override suspend fun requestFriends(username: String): List<Friend> =
        withContext(Dispatchers.IO) {
            val friendsTable1: ParseQuery<ParseObject> = ParseQuery.getQuery("Friends")
            val queryFriends1 = friendsTable1.whereEqualTo("user1", username)

            val friendsTable2: ParseQuery<ParseObject> = ParseQuery.getQuery("Friends")
            val queryFriends2 = friendsTable2.whereEqualTo("user2", username)

            val mainQueryFriends = ParseQuery.or(listOf(queryFriends1, queryFriends2)).find()

            // TODO: throw exceptions except of "!!"
            mainQueryFriends.map { Friend.parseObjectToFriend(username, it) }
        }

    override suspend fun requestGames(username: String): List<Game> {
        val query1 = ParseQuery.getQuery<ParseObject>("Games")
            .whereEqualTo("userN", username)
        val query2 = ParseQuery.getQuery<ParseObject>("Games")
            .whereEqualTo("userC", username)

        val query = ParseQuery.or(listOf(query1, query2)).find()

        // TODO: try to get rid of "!!"
        return query.map { Game.parseObjToGame(it) }
    }

    override fun unsubscribeLiveQuery() {
        liveQueryClient?.unsubscribe(mainQueryFriends)
        liveQueryClient?.unsubscribe(mainQueryGames)
    }

    override fun subscribeGameUpdate(
        username: String, gameId: String,
        updateCurrentGame: (ParseObject) -> Unit,
    ) {
        Log.d(TAG, "Subscrib particular game updates (username=$username, gameId=$gameId)")
        val query1 = ParseQuery.getQuery<ParseObject>("Games")
            .whereEqualTo("userC", username)

        val query2 = ParseQuery.getQuery<ParseObject>("Games")
            .whereEqualTo("userN", username)

        gameUpdateQuery = ParseQuery.or(listOf(query1, query2))

        val sub = liveQueryClient?.subscribe(gameUpdateQuery)
        sub?.handleSubscribe {
            sub?.handleEvent(SubscriptionHandling.Event.UPDATE) {
                    pq: ParseQuery<ParseObject>,
                    obj: ParseObject,
                ->
                Log.d(TAG, "Updating game (gameId = $gameId)")
                updateCurrentGame(obj)
            }
        }
    }

    override fun unsubscribeGameUpdate() {
        Log.d(TAG, "Unsubscribe particular game updates")
        liveQueryClient?.unsubscribe(gameUpdateQuery)
    }

    override suspend fun getUsersBySubstring(sub: String): List<String> =
        withContext(Dispatchers.IO) {
            // TODO: throw exceptions exceptof "!!"
            val query: ParseQuery<ParseUser> = ParseUser.getQuery()
            query.whereContains("username", sub)
            query.find().map { ob -> ob.getString("username")!! }
        }

    override suspend fun removeFriend(username: String, friend: String): Unit =
        withContext(Dispatchers.IO) {
            val table: ParseQuery<ParseObject> = ParseQuery.getQuery("Friends")
            val query = table.whereEqualTo("user1", username).whereEqualTo("user2", friend)
            try {
                var queryRes = query.find()
                val data = queryRes.first()
                // TODO: change exception handling, get rid of Log
                data.deleteInBackground { e ->
                    if (e != null) {
                        Log.d(TAG, "Error: ${e.message}")
                    }
                }
            } catch (e: NoSuchElementException) {
                Log.d(TAG, "Error, NoSuchElementException: ${e.message}")
            } catch (e: ParseException) {
                Log.d(TAG, "Error, ParseException: ${e.message}")
            }
        }

    override suspend fun addNewFriendPair(username: String, friend: String): Unit =
        withContext(Dispatchers.IO) {
            val table1: ParseQuery<ParseObject> = ParseQuery.getQuery("Friends")
            val query1 = table1.whereEqualTo("user1", username).whereEqualTo("user2", friend)

            val table2: ParseQuery<ParseObject> = ParseQuery.getQuery("Friends")
            val query2 = table2.whereEqualTo("user1", friend).whereEqualTo("user2", username)

            try {
                // check if the pair already in Friends table
                val count = ParseQuery.or(listOf(query1, query2)).count()
                if (count > 0) {
                    Log.d(TAG, "Users \"$username\" and \"$friend\" are already friends")
                } else {
                    // add new friends pair
                    val friends: ParseObject = ParseObject("Friends")
                    friends.put("user1", username)
                    friends.put("user2", friend)
                    friends.save()
                }
            } catch (e: ParseException) {
                Log.d(TAG, "Exception, ParseException: ${e.message}")
            }
        }

    override suspend fun updateGame(gameId: String, state: String) = withContext(Dispatchers.IO) {
        val ob = ParseObject("Games")
        ob.objectId = gameId
        ob.put("State", state)
        ob.saveInBackground { e ->
            if (e != null) {
                // TODO: maybe this could be done better?
                Log.d(TAG, "updateGame, error: ${e.message}")
            }
        }
    }
}

class MockRepository() : AppRepository {
    override fun subscribeGameUpdate(
        username: String,
        gameId: String,
        updateCurrentGame: (ParseObject) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun unsubscribeGameUpdate() {
        TODO("Not yet implemented")
    }

    override suspend fun updateGame(gameId: String, state: String) {
        TODO("Not yet implemented")
    }

    override suspend fun requestFriends(username: String): List<Friend> {
        return listOf(
            Friend("username1", false, false, null),
            Friend("username2", false, false, null),
            Friend("username3", false, false, null),
            Friend("username4", false, false, null)
        )
    }

    override fun subscribeLiveQuery(
        username: String,
        gameCreateEnterHandler: (Game) -> Unit,
        gameDeleteLeaveHandler: (Game) -> Unit,
        gameUpdateHandler: (Game) -> Unit,
        friendCreateEnterHandler: (Friend) -> Unit,
        friendDeleteLeaveHandler: (Friend) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    override fun unsubscribeLiveQuery() {
        TODO("Not yet implemented")
    }

    override suspend fun requestGames(username: String): List<Game> {
        return listOf(
            Game(
                gameId = "gameId1",
                userN = "username1",
                userC = "username3",
                state = "100200111",
                createdAt = Date(1231231287313L),
                updatedAt = Date(1923128731433L),
            ),
            Game(
                gameId = "gameId2",
                userN = "username1",
                userC = "username2",
                state = "000121101",
                createdAt = Date(1823723713123230L),
                updatedAt = Date(1011929382374245L),
            ),
            Game(
                gameId = "gameId3",
                userN = "username3",
                userC = "username2",
                state = "101222111",
                createdAt = Date(19383487347233333L),
                updatedAt = Date(1930340320349234003L),
            )
        )
    }

    override suspend fun getUsersBySubstring(sub: String): List<String> {
        return listOf("username1", "username2")
    }

    override suspend fun removeFriend(username: String, friend: String) {
    }

    override suspend fun addNewFriendPair(username: String, friend: String) {
    }
}