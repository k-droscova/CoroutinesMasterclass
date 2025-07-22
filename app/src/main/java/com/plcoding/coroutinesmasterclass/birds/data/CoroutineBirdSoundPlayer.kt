package com.plcoding.coroutinesmasterclass.birds.data

import com.plcoding.coroutinesmasterclass.birds.domain.Bird
import com.plcoding.coroutinesmasterclass.birds.domain.BirdSoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineBirdSoundPlayer: BirdSoundPlayer {
    private var currentJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun playBirdSound(bird: Bird) {
        currentJob?.cancel()
        currentJob = scope.launch {
            while (true) {
                println(bird.sound)
                delay(bird.intervalMillis)
            }
        }
    }

    override fun stopPlaying() {
        currentJob?.cancel()
        currentJob = null
    }

    override fun stop() {
        stopPlaying()
        scope.cancel().also { println("Scope cancelled") }
    }
}