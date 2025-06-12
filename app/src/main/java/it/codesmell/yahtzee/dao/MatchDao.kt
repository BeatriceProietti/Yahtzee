package it.codesmell.yahtzee.dao

import androidx.room.*

data class MatchWithScores(
    @Embedded val match: MatchEntity,
    @Relation(
        parentColumn = "matchId",
        entityColumn = "matchOwnerId"
    )
    val scores: List<TableScoreEntity>
)

@Dao
interface MatchDao {

    @Transaction
    @Query("SELECT * FROM match_table")
    suspend fun getAllMatches(): List<MatchWithScores>

    @Insert
    suspend fun insertMatch(match: MatchEntity): Long

    @Insert
    suspend fun insertScores(scores: List<TableScoreEntity>)
}
