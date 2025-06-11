package it.codesmell.yahtzee
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class GamesData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val totalScore: Int,
    val date: Long // timestamp partita
)
