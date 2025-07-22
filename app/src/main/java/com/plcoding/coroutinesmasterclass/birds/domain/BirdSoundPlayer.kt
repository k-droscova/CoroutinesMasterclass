package com.plcoding.coroutinesmasterclass.birds.domain

interface BirdSoundPlayer {
    fun playBirdSound(bird: Bird)
    fun stopPlaying()
    fun stop()
}