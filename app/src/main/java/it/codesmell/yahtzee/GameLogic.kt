package it.codesmell.yahtzee

import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_HEAVY_CLICK
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import it.codesmell.yahtzee.dao.TableScore
import it.codesmell.yahtzee.dao.TableScoreDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.random.Random


class GameLogic : ViewModel() {

    var dao: TableScoreDao? = null

    val rng = Random(System.currentTimeMillis()) //prendo come seed l'ora attuale

    var statusText by mutableStateOf("status")

    //parametri gamemode
    var multiPlayer by mutableStateOf(false) // parte falso e verrà settato a true //sostituisci con playerAmount
    var playerAmount by mutableStateOf(1)
    var diceAmount: Int = 5
    val bonusThreshold = 1
    val bonusAmount = 35
    var rollsLeft = 3

    //variabili logica di gioco
    var currentPlayer by mutableStateOf(0) //numero giocatore di cui è il turno parte da 1
    var selectedDice: MutableList<Boolean> = mutableStateListOf<Boolean>(false, false, false, false, false) //TODO inizializzalo col numero dinamico
    var hasRolled by mutableStateOf(false)
    var dice by mutableStateOf(List(diceAmount) { 0 }) //lista di 5 mutable state = 0s
    var roundsPlayed = 0
    var gameOver by mutableStateOf(false)
    var bonusShown = false

    var currentPlayerStatus: PlayerStatus? =
        null //entity PlayerStatus per il giocatore che sta giocando
    var playerStatuses: Array<PlayerStatus> =
        emptyArray() //lista dei playerstatus di tutti i giocatori.  spero non vada fatta mutable




    fun getYahtzee() {
        dice = List(diceAmount) { 1 }
    }

    fun initGame(numOfPlayers: Int) {// si prende il player
        //variabili gamelogic
        gameOver = false
        currentPlayer = 1
        roundsPlayed = 0
        selectedDice = mutableStateListOf<Boolean>(false, false, false, false, false)
        dice = List(diceAmount) { 0 }
        playerAmount = numOfPlayers
        playerStatuses = Array(playerAmount + 1) { PlayerStatus() }
        for (i in 1..playerAmount + 1) {
            Log.d("GameLogic", "inizializzo il giocatore $i")
            //playerStatuses[i] = PlayerStatus() //mi sa che lo fa già quando inizializzo l'array?
            playerStatuses[currentPlayer].playerNo = 1 //numero giocatore
            playerStatuses[currentPlayer].totalScore = 0
            playerStatuses[currentPlayer].upperSectionScores.keys.forEach {
                playerStatuses[currentPlayer].upperSectionScores[it] = null
            }


            playerStatuses[currentPlayer].YahtzeeBonusCount =
                0 //quantità di yahtzee bonus (100p) ottenuti
            playerStatuses[currentPlayer].usedCombos =
                mutableStateMapOf<String, Int>() // es: "Full house" -> 25

            playerStatuses[currentPlayer].bonusJustAwarded = false
            playerStatuses[currentPlayer].upperSectionBonus = 0
        }
    }




//



///////////


    //Tira un dado a scelta. which: quale dado tirare. size: numero massimo del dado
    fun rollDie(which: Int, size: Int) { //quale dado da tirare, dimensione del dado
        val newList = dice.toMutableList()
        newList[which] = (1..size).random(rng) //numero random tra 1 e size
        dice = newList
    }

    //Tira un dado a scelta, con animazione
    suspend fun rollDieAnimated(which: Int, size: Int) {
        var rolls: Int = (3..13).random(rng)
        var delay: Long = 25 //millisecondi
        for (i in 1..rolls) {
            //animazione dado che gira prima di ogni roll, durante il delay
            rollDie(which, size)
            hfx?.click(1f)
            sfx?.diceRoll()
            delay(delay)
            if (i > rolls - 5) {
                delay += 0
            }//posso modificare il delay
        }
    }

