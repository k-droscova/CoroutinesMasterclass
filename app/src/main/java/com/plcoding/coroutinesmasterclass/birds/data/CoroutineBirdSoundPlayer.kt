package com.plcoding.coroutinesmasterclass.birds.data

import com.plcoding.coroutinesmasterclass.birds.domain.Bird
import com.plcoding.coroutinesmasterclass.birds.domain.BirdSoundPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CoroutineBirdSoundPlayer(
    private val scope: CoroutineScope
) : BirdSoundPlayer {

    private var currentJob: Job? = null

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
}