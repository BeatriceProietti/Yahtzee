package it.codesmell.yahtzee.dao

import it.codesmell.yahtzee.entity.Match
import it.codesmell.yahtzee.entity.TableScore

// MatchEntity + List<TableScoreEntity> → Match (domain)

// TableScoreEntity → TableScore
fun TableScoreEntity.toDomain(): TableScore {
    return TableScore(
        aces, twos, threes, fours, fives, sixes, bonusUpperSection,
        threeOfAKind, fourOfAKind, fullHouse, smallStraight, largeStraight,
        chance, yahtzee, yahtzeeBonus, finalScore
    )
}

// TableScore → TableScoreEntity
fun TableScore.toEntity(matchId: Long, player: String): TableScoreEntity {
    return TableScoreEntity(
        matchOwnerId = matchId,
        playerName = player,

        aces = aces, twos = twos, threes = threes, fours = fours, fives = fives,
        sixes = sixes, bonusUpperSection = bonusUpperSection,
        threeOfAKind = threeOfAKind, fourOfAKind = fourOfAKind,
        fullHouse = fullHouse, smallStraight = smallStraight, largeStraight = largeStraight,
        chance = chance, yahtzee = yahtzee, yahtzeeBonus = yahtzeeBonus,
        finalScore = finalScore
    )
}

// Domain → Room
fun Match.toEntity(mode: GameMode): MatchEntity {
    return MatchEntity(
        mode = mode,
        timestamp = this.timestamp
    )
}

// MatchEntity + List<TableScoreEntity> → Match (domain)
fun matchEntityToDomain(match: MatchEntity, scores: List<TableScoreEntity>): Match {
    val result = Match(match.timestamp)
    for (score in scores) {
        result.changeScore(score.playerName, score.toDomain())
    }
    return result
}


