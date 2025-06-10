package it.codesmell.yahtzee

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
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


class GameLogic : ViewModel() {


    var bonusJustAwarded by mutableStateOf(false)

    var upperSectionBonus by mutableStateOf(0)
    val bonusThreshold = 63
    val bonusAmount = 35

    var rollsLeft = 3
    var roundsPlayed = 0
    var gameOver = false
    var bonusShown = false


    val upperSectionScores = mutableStateMapOf<String, Int?>(
        "Ones" to null,
        "Twos" to null,
        "Threes" to null,
        "Fours" to null,
        "Fives" to null,
        "Sixes" to null,
    )




    var hasRolled by mutableStateOf(false)
    val usedCombos = mutableStateMapOf<String, Int>() // es: "Full house" -> 25
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
        rollsLeft--

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

    fun checkAndApplyUpperSectionBonus() {
        val upperTotal = upperSectionScores.values.filterNotNull().sum()
        if (upperTotal >= 63 && upperSectionBonus == 0) {
            upperSectionBonus = 35
            totalScore += 35
            bonusJustAwarded = true
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



    //Gi
    fun playPhase(){

    }

    fun resetDie(index : Int){
        Log.d("GameLogic", "abbasso il dado $index")
        selectedDice[index] = false
       statusText = "Dadi selezionati: $selectedDice"

    }

    fun balatro(): String{

        var valueAmounts : Array<Int> = arrayOf(0, 0, 0, 0, 0, 0)
        var selectedDiceValues : MutableList<Int> = mutableListOf()

        //compongo la lista di dadi selezionati
        for(i in 0..dice.size-1){
            if(selectedDice[i] == true){
                if(dice[i] in 1..6) selectedDiceValues.add(dice[i])
            }
        }

        //conto le occorrenze di ciascun valore
        for(i in 0..selectedDiceValues.size-1){
            valueAmounts[selectedDiceValues[i]-1]++
        }
        statusText = valueAmounts[0].toString() + valueAmounts[1].toString() + valueAmounts[2].toString() + valueAmounts[3].toString() + valueAmounts[4].toString() + valueAmounts[5].toString()


        //le combinazioni combinate vanno controllate prima di quelle semplici!
        if(3 in valueAmounts && 2 in valueAmounts) return "Full House"
        //Doppia coppia ----------------------------------------------
        var counter = 0
        for (i in 0..valueAmounts.size-1) {
            if (valueAmounts[i] == 2) counter++
            if (counter == 2) return "Two Pair"
        }
        //-----------------------------------------------------------


        //Scala----------------------------------------------------------------------------------
        var straightCounter = 1
        var gymbo  = selectedDiceValues
        gymbo.sort() //tocca fare così perchè sort va a modificare la variabile
        for (i in 1..selectedDiceValues.size-1){
            if(gymbo[i-1] == gymbo[i]-1){
                straightCounter++
                Log.d("Sgarunzolo", straightCounter.toString())
                if (straightCounter == 5){return "Straight"}
                else if (straightCounter == 4) {
                    return "Small Straight"
                }
            }

        } //rileva fino a small straight poi straightCounter non sale sopra al 4
        //----------------------------------------------------------------------------------------


        if(2 in valueAmounts) return "Pair"
        if(3 in valueAmounts) return "Three of a kind"
        if(4 in valueAmounts) return "Four of a kind"
        if(5 in valueAmounts ) return "Yahtzee!"

        return "Zillo!"
    }


    //Aggiungi un dado alla lista dei dadi da tenere
    fun selectDie(which : Int){
        //Controlla se è già tra i selezionati. se si, rimuovilo.
            for(i in 0..selectedDice.size-1){
                if(selectedDice[which] == true){
                    selectedDice[which] = false
                    Log.d("GameLogic", "abbasso il dado $which")
                    //statusText = "Dadi selezionati: $selectedDice"
                    balatro()
                    return
                }
            }
        Log.d("GameLogic", "alzo il dado $which")
        selectedDice[which] = true
        statusText = "Dadi selezionati: $selectedDice"
        statusText = balatro()
    }

    //pisello e palle


    fun calculatePossibleScores(dice: List<Int>): Map<String, Int> {
        val counts = dice.groupingBy { it }.eachCount()
        val sum = dice.sum()

        fun hasNOfAKind(n: Int) = counts.any { it.value >= n }
        fun isFullHouse() = counts.size == 2 && counts.values.contains(3)
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
        fun isYahtzee() = dice.size == 5 && counts.any { it.value == 5 }

        return mapOf(
            "Three of a kind" to if (hasNOfAKind(3)) sum else 0,
            "Four of a kind" to if (hasNOfAKind(4)) sum else 0,
            "Full house" to if (isFullHouse()) 25 else 0,
            "Small straight" to if (isSmallStraight()) 30 else 0,
            "Big straight" to if (isLargeStraight()) 40 else 0,
            "Yahtzee!" to if (isYahtzee()) 50 else 0,
            "Chance" to sum
        )
    }

    fun confirmScore(combo: String, score: Int) {
        if (!usedCombos.containsKey(combo) && hasRolled) {
            usedCombos[combo] = score
            totalScore += score

            if (combo in upperSectionScores) {
                upperSectionScores[combo] = score
            }

            hasRolled = false
            rollsLeft = 3
            roundsPlayed++

            if (roundsPlayed >= 13) { //finisci la partita
                gameOver = true
            }
        }
    }






}

enum class ComboCategory {
    THREE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE,
    SMALL_STRAIGHT, LARGE_STRAIGHT, YAHTZEE, CHANCE
}

class ScoreCard {
    val scores = mutableMapOf<ComboCategory, Int?>()

    fun setScore(category: ComboCategory, dice: List<Int>) {
        if (scores[category] == null && gameLogic.hasRolled) {
            scores[category] = calculateScore(category, dice)
        }
    }

    fun getScore(category: ComboCategory): Int? = scores[category]

    fun totalScore(): Int = scores.values.filterNotNull().sum()

    private fun calculateScore(category: ComboCategory, dice: List<Int>): Int {
        val counts = dice.groupingBy { it }.eachCount()
        val unique = dice.distinct().sorted()

        fun isStraight(length: Int): Boolean {
            var max = 1
            var cur = 1
            for (i in 1 until unique.size) {
                if (unique[i] == unique[i - 1] + 1) {
                    cur++
                    max = maxOf(max, cur)
                } else {
                    cur = 1
                }
            }
            return max >= length
        }

        if (dice.size < 5 && category == ComboCategory.YAHTZEE) return 0
        return when (category) {
            ComboCategory.THREE_OF_A_KIND ->
                if (counts.any { it.value >= 3 }) dice.sum() else 0
            ComboCategory.FOUR_OF_A_KIND ->
                if (counts.any { it.value >= 4 }) dice.sum() else 0
            ComboCategory.FULL_HOUSE ->
                if (counts.values.contains(3) && counts.values.contains(2)) 25 else 0
            ComboCategory.SMALL_STRAIGHT ->
                if (isStraight(4)) 30 else 0
            ComboCategory.LARGE_STRAIGHT ->
                if (isStraight(5)) 40 else 0
            ComboCategory.YAHTZEE ->
                if (counts.any { it.value == 5 }) 50 else 0  // **qui è essenziale `== 5`**
            ComboCategory.CHANCE -> dice.sum()
        }
    }




}