    //Tira i dadi appartenenti a selectedDice
    fun rollSelectedDice() {

        if (gameOver) return
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
            checkYahtzeeBonus(dice)
        }
    }


    //Tira tutti i dadi. per la prima fase
    fun rollAllDice() {
        for (i in 0..diceAmount - 1) {
            CoroutineScope(Dispatchers.IO).launch {
                rollDieAnimated(i, 6)
                resetDie(i)
            }
        }
    }

    //abbassa un dado
    fun resetDie(index: Int) {
        Log.d("GameLogic", "abbasso il dado $index")
        selectedDice[index] = false
        statusText = "Dadi selezionati: $selectedDice"
    }

    //Aggiungi un dado alla lista dei dadi da tenere
    fun selectDie(which: Int) {
        //Controlla se è già tra i selezionati. se si, rimuovilo.
        for (i in 0..selectedDice.size - 1) {
            if (selectedDice[which] == true) {
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



    //Funzione che si calcola il bonus dei dadini
    fun checkAndApplyUpperSectionBonus() {
        val upperTotal =
            playerStatuses[currentPlayer].upperSectionScores.values.filterNotNull().sum()
        if (upperTotal >= bonusThreshold && !playerStatuses[currentPlayer].bonusJustAwarded) {
            playerStatuses[currentPlayer].totalScore += 35
            var mealmeal = playerStatuses[currentPlayer].totalScore
            Log.d("totalScore", "$mealmeal")
            playerStatuses[currentPlayer].bonusJustAwarded = true
        }
        Log.d("uppertotal", "$upperTotal")
    }

    //calcolo punteggi upper section
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
        return dice.filter { it == target }
            .sum() //restituisce la somma del valore di tutti i dadi di un certo valore
    }



    fun checkYahtzeeBonus(dice: List<Int>) {
        val valueCounts = dice.groupingBy { it }.eachCount()

        val isYahtzee = valueCounts.values.any { it == 5 } && valueCounts[0] == 0
        if (isYahtzee && rollsLeft < 3) {
            // Verifica che la combo Yahtzee sia già stata usata
            val yahtzeeAlreadyUsed =
                playerStatuses[currentPlayer].usedCombos.containsKey("combo_5kind")

            if (yahtzeeAlreadyUsed) {
                // Assegna il bonus
                playerStatuses[currentPlayer].totalScore += 100
                Log.d("yahtzee bonus", "hai ottenuto il bonus")
            }
            playerStatuses[currentPlayer].YahtzeeBonusCount++
            hfx?.click(1f)
        }
    }




    fun savePlayerStatus(ps: PlayerStatus){
        viewModelScope.launch{
            val gameData = TableScore(
                date = (LocalDate.now().year.toString()+"-"+
                        LocalDate.now().monthValue.toString()+"-"+
                        LocalDate.now().dayOfMonth.toString()),
                aces = ps.upperSectionScores.get("Ones")?:0,
                twos = ps.upperSectionScores.get("Twos")?:0,
                threes = ps.upperSectionScores.get("Threes")?:0,
                fours = ps.upperSectionScores.get("Fours")?:0,
                fives = ps.upperSectionScores.get("Fives")?:0,
                sixes = ps.upperSectionScores.get("Sixes")?:0,
                bonusUpperSection = if (ps.bonusJustAwarded) 35 else 0,
                threeOfAKind = ps.usedCombos.get("combo_3kind")?:0,
                fourOfAKind = ps.usedCombos.get("combo_4kind")?:0,
                fullHouse = ps.usedCombos.get("combo_fullhouse")?:0,
                smallStraight = ps.usedCombos.get("combo_sstraight")?:0,
                largeStraight = ps.usedCombos.get("combo_lstraight")?:0,
                chance = ps.usedCombos.get("combo_chance")?:0,
                yahtzee = ps.usedCombos.get("combo_5kind")?:0,
                yahtzeeBonus = ps.YahtzeeBonusCount,
                finalScore = ps.totalScore
                //ª
            )

            dao?.storeTable(gameData)

        }

    }


    //prende una lista di dadi e restituisce i possibili punteggi per ciascuna combinazione
    fun calculatePossibleScores(dice: List<Int>): Map<String, Int> {
        val valueCounts = dice.groupingBy { it }.eachCount()  // Esempio: {6=3, 2=2}
        val sum = dice.sum()
        val yahtzee = 50
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
            return valueCounts.values.contains(3) && valueCounts.values.contains(2)
        }

        return mapOf(
            "combo_3kind" to if (hasNOfAKind(3)) sum else 0, //tris
            "combo_4kind" to if (hasNOfAKind(4)) sum else 0, //poker
            "combo_fullhouse" to if (isFullHouse()) 25 else 0, //full
            "combo_sstraight" to if (isSmallStraight()) 30 else 0, //sstraight
            "combo_lstraight" to if (isLargeStraight()) 40 else 0,//lstraight
            "combo_5kind" to if (hasNOfAKind(5)) yahtzee else 0,//yahtzee
            "combo_chance" to sum //chance
        )


    }


    //quando premi un punteggio, fissa il punteggio
    fun confirmScore(combo: String, score: Int) {

        if (!playerStatuses[currentPlayer].usedCombos.containsKey(combo) && hasRolled) {
            playerStatuses[currentPlayer].usedCombos[combo] =
                score //aggiunge la coppia combo-punteggio

            if (combo in playerStatuses[currentPlayer].upperSectionScores) {
                playerStatuses[currentPlayer].upperSectionScores[combo] = score
            }


            playerStatuses[currentPlayer].totalScore += score


            //pausa per mostrare il punteggio
            //fa partire un altro thread, che attende e poi fa partire la seconda parte dell'effetto
            CoroutineScope(Dispatchers.IO).launch {
                delay(1500)
                sfx?.bell()
                withContext(Dispatchers.Main) {
                    // reset e passa al prossimo turno
                    rollsLeft = 3
                    roundsPlayed++

                    Log.d("player increment", "$currentPlayer")
                    Log.d("player amount", "$playerAmount")
                    //blocco rotazione turni
                    if (currentPlayer < playerAmount && currentPlayer > 0) {
                        Log.d("player increment", "$currentPlayer")
                        currentPlayer++
                    } else currentPlayer = 1
                    //


                    Log.d("roundplay", "$roundsPlayed")
                    Log.d("rolls", "$rollsLeft")

                    checkAndApplyUpperSectionBonus()
                    //fine partita
                    if (roundsPlayed >= 13 * playerAmount) {
                        gameOver = true
                        //savePlayerStatus(playerStatuses[currentPlayer])
                        getWinner()
                        Log.d("roundplay", "sono nel gameover la partita è finita ?Bu) $gameOver")
                    }
                }
            }


        }

        dice = List(diceAmount) { 0 }
        hasRolled = false
    }


    fun getWinner(): Int {
        var winner = 1
        for (i in 2..playerAmount) {
            if (playerStatuses[i].totalScore > playerStatuses[winner].totalScore) {
                winner = i
            }
        }
        Log.d("winner", "$winner")
        return winner
    }




}
