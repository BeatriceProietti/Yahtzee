package it.codesmell.yahtzee.controller

import AppDatabase
import it.codesmell.yahtzee.dao.matchEntityToDomain
import it.codesmell.yahtzee.entity.Match

class MatchController(private val db: AppDatabase) {

    private suspend fun loadAllMatches(): List<Match> {
        return db.matchDao().getAllMatches().map { matchWithScores ->
            matchEntityToDomain(matchWithScores.match, matchWithScores.scores)
        }
    }

    suspend fun getScoresByDate(): List<Match> {
        return loadAllMatches().sortedBy { it.timestamp }
    }

    suspend fun getScoresByHigherScores(): List<Match> {
        return loadAllMatches().sortedByDescending { it.getHighestScore() }
    }
}

    /*
    Esempio di uso
    val controller = MatchController(AppDatabase.getInstance(context))

    val byDate = MatchController.getScoresByDate()
    val byScore = MatchController.getScoresByHigherScores()
     */