package com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization

interface Leaderboard {
    suspend fun updateScore(playerName: String, score: Int)
    suspend fun addListener(listener: LeaderboardListener)
    suspend fun removeListener(listener: LeaderboardListener)
}