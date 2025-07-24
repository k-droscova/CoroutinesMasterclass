package com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LeaderboardLimitedParalelism @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val coroutineScope: CoroutineScope = CoroutineScope(
        Dispatchers.IO.limitedParallelism(
            1
        ) + SupervisorJob()
    )
): Leaderboard {
    private val scores = hashMapOf<String, Int>()
    private val listeners = mutableListOf<LeaderboardListener>()

    override suspend fun updateScore(playerName: String, score: Int) {
        withContext(coroutineScope.coroutineContext) {
            scores[playerName] = score
            val topThree = scores
                .entries
                .sortedByDescending { it.value }
                .take(3)
                .withIndex()
                .joinToString("\n") { (index, entry) ->
                    "#${index + 1} is ${entry.key} with ${entry.value} points"
                }
            notifyListeners(topThree)
        }
    }

    override suspend fun addListener(listener: LeaderboardListener) {
        coroutineScope.launch {
            listeners.add(listener)
        }
    }
    override suspend fun removeListener(listener: LeaderboardListener) {
        coroutineScope.launch {
            listeners.remove(listener)
        }
    }

    private suspend fun notifyListeners(leaderboard: String) {
        withContext(coroutineScope.coroutineContext) {
            listeners.forEach { listener ->
                listener.onLeaderboardUpdated(leaderboard)
            }
        }
    }
}