package com.plcoding.coroutinesmasterclass

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization.Leaderboard
import com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization.LeaderboardLimitedParalelism
import com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization.LeaderboardListener
import com.plcoding.coroutinesmasterclass.sections.coroutine_synchronization.LeaderboardMutex
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    private val leaderboard: Leaderboard = LeaderboardMutex()
    private val listener = LeaderboardListener { topScores: String ->
        println("New Top Scores:")
        println(topScores + "\n\n")
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // USE THIS TO RUN YOUR Leaderboard CLASS
        lifecycleScope.launch {
            leaderboard.addListener(listener)
            (1..30).map { index ->
                launch {
                    val playerName = "Player $index"
                    val playerScore = Random.nextInt(1, 10_0000)
                    leaderboard.updateScore(playerName, playerScore)
                }
            }.joinAll()
            println("Completed!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            leaderboard.removeListener(listener)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun Context.getLocation(): Location {
        return suspendCancellableCoroutine { continuation ->
            val locationManager = getSystemService<LocationManager>()!!

            val hasFineLocationPermission = ActivityCompat.checkSelfPermission(
                this@getLocation,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(
                this@getLocation,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val signal = CancellationSignal()
            if (hasFineLocationPermission && hasCoarseLocationPermission) {
                locationManager.getCurrentLocation(
                    LocationManager.NETWORK_PROVIDER,
                    signal,
                    mainExecutor
                ) { location ->
                    println("Got location: $location")
                    continuation.resume(location)
                }
            } else {
                continuation.resumeWithException(
                    RuntimeException("Missing location permission")
                )
            }

            continuation.invokeOnCancellation {
                signal.cancel()
            }
        }
    }
}