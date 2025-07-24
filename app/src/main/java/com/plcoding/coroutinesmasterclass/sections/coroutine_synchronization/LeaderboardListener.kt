package com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization

fun interface LeaderboardListener {
    fun onLeaderboardUpdated(leaderboard: String)
}