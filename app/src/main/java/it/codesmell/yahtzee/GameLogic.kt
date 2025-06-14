package it.codesmell.yahtzee

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

val rng = Random(System.currentTimeMillis()) //prendo come seed l'ora attuale



var diceAmount : Int = 5
val usedCombos = mutableStateMapOf<String, Int>() // es: "Full house" -> 25
var totalScore by mutableStateOf(0)
var statusText by mutableStateOf("status")
var firstPhase = true //primo tiro, e poi i reroll
var rerollAmount : Int = 2 //quanti reroll si possono fare

var selectedDice : MutableList<Boolean> = mutableStateListOf<Boolean>(false,false,false,false,false) //TODO inizializzalo col numero dinamico






// Giocatore 1

var p1TotalScore by mutableStateOf(0)
var p1BonusJustAwarded by mutableStateOf(false)
var p1UpperSectionBonus by mutableStateOf(0)

// Giocatore 2
var p2TotalScore by mutableStateOf(0)
var p2BonusJustAwarded by mutableStateOf(false)
var p2UpperSectionBonus by mutableStateOf(0)








class GameLogic : ViewModel() {


    var isPlayerOneTurn by mutableStateOf(true) // turno del giocatore, usato anche per il secondo

    val currentTotalScore: Int
        get() = if (isPlayerOneTurn) p1TotalScore else p2TotalScore // score per quando sono in mutli poi al massimo lo sistemo


    var bonusJustAwarded by mutableStateOf(false)

    var multiPlayer by mutableStateOf(false) // parte falso e verrà settato a true
    var upperSectionBonus by mutableStateOf(0)
    val bonusThreshold = 63
    val bonusAmount = 35

    var rollsLeft = 3
    var roundsPlayed = 0
    var gameOver by mutableStateOf(false)
    var bonusShown = false

/////////////////








    val p1UpperSectionScores = mutableStateMapOf<String, Int?>(
        "Ones" to null,
        "Twos" to null,
        "Threes" to null,
        "Fours" to null,
        "Fives" to null,
        "Sixes" to null,
    )
    val p1UsedCombos = mutableStateMapOf<String, Int>() // es: "Full house" -> 25




    val p2UpperSectionScores = mutableStateMapOf<String, Int?>(
        "Ones" to null,
        "Twos" to null,
        "Threes" to null,
        "Fours" to null,
        "Fives" to null,
        "Sixes" to null,
    )
    val p2UsedCombos = mutableStateMapOf<String, Int>() // es: "Full house" -> 25



    val currentUpperSectionScores: MutableMap<String, Int?>
        get() = if (isPlayerOneTurn) p1UpperSectionScores else p2UpperSectionScores

    val currentUsedCombos: MutableMap<String, Int>
        get() = if (isPlayerOneTurn) p1UsedCombos else p2UsedCombos





///////////









    var hasRolled by mutableStateOf(false)

    var selectedDice : MutableList<Boolean> = mutableStateListOf<Boolean>(false,false,false,false,false) //TODO inizializzalo col numero dinamico
    var totalScore by mutableStateOf(0)
    var dice by mutableStateOf(List(diceAmount){0}) //lista di 5 mutable state = 0s


    //Tira un dado a scelta. which: quale dado tirare. size: numero massimo del dado
    fun rollDie(which : Int, size : Int) { //quale dado da tirare, dimensione del dado
        val newList = dice.toMutableList()
        newList[which] = (1..size).random(rng) //numero random tra 1 e size
        dice = newList
    }

    //Tira un dado a scelta, con animazione
    suspend fun rollDieAnimated(which : Int, size : Int){
        var rolls : Int = (3..13).random(rng)
        var delay : Long = 25 //millisecondi
        for(i in 1..rolls){
            //animazione dado che gira prima di ogni roll, durante il delay
            rollDie(which, size)
            hfx?.click(1f)
            delay(delay)
            if(i>rolls-5){delay += 0}//posso modificare il delay
        }
    }

