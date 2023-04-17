package com.example.tictaktoe.data

import com.parse.ParseObject
import java.util.Date

data class Friend(
    val name: String,
    val requestToYou: Boolean,
    val requestByYou: Boolean,
    val updatedAt: Date?,
) {
    companion object {
        fun parseObjectToFriend(username: String, pobj: ParseObject): Friend {
            val user1 = pobj.getString("user1")!!
            val user2 = pobj.getString("user2")!!
            val requestedBy = pobj.getInt("requestedBy")!!
            val updatedAt = pobj.updatedAt

            var requestToYou = false
            var requestedByYou = false
            var friendName = ""

            if (username.equals(user1)) {
                friendName = user2
                if (requestedBy == 1) {
                    requestedByYou = true
                } else if (requestedBy == 2) {
                    requestToYou = true
                }

            } else if (username.equals(user2)) {
                friendName = user1
                if (requestedBy == 1) {
                    requestToYou = true
                } else if (requestedBy == 2) {
                    requestedByYou = true
                }

            } else {
                throw IllegalArgumentException("ParseObject doesn't contain usernme=$username.")
            }

            return Friend(
                name = friendName,
                updatedAt = updatedAt,
                requestToYou = requestToYou,
                requestByYou = requestedByYou
            )
        }
    }
}