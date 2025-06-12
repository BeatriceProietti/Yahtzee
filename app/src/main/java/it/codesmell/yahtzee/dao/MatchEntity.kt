package it.codesmell.yahtzee.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_table")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val matchId: Long = 0,
    val mode: GameMode,
    val timestamp: Long // <-- nuovo campo
)