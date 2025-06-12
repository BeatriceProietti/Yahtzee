package it.codesmell.yahtzee.entity

class Match {

    private val scores: MutableMap<String, TableScore> = mutableMapOf()

    fun changeScore(playerName: String, tableScore: TableScore) {
        scores[playerName] = tableScore
    }

    fun getScore(playerName: String): TableScore? {
        return scores[playerName]
    }

    fun getAllScores(): Map<String, TableScore> {
        return scores.toMap()
    }
}
