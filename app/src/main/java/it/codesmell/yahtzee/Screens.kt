package it.codesmell.yahtzee

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.hardware.SensorEvent
import android.print.PrintAttributes.Margins
import android.util.Size
import android.widget.Button
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import it.codesmell.yahtzee.gameLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import androidx.compose.material3.Button // O androidx.compose.material.Button


// qua mettiamo tutte le schermate dell'app

    @Composable
    fun MainScreen(navController: NavController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Benvenuto allo Yahtzeello")
            composables?.funButton(::provas, "Sbrisculo Aptico", 50)
            composables?.funButton(::switchVibMode, "Cambia mod. vibrazione", 0)
            composables?.funButton({ navController.navigate("GameScreen") }, "Gioca", 0)
            composables?.funButton(::switchTheme, "cambia il tema", 0)
            composables?.funButton({ navController.navigate("Screen2") }, "schermata di prova delle applicazioni", 0)
        }
    }


    @Composable
    fun Screen2(navController: NavController) {
        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val screenHeight = LocalWindowInfo.current.containerSize.height.dp
        val screenWidth = LocalWindowInfo.current.containerSize.width.dp
        var isOn by remember { mutableStateOf(true) }
        var isMoved = false
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {



        }

    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun gameScreenContent(navController : NavController) {

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        var dr = IntArray(diceAmount)
        for (i in 0..diceAmount - 1) {
            dr[i] = gameLogic.dice[i] //i dadi presi da gameLogic, da mandare all'interfaccia
        }





        //---------------------------------------------------------


    }

    @Composable
    fun GameScreen(navController: NavController, gameLogic: GameLogic) {

        var showOverlay by remember { mutableStateOf(false) }
        val context = LocalContext.current

        LaunchedEffect(gameLogic.bonusJustAwarded) {
            if (gameLogic.bonusJustAwarded) {
                Toast.makeText(context, "Hai ottenuto il bonus di +35 punti!", Toast.LENGTH_SHORT).show()
                gameLogic.bonusJustAwarded = false // resetta il flag
            }
        }
        var gameOverShown by remember { mutableStateOf(false) }
        var bonusShown by remember { mutableStateOf(false) }

        LaunchedEffect(gameLogic.roundsPlayed) {
            if (gameLogic.gameOver && !gameOverShown) {
                Toast.makeText(context, "Partita finita! Punteggio: ${gameLogic.totalScore}", Toast.LENGTH_LONG).show()
                gameOverShown = true
                showOverlay = true
            }
        }




        val scoreCard = remember { ScoreCard() }
        var totalScore by remember { mutableStateOf(0) }

        var dr = IntArray(diceAmount)
        for (i in 0..diceAmount - 1) {
            dr[i] = gameLogic.dice[i] //i dadi presi da gameLogic, da mandare all'interfaccia
        }
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(modifier = Modifier //punteggio
                    .padding(top = screenHeight*0.05f, bottom = screenHeight*0.05f)
                ){

                    Text(
                        text = "Punteggio Totale: ${gameLogic.totalScore}",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )

                }
                Box(){
                    composables?.swappingCards(
                        scoreCard = scoreCard,
                        onComboClick = { category ->
                            scoreCard.setScore(category, dr.toList())
                            totalScore = scoreCard.totalScore()
                            gameLogic.rollsLeft--
                        }
                    )

                }
                Box(modifier = Modifier // riga di dadi di merda
                    .padding(bottom = screenHeight*0.05f, top = screenHeight*0.05f)
                ) {
                    composables?.diceRow(dice = dr.toTypedArray(), gameLogic = gameLogic)

                }
                Box(modifier = Modifier // bottone DUCE di merda
                    .padding(bottom = screenHeight*0.05f)
                ) {

                    val canRoll = gameLogic.rollsLeft > 0 && !gameLogic.gameOver

                    composables?.funButton(
                        onClick = { if (canRoll) gameLogic.rollSelectedDice() },
                        text = if (canRoll) "Tira Dadi" else "Scegli una combo",
                        depth = 0
                    )                }

            }
        }
        else{

            Row(modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
                ) {

                Box(modifier = Modifier //punteggio
                    .padding(start = screenHeight*0.05f, end = screenHeight*0.05f)
                ){

                    Text(
                        text = "${gameLogic.totalScore}",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Box(){
                    composables?.swappingCards(
                        scoreCard = scoreCard,
                        onComboClick = { category ->
                            scoreCard.setScore(category, dr.toList())
                            totalScore = scoreCard.totalScore()
                            gameLogic.rollsLeft--
                        }
                    )

                }
                Column(modifier = Modifier
                    .rotate(-90f)
                    .offset(y = 50.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

                ){//colonnadiceRow
                    Box(
                        modifier = Modifier // riga di dadi di merda
                            .padding()
                    ) {
                        composables?.diceRow(dice = dr.toTypedArray(), gameLogic = gameLogic)
                    }
                    Box(
                        modifier = Modifier // bottone DUCE di merda
                            .padding(50.dp)
                            .rotate(90f)
                    ) {
                        val canRoll = gameLogic.rollsLeft > 0 && !gameLogic.gameOver

                        composables?.funButton(
                            onClick = { if (canRoll) gameLogic.rollSelectedDice() },
                            text = if (canRoll) "Tira Dadi" else "Scegli una combo",
                            depth = 0
                        )


                    }
                }

            }

        }
        composables?.EndGameSquare(show = showOverlay, onDismiss = { showOverlay = false })
    }



// -----