    //Tira i dadi appartenenti a selectedDice
    fun rollSelectedDice() {
        if (rollsLeft <= 0 || gameOver) return

        hasRolled = true

        if (selectedDice.any { it == true }) {
            rollsLeft--

        }


        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until selectedDice.size) {
                if (selectedDice[i]) {
                    withContext(Dispatchers.IO) {
                        rollDieAnimated(i, 6)
                        resetDie(i)
                    }
                }
            }
        }
    }



    //Tira tutti i dadi. per la prima fase
    fun rollAllDice(){
        for(i in 0..diceAmount-1){
            CoroutineScope(Dispatchers.IO).launch {
                rollDieAnimated(i,6)
                resetDie(i)
            }
        }
    }


    //Funzione che si calcola il bonus dei dadini

    fun checkAndApplyUpperSectionBonus(isPlayerOneTurn: Boolean) {
        val playerOneBonus = false
        val playerTwoBonus = false
        val upperTotal = currentUpperSectionScores.values.filterNotNull().sum()
        if (upperTotal >= 1 && upperSectionBonus == 0) {
            upperSectionBonus = 35
            if (multiPlayer) {
                if (isPlayerOneTurn && playerOneBonus == false) {
                    p1TotalScore += upperSectionBonus
                    playerOneBonus == true
                }
                if (isPlayerOneTurn == false && playerTwoBonus == false) {
                    p2TotalScore += upperSectionBonus
                    playerTwoBonus == true
                }
                bonusJustAwarded = true
            }
            else{
                totalScore += 35
            }
        }
    }



    //funzione che si calcola la seconda carta
    fun calculateUpperSectionScore(combo: String, dice: List<Int>): Int {
        val target = when (combo) {
            "Ones" -> 1
            "Twos" -> 2
            "Threes" -> 3
            "Fours" -> 4
            "Fives" -> 5
            "Sixes" -> 6
            else -> 0
        }
        return dice.filter { it == target }.sum()
    }


    fun resetDie(index : Int){
        Log.d("GameLogic", "abbasso il dado $index")
        selectedDice[index] = false
       statusText = "Dadi selezionati: $selectedDice"

    }


    //Aggiungi un dado alla lista dei dadi da tenere
    fun selectDie(which : Int){
        //Controlla se è già tra i selezionati. se si, rimuovilo.
            for(i in 0..selectedDice.size-1){
                if(selectedDice[which] == true){
                    selectedDice[which] = false
                    Log.d("GameLogic", "abbasso il dado $which")
                    //statusText = "Dadi selezionati: $selectedDice"
                    return
                }
            }
        Log.d("GameLogic", "alzo il dado $which")
        selectedDice[which] = true
        statusText = "Dadi selezionati: $selectedDice"
    }

    //pisello e palle

    // sbobba chatgpt da rifare vv----------------------------------------------------------------------------------------------

    //prende una lista di dadi e restituisce il punteggio
    fun calculatePossibleScores(dice: List<Int>): Map<String, Int> {

        var valueAmounts : Array<Int> = arrayOf(0, 0, 0, 0, 0, 0)   //numero occorrenze di ciascun valore

        //conto le occorrenze di ciascun valore
        for(i in 0..dice.size-1){
            valueAmounts[dice[i]-1]++
        }

        //somma dei valori dei dadi selezionati
        val sum = dice.sum()

        //restituisce true se c'è almeno un elemento di valueAmounts con valore n
        fun hasNOfAKind(n: Int) : Boolean {return (n in valueAmounts)}
        //restituisce true se c'è almeno un elemento di valueAmounts di valore 2 e uno di valore 3
        fun isFullHouse() : Boolean {return (2 in valueAmounts && 3 in valueAmounts)}
        //hardcoded è brutto ma per 5 dadi è più semplice così
        fun isSmallStraight(): Boolean {
            val unique = dice.distinct().sorted()
            val straights = listOf(
                listOf(1, 2, 3, 4),
                listOf(2, 3, 4, 5),
                listOf(3, 4, 5, 6)
            )
            return straights.any { unique.containsAll(it) }
        }
        fun isLargeStraight(): Boolean {
            val unique = dice.distinct().sorted()
            return unique == listOf(1, 2, 3, 4, 5) || unique == listOf(2, 3, 4, 5, 6)
        }

        return mapOf( //restituisce una mappa di coppie combinazione-punteggio (key-value)
            "Three of a kind" to if (hasNOfAKind(3)) sum else 0,
            "Four of a kind" to if (hasNOfAKind(4)) sum else 0,
            "Full house" to if (isFullHouse()) 25 else 0,
            "Small straight" to if (isSmallStraight()) 30 else 0,
            "Big straight" to if (isLargeStraight()) 40 else 0,
            "Yahtzee!" to if (hasNOfAKind(5)) 50 else 0,
            "Chance" to sum
        )
    }

    fun confirmScore(combo: String, score: Int) {
        val combos = currentUsedCombos
        val upperScores = currentUpperSectionScores

        if (!combos.containsKey(combo) && hasRolled) {
            combos[combo] = score

            if (combo in upperScores) {
                upperScores[combo] = score
            }

            if (multiPlayer) {
                if (isPlayerOneTurn) {
                    checkAndApplyUpperSectionBonus(isPlayerOneTurn)
                    p1TotalScore += score
                    if (rollsLeft <= 0 || gameOver) return

                } else {
                    checkAndApplyUpperSectionBonus(isPlayerOneTurn)
                    p2TotalScore += score
                    if (rollsLeft <= 0 || gameOver) return
                }
            } else {
                totalScore += score
            }

            hasRolled = false
            rollsLeft = 3
            roundsPlayed++


            if (multiPlayer) {
                isPlayerOneTurn = !isPlayerOneTurn
            }


            Log.d("roundplay", "$roundsPlayed")
            Log.d("roundplay", "$multiPlayer")


            checkAndApplyUpperSectionBonus(false)


            if ((multiPlayer && roundsPlayed >= 2) || (!multiPlayer && roundsPlayed >= 2)) {
                gameOver = true
                Log.d("roundplay", "sono nel gameover la partita è finita dio cristo $gameOver")

            }
        }
    }


}
