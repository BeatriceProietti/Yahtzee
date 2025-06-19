package it.codesmell.yahtzee

enum class SortType(val modeName: String) {
    SCORE("Score"),
    DATE("Date");

    override fun toString(): String {
        return modeName
    }
}