package it.codesmell.yahtzee

import it.codesmell.yahtzee.dao.TableScore

data class ScoreListState( // contains the screen state, such as displayed scores on the screen, scores sorting value
    val scores: List<TableScore> = emptyList(),
    val sortType: SortType = SortType.DATE //default scores order
)
