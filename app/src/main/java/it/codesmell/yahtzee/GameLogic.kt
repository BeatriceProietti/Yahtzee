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


// funzione di reset

    fun resetGame() {
        // Reset variabili di gioco
        isPlayerOneTurn = true

        playerOneBonusAwarded = false
        playerTwoBonusAwarded = false
        bonusJustAwarded = false
        upperSectionBonus = 0

        rollsLeft = 3
        roundsPlayed = 0
        gameOver = false
        bonusShown = false
        hasRolled = false

        // Reset punteggi
        totalScore = 0
        p1TotalScore = 0
        p2TotalScore = 0
        p1UpperSectionBonus = 0
        p2UpperSectionBonus = 0
        p1BonusJustAwarded = false
        p2BonusJustAwarded = false

        // Reset punteggi combo e upper section
        p1UpperSectionScores.keys.forEach { p1UpperSectionScores[it] = null }
        p2UpperSectionScores.keys.forEach { p2UpperSectionScores[it] = null }

        p1UsedCombos.clear()
        p2UsedCombos.clear()
        usedCombos.clear()

        // Reset dadi e selezioni dadi
        dice = List(diceAmount) { 0 }
        selectedDice.clear()
        repeat(diceAmount) { selectedDice.add(false) }

        statusText = "status"
    }


//










    var isPlayerOneTurn by mutableStateOf(true) // turno del giocatore, usato anche per il secondo

    val currentTotalScore: Int
        get() = if (isPlayerOneTurn) p1TotalScore else p2TotalScore // score per quando sono in mutli poi al massimo lo sistemo


    var bonusJustAwarded by mutableStateOf(false)

    var multiPlayer by mutableStateOf(false) // parte falso e verrà settato a true
    var upperSectionBonus by mutableStateOf(0)
    val bonusThreshold = 1
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

        if(gameOver) return
        if (rollsLeft <= 0) return

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
    var playerOneBonusAwarded by mutableStateOf(false)
    var playerTwoBonusAwarded by mutableStateOf(false)
    fun checkAndApplyUpperSectionBonus() {
        val upperTotal = currentUpperSectionScores.values.filterNotNull().sum()

        if (upperTotal >= bonusThreshold) {
            if (multiPlayer) {
                if (isPlayerOneTurn && !playerOneBonusAwarded) {
                    p1TotalScore += bonusAmount
                    playerOneBonusAwarded = true
                    bonusJustAwarded = true
                } else if (!isPlayerOneTurn && !playerTwoBonusAwarded) {
                    p2TotalScore += bonusAmount
                    playerTwoBonusAwarded = true
                    bonusJustAwarded = true
                }
            } else {
                if (upperSectionBonus == 0) {
                    upperSectionBonus = bonusAmount
                    totalScore += bonusAmount
                    bonusJustAwarded = true
                }
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
        val valueCounts = dice.groupingBy { it }.eachCount()  // Esempio: {6=3, 2=2}
        val sum = dice.sum()



        fun hasNOfAKind(n: Int): Boolean {
            return valueCounts.values.any { it >= n }
        }



        fun isSmallStraight(): Boolean {
            val unique = dice.toSet()
            val sequences = listOf(
                setOf(1, 2, 3, 4),
                setOf(2, 3, 4, 5),
                setOf(3, 4, 5, 6)
            )
            return sequences.any { unique.containsAll(it) }
        }

        fun isLargeStraight(): Boolean {
            val unique = dice.toSet()
            return unique == setOf(1, 2, 3, 4, 5) || unique == setOf(2, 3, 4, 5, 6)
        }

        fun isFullHouse(): Boolean {
            Log.d("full", "il full house funziona")
            return valueCounts.values.contains(3) && valueCounts.values.contains(2)
        }

        return mapOf(
            "Three of a kind" to if (hasNOfAKind(3)) sum else 0,
            "Four of a kind" to if (hasNOfAKind(4)) sum else 0,
            "Full house" to if (isFullHouse()) 25 else 0,
            "Small straight" to if (isSmallStraight()) 30 else 0,
            "Big straight" to if (isLargeStraight()) 40 else 0,
            "Yahtzee!" to if (hasNOfAKind(5)) 50 else 0,
            "Chance" to sum
        )
    }


    fun runComboTests() {
        val testCases = listOf(
            Triple(listOf(6, 6, 6, 2, 2), "Full house", 25),
            Triple(listOf(3, 3, 3, 4, 5), "Three of a kind", 18),
            Triple(listOf(4, 4, 4, 4, 1), "Four of a kind", 17),
            Triple(listOf(2, 3, 4, 5, 6), "Big straight", 40),
            Triple(listOf(1, 2, 3, 4, 6), "Small straight", 30),
            Triple(listOf(5, 5, 5, 5, 5), "Yahtzee!", 50),
            Triple(listOf(1, 3, 4, 2, 6), "Chance", 16)
        )

        for ((dice, expectedCombo, expectedScore) in testCases) {
            val result = calculatePossibleScores(dice)
            val actualScore = result[expectedCombo] ?: 0
            if (actualScore == expectedScore) {
                println("✅ PASS: $expectedCombo on $dice → $actualScore")
            } else {
                println("❌ FAIL: $expectedCombo on $dice → got $actualScore, expected $expectedScore")
            }
        }
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
                checkAndApplyUpperSectionBonus()

                if (isPlayerOneTurn) {
                    p1TotalScore += score
                } else {
                    p2TotalScore += score
                }
            } else {
                totalScore += score
            }

            // Reset turn
            rollsLeft = 300
            roundsPlayed++

            if (multiPlayer) {
                isPlayerOneTurn = !isPlayerOneTurn
            }

            Log.d("roundplay", "$roundsPlayed")
            Log.d("rolls", "$rollsLeft")
            Log.d("roundplay", "$multiPlayer")

            checkAndApplyUpperSectionBonus()

            if ((multiPlayer && roundsPlayed >= 2) || (!multiPlayer && roundsPlayed >= 2)) {
                gameOver = true
                Log.d("roundplay", "sono nel gameover la partita è finita dio cristo $gameOver")
            }
        }

        dice = List(diceAmount) { 0 }
        hasRolled = false
    }


}
