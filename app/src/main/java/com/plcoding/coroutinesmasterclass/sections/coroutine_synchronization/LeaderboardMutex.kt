package com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class LeaderboardMutex(
    private val coroutineScope: CoroutineScope = CoroutineScope(
        Dispatchers.IO + SupervisorJob()
    )
): Leaderboard {
    private val scores = hashMapOf<String, Int>()
    private val scoresMutex = Mutex()
    private val listeners = mutableListOf<LeaderboardListener>()
    private val listenersMutex = Mutex()

    override suspend fun updateScore(playerName: String, score: Int) {
        withContext(coroutineScope.coroutineContext) {
            var topThree = ""
            scoresMutex.withLock {
                scores[playerName] = score
                topThree = scores
                    .entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .withIndex()
                    .joinToString("\n") { (index, entry) ->
                        "#${index + 1} is ${entry.key} with ${entry.value} points"
                    }
            }
            notifyListeners(topThree)
        }
    }

    override suspend fun addListener(listener: LeaderboardListener) {
        listenersMutex.withLock {
            listeners.add(listener)
        }
    }
    override suspend fun removeListener(listener: LeaderboardListener) {
        listenersMutex.withLock {
            listeners.remove(listener)
        }
    }

    private suspend fun notifyListeners(leaderboard: String) {
        listenersMutex.withLock {
            listeners.forEach { listener ->
                listener.onLeaderboardUpdated(leaderboard)
            }
        }
    }
}