package it.codesmell.yahtzee

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

//Lo stato di un singolo giocatore
class PlayerStatus {
    var playerNo by mutableStateOf(1) //numero giocatore
    var totalScore by mutableStateOf(0)
    var upperSectionScores = mutableStateMapOf<String, Int?>(
        "upper_aces" to null,
        "upper_twos" to null,
        "upper_threes" to null,
        "upper_fours" to null,
        "upper_fives" to null,
        "upper_sixes" to null,
    )


    var YahtzeeBonusCount by mutableStateOf(0) //quantità di yahtzee bonus (100p) ottenuti
    var usedCombos = mutableStateMapOf<String, Int>() // es: "Full house" -> 25

    var bonusJustAwarded by mutableStateOf(false)
    var upperSectionBonus by mutableStateOf(0)



}