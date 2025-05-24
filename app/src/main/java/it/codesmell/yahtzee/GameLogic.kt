package it.codesmell.yahtzee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random

val rng = Random(System.currentTimeMillis()) //prendo come seed l'ora attuale

class GameLogic : ViewModel() {

    var die1 by mutableStateOf(1)

    fun rollDie(size : Int): Int {
        die1 = (1..size).random(rng) //numero random tra 1 e size
        return 0
    }

}