package com.plcoding.coroutinesmasterclass.birds.domain

data class Bird(
    val name: String,
    val sound: String,
    val intervalMillis: Long
)