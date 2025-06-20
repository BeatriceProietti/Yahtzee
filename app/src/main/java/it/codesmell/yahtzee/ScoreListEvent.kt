package it.codesmell.yahtzee

import it.codesmell.yahtzee.dao.TableScore

sealed interface ScoreListEvent { // user actions on the screen
    data class sortScores(val sortType: SortType): ScoreListEvent
    data class deleteScore(val tableScore: TableScore): ScoreListEvent
}