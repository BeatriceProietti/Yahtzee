package it.codesmell.yahtzee

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.random.Random

val rng = Random(System.currentTimeMillis()) //prendo come seed l'ora attuale



var diceAmount : Int = 5

var statusText by mutableStateOf("status")
var firstPhase = true //primo tiro, e poi i reroll
var rerollAmount : Int = 2 //quanti reroll si possono fare
var selectedDice : ArrayList<Int> = ArrayList()

var upDice : MutableList<Boolean> = mutableListOf<Boolean>(false,false,false,false,false) //TODO inizializzalo col numero dinamico


class GameLogic : ViewModel() {

    var dice by mutableStateOf(List(diceAmount){0}) //lista di 5 mutable state = 0

    //Tira un dado a scelta. which: quale dado tirare. size: numero massimo del dado
    fun rollDie(which : Int, size : Int) { //quale dado da tirare, dimensione del dado
        val newList = dice.toMutableList()
        newList[which] = (1..size).random(rng) //numero random tra 1 e size
        dice = newList
    }

    //Tira un dado a scelta, con animazione
    suspend fun rollDieAnimated(which : Int, size : Int){
        var rolls : Int = (3..13).random(rng)
        var delay : Long = 50 //millisecondi
        for(i in 1..rolls){
            //animazione dado che gira prima di ogni roll, durante il delay
            rollDie(which, size)
            hfx?.click(1f)
            delay(delay)
            if(i>rolls-5){delay += 35}
        }
    }

    //Tira i dadi appartenenti a selectedDice
    fun rollSelectedDice(){
        for(i in 0..selectedDice.size-1){
            CoroutineScope(Dispatchers.IO).launch {
                rollDieAnimated(selectedDice[i],6)
                resetDice()
                statusText = ""
            }
        }
    }

    //Tira tutti i dadi. per la prima fase
    fun rollAllDice(){
        for(i in 0..diceAmount-1){
            CoroutineScope(Dispatchers.IO).launch {
                rollDieAnimated(i,6)
                resetDice()
            }
        }
    }

    //Gi
    fun playPhase(){

    }

    fun resetDice(){
        for(i in 0..upDice.size-1){
            upDice[i] = false
        }
        selectedDice = ArrayList() //svuoto la selezione
    }


    //Aggiungi un dado alla lista dei dadi da tenere
    fun selectDie(which : Int){
        //Controlla se è già tra i selezionati. se si, rimuovilo.
            for(i in 0..selectedDice.size-1){
                if(which == selectedDice[i]){
                    selectedDice.removeAt(i)
                    upDice[which] = false
                    statusText = "Dadi selezionati: $selectedDice"
                    return
                }
            }
        selectedDice.add(which)
        upDice[which] = true
        statusText = "Dadi selezionati: $selectedDice"
    }

    //Aggiorna l'aspetto dei dadi in base a quale è selezionato (in futuro anche al tipo di dado)
    fun updateDiceColors(){ //Per ora è solo colori, dopo sostituiamo con una grafica carina
        for(i in 0..diceAmount){
            if(i == selectedDice[i]){

            }
        }
    }


}