package it.codesmell.yahtzee

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList // Import necessario
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

var selectedDice : MutableList<Boolean> = mutableStateListOf<Boolean>(false,false,false,false,false) //TODO inizializzalo col numero dinamico

// Enum per rappresentare le combinazioni di poker
enum class PokerCombination {
    NONE, // Nessuna combinazione rilevata
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    STRAIGHT, // Scala (non specifica se piccola o grande, puoi estendere)
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND // O "Poker" nel caso dei dadi
}

class GameLogic : ViewModel() {

    // La lista dei valori attuali dei dadi. È un List<Int> gestito da mutableStateOf.
    var dice by mutableStateOf(List(diceAmount){0})

    // Tira un dado a scelta. which: quale dado tirare. size: numero massimo del dado
    fun rollDie(which : Int, size : Int) {
        val newList = dice.toMutableList()
        newList[which] = (1..size).random(rng) //numero random tra 1 e size
        dice = newList
    }

    // Tira un dado a scelta, con animazione
    suspend fun rollDieAnimated(which : Int, size : Int){
        var rolls : Int = (3..13).random(rng)
        var delay : Long = 50 //millisecondi
        for(i in 1..rolls){
            //animazione dado che gira prima di ogni roll, durante il delay
            rollDie(which, size)
            // hfx?.click(1f) // Assumendo che hfx sia gestito esternamente per effetti sonori
            delay(delay)
            if(i > rolls - 5){delay += 35}
        }
    }

    // Tira i dadi appartenenti a selectedDice
    fun rollSelectedDice(){
        for(i in 0 until selectedDice.size){ // Usiamo 'until' per chiarezza
            CoroutineScope(Dispatchers.IO).launch {
                if(selectedDice[i]) { // Se il dado è selezionato
                    rollDieAnimated(i,6)
                    resetDie(i) // Resetta lo stato di selezione dopo il tiro
                }
            }
        }
    }

    // Tira tutti i dadi. per la prima fase
    fun rollAllDice(){
        for(i in 0 until diceAmount){ // Usiamo 'until' per chiarezza
            CoroutineScope(Dispatchers.IO).launch {
                rollDieAnimated(i,6)
                resetDie(i) // Resetta lo stato di selezione dopo il tiro
            }
        }
    }

    // Funzione per la logica di gioco della fase (es. fine turno, calcolo punteggio)
    fun playPhase(){
        // Qui potresti chiamare getCombinationOfSelectedDice() o balatro()
        // per determinare il punteggio o la combinazione attuale.
    }

    // Resetta lo stato di selezione di un dado specifico
    fun resetDie(index : Int){
        Log.d("GameLogic", "abbasso il dado $index")
        selectedDice[index] = false
        //statusText = "Dadi selezionati: $selectedDice" // Commentato per evitare sovrascritture rapide
    }

    /**
     * Funzione per rilevare la combinazione di poker più alta tra i dadi selezionati.
     * Questa funzione è la versione robusta per il rilevamento delle combinazioni.
     *
     * @return La PokerCombination più alta rilevata per i dadi selezionati.
     */
    fun balatro(): PokerCombination {
        // Estrai i valori dei dadi selezionati
        val activeDiceValues = mutableListOf<Int>()
        for (i in selectedDice.indices) {
            // Assicurati che l'indice sia valido per entrambi gli elenchi
            if (selectedDice[i] && i < dice.size) {
                activeDiceValues.add(dice[i])
            }
        }

        // Se non ci sono dadi selezionati, non c'è combinazione
        if (activeDiceValues.isEmpty()) {
            return PokerCombination.NONE
        }

        // Ordina i dadi per facilitare il rilevamento delle combinazioni
        val sortedValues = activeDiceValues.sorted()
        val numDice = sortedValues.size

        // Controlla le frequenze di ogni numero
        val counts = sortedValues.groupingBy { it }.eachCount()

        // Funzione helper per controllare n-of-a-kind
        fun hasNOfAKind(n: Int) = counts.values.any { it >= n }
        fun countNOfAKind(n: Int) = counts.values.count { it >= n }

        // Rilevamento delle combinazioni in ordine di priorità (dalla più alta alla più bassa)

        // 5 di un tipo (Poker / Five of a Kind)
        if (numDice >= 5 && hasNOfAKind(5)) {
            return PokerCombination.FIVE_OF_A_KIND
        }

        // 4 di un tipo (Four of a Kind)
        if (numDice >= 4 && hasNOfAKind(4)) {
            return PokerCombination.FOUR_OF_A_KIND
        }

        // Full House (3 di un tipo E una coppia)
        // Deve avere esattamente un tris e una coppia distinta
        if (numDice >= 5) {
            val threeOfAKindCount = counts.values.count { it == 3 }
            val pairCount = counts.values.count { it == 2 }
            if (threeOfAKindCount == 1 && pairCount == 1) {
                return PokerCombination.FULL_HOUSE
            }
        }

        // Scala (Straight)
        // Controlla se i valori sono consecutivi e unici
        if (numDice >= 5) {
            val uniqueSortedValues = sortedValues.distinct()
            if (uniqueSortedValues.size == numDice &&
                uniqueSortedValues.last() - uniqueSortedValues.first() == numDice - 1) {
                return PokerCombination.STRAIGHT
            }
        }

        // 3 di un tipo (Three of a Kind)
        if (numDice >= 3 && hasNOfAKind(3)) {
            return PokerCombination.THREE_OF_A_KIND
        }

        // Doppia coppia (Two Pair)
        if (numDice >= 4) {
            val pairs = counts.values.count { it >= 2 }
            if (pairs >= 2) {
                return PokerCombination.TWO_PAIR
            }
        }

        // Coppia (One Pair)
        if (numDice >= 2 && hasNOfAKind(2)) {
            return PokerCombination.ONE_PAIR
        }

        // Se nessuna delle precedenti, non c'è combinazione specifica
        return PokerCombination.NONE
    }


    /**
     * La tua funzione 'balatro' originale, ora aggiornata per usare la logica di rilevamento
     * delle combinazioni sui dadi selezionati.
     *
     * @return Una Stringa che descrive la combinazione di poker rilevata.
     */

    // Aggiungi o rimuovi un dado dalla lista dei dadi da tenere
    fun selectDie(which : Int){
        // Inverti lo stato di selezione del dado
        selectedDice[which] = !selectedDice[which]
        Log.d("GameLogic", "Dado $which selezionato: ${selectedDice[which]}")
        statusText = "Dadi selezionati: $selectedDice" // Aggiorna lo stato visivo
        val currentCombination = balatro()
        Log.d("GameLogic", "Combinazione dei dadi selezionati: $currentCombination")
        statusText = "Combinazione: $currentCombination"
    }

    // Aggiorna l'aspetto dei dadi in base a quale è selezionato (in futuro anche al tipo di dado)
    fun updateDiceColors(){ // Per ora è solo colori, dopo sostituiamo con una grafica carina
        // Logica per aggiornare l'UI dei dadi
    }
}
