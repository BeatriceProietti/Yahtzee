package it.codesmell.yahtzee.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity //tabella nel DB
data class TableScore(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val date: String,

    //upper section
    val aces: Int,
    val twos: Int,
    val threes: Int,
    val fours: Int,
    val fives: Int,
    val sixes: Int,
    val bonusUpperSection: Int,

    //lower section
    val threeOfAKind: Int,
    val fourOfAKind: Int,
    val fullHouse: Int,
    val smallStraight: Int,
    val largeStraight: Int,
    val chance: Int,
    val yahtzee: Int,
    val yahtzeeBonus: Int,

    val finalScore: Int
)
