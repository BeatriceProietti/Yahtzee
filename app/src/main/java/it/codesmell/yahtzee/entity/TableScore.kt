package it.codesmell.yahtzee.entity

data class TableScore(
    //upper section
    var aces: Int = 0,
    var twos: Int = 0,
    var threes: Int = 0,
    var fours: Int = 0,
    var fives: Int = 0,
    var sixes: Int = 0,
    var bonusUpperSection: Int = 0,
    //lower section
    var threeOfAKind: Int = 0,
    var fourOfAKind: Int = 0,
    var fullHouse: Int = 0,
    var smallStraight: Int = 0,
    var largeStraight: Int = 0,
    var chance: Int = 0,
    var yahtzee: Int = 0,
    var yahtzeeBonus: Int = 0,
    //final score
    var finalScore: Int = 0
) {

    fun computeScore(): Int {
        val upper = aces + twos + threes + fours + fives + sixes
        val lower = threeOfAKind + fourOfAKind + fullHouse + smallStraight +
                largeStraight + chance + yahtzee + yahtzeeBonus

        bonusUpperSection = if (upper >= 63) 35 else 0
        finalScore = upper + bonusUpperSection + lower
        return finalScore
    }

    fun updateTable(tableScore: TableScore) {
        this.aces = tableScore.aces
        this.twos = tableScore.twos
        this.threes = tableScore.threes
        this.fours = tableScore.fours
        this.fives = tableScore.fives
        this.sixes = tableScore.sixes
        this.bonusUpperSection = tableScore.bonusUpperSection
        this.threeOfAKind = tableScore.threeOfAKind
        this.fourOfAKind = tableScore.fourOfAKind
        this.fullHouse = tableScore.fullHouse
        this.smallStraight = tableScore.smallStraight
        this.largeStraight = tableScore.largeStraight
        this.chance = tableScore.chance
        this.yahtzee = tableScore.yahtzee
        this.yahtzeeBonus = tableScore.yahtzeeBonus
        this.finalScore = tableScore.finalScore
    }
}