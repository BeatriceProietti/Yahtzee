package it.codesmell.yahtzee.entity

class Match(val timestamp: Long = System.currentTimeMillis()) {

    private val scores: MutableMap<String, TableScore> = mutableMapOf()

    fun changeScore(playerName: String, tableScore: TableScore) {
        scores[playerName] = tableScore
    }

    fun getScore(playerName: String): TableScore? = scores[playerName]

    fun getAllScores(): Map<String, TableScore> = scores.toMap()

    fun getHighestScore(): Int = scores.values.maxOfOrNull { it.finalScore } ?: 0
}