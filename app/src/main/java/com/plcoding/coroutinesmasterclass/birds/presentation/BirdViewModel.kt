package com.plcoding.coroutinesmasterclass.birds.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.coroutinesmasterclass.birds.data.CoroutineBirdSoundPlayer
import com.plcoding.coroutinesmasterclass.birds.domain.Bird
import com.plcoding.coroutinesmasterclass.birds.domain.BirdSoundPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BirdViewModel : ViewModel() {
    private val birdPlayer: BirdSoundPlayer = CoroutineBirdSoundPlayer(viewModelScope)
    private val _currentBird = MutableStateFlow<Bird?>(null)
    val currentBird = _currentBird.asStateFlow()

    fun onBirdClicked(bird: Bird) {
        if (_currentBird.value == bird) {
            _currentBird.value = null
            birdPlayer.stopPlaying()
            return
        }
        _currentBird.value = bird
        birdPlayer.playBirdSound(bird)
    }

    override fun onCleared() {
        super.onCleared()
        birdPlayer.stopPlaying()
    }
}