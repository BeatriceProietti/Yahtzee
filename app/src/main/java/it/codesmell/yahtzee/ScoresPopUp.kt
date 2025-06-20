package it.codesmell.yahtzee

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.codesmell.yahtzee.dao.TableScore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoresPopUp(
    onEvent: (ScoreListEvent) -> Unit,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    tableScore: TableScore
) {
    if (showDialog) {
        val popUpWidth = screenWidth * 0.9f
        val popUpHeight = screenHeight * 0.7f
        BasicAlertDialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(5.dp).size(popUpWidth, popUpHeight),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    Modifier.size(popUpWidth, popUpHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // pop up header
                    Row(
                        Modifier.width(popUpWidth),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start) {
                        Image(
                            painter = painterResource(id = R.drawable.dice_icon),
                            contentDescription = "",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Fit,
                        )

                        Text(text = "Dettagli punteggio:", fontSize = 20.sp)
                    }

                    //table content
                    Text(text = "${tableScore.date} ${tableScore.finalScore}", fontSize = 20.sp)

                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item { Text("Upper", fontSize = 18.sp) }
                        item { Text("Aces: ${tableScore.aces}") }
                        item { Text("Twos: ${tableScore.twos}") }
                        item { Text("Threes: ${tableScore.threes}") }
                        item { Text("Fours: ${tableScore.fours}") }
                        item { Text("Fives: ${tableScore.fives}") }
                        item { Text("Sixes: ${tableScore.sixes}") }
                        item { Text("Bonus: ${tableScore.bonusUpperSection}") }

                        item { Text("Lower:", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp)) }
                        item { Text("Three of a Kind: ${tableScore.threeOfAKind}") }
                        item { Text("Four of a Kind: ${tableScore.fourOfAKind}") }
                        item { Text("Full House: ${tableScore.fullHouse}") }
                        item { Text("Small Straight: ${tableScore.smallStraight}") }
                        item { Text("Large Straight: ${tableScore.largeStraight}") }
                        item { Text("Chance: ${tableScore.chance}") }
                        item { Text("Yahtzee: ${tableScore.yahtzee}") }
                        item { Text("Yahtzee Bonus: ${tableScore.yahtzeeBonus}") }
                        item { Text("Final Score: ${tableScore.finalScore}", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp)) }
                    }


                    // delete button
                    composables?.funButton3D(
                        onClick = { onEvent(ScoreListEvent.deleteScore(tableScore)) }, //send action to viewModel
                        text = "Cancella",
                        color = Color.Blue,
                        depth = 15,
                        screenWidth * 0.50f, 50.dp
                    )
                }

            }
        }
    }
}

