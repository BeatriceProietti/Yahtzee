package it.codesmell.yahtzee

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.random.Random

val rng = Random(System.currentTimeMillis()) //prendo come seed l'ora attuale

var rerollsLeft : Int = 2
var selectedDice : ArrayList<Int> = ArrayList()

class GameLogic : ViewModel() {

    var dice by mutableStateOf(List(5){0}) //lista di 5 mutable state = 0

    //Tira un dado a scelta
    fun rollDie(which : Int, size : Int) { //quale dado da tirare, dimensione del dado
        val newList = dice.toMutableList()
        newList[which] = (1..size).random(rng) //numero random tra 1 e size
        dice = newList
    }

    suspend fun rollDieAnimated(which : Int, size : Int){
        var rolls : Int = (3..13).random(rng)
        var delay : Long = 50 //millisecondi
        for(i in 1..rolls){
            //animazione dado che gira prima di ogni roll, durante il delay
            rollDie(which, size)
            delay(delay)
            hfx?.click(1f)
            if(i>rolls-5){delay += 35}
        }
    }

    fun rollSelectedDice(){
        for(i in 0..selectedDice.size-1){
            CoroutineScope(Dispatchers.IO).launch {
                gameLogic.rollDieAnimated(selectedDice[i],6)
            }
        }
    }

    //Aggiungi un dado alla lista dei dadi da tenere
    fun selectDie(which : Int){
        //Controlla se è già tra i selezionati. se si, rimuovilo.
            for(i in 0..selectedDice.size-1){
                if(which == selectedDice[i]){
                    selectedDice.removeAt(i)
                    return
                }
            }
        selectedDice.add(which)
    }

}