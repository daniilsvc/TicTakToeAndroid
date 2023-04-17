package com.example.tictaktoe.data

import com.parse.ParseObject
import java.util.Date

data class Game(
    val gameId: String,
    val userN: String,
    val userC: String,
    val state: String,
    val createdAt: Date?,
    val updatedAt: Date?,
) {
    fun getOponent(name: String): String {
        if (name.equals(userN)) {
            return userC
        } else if (name.equals(userC)) {
            return userN
        } else {
            throw IllegalArgumentException("Illegal argument for Game::getOponent(String)")
        }
    }

    private fun checkWinner(c: Char): Boolean {
        // check rows
        for (row in 0..2) {
            if (state.get(3 * row) == c && state.get(3 * row + 1) == c
                && state.get(3 * row + 2) == c
            )
                return true
        }

        // check columns
        for (col in 0..2) {
            if (state.get(col) == c && state.get(col + 3) == c
                && state.get(col + 6) == c
            )
                return true
        }

        // check diagonals
        if (state.get(0) == c && state.get(4) == c && state.get(8) == c)
            return true

        if (state.get(2) == c && state.get(4) == c && state.get(6) == c)
            return true

        return false
    }

    fun getWinner(): String {
        var res = when {
            checkWinner('1') -> "userC"
            checkWinner('2') -> "userN"
            state.find { it == '0' } == null -> "draw"
            else -> ""
        }

        return res
    }

    fun whosTurn(): String {
        val sum = state.fold(0, { acc, c -> acc + c.code })
        if (sum % 3 == 0) return "userC"
        return "userN"
    }

    fun updateState(index: Int): String {
        if (index !in 0 until state.length) {
            return state
        }

        if (state.get(index) != '0')
            return state

        val chars = state.toCharArray()
        val turn = whosTurn()
        if (turn == "userC") {
            chars[index] = '1'
        } else {
            chars[index] = '2'
        }

        return String(chars)
    }

    companion object {
        fun emptyGame() = Game("", "", "", "000000000", null, null)

        fun parseObjToGame(pobj: ParseObject): Game {
            return Game(
                userC = pobj.getString("userC")!!,
                userN = pobj.getString("userN")!!,
                state = pobj.getString("State")!!,
                gameId = pobj.objectId,
                createdAt = pobj.createdAt,
                updatedAt = pobj.updatedAt
            )
        }
    }
}