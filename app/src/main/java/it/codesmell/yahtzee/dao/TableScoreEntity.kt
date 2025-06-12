package it.codesmell.yahtzee.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_score")
data class TableScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchOwnerId: Long, // FK manuale verso MatchEntity.matchId
    val playerName: String,

    val aces: Int,
    val twos: Int,
    val threes: Int,
    val fours: Int,
    val fives: Int,
    val sixes: Int,
    val bonusUpperSection: Int,

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
