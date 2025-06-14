package it.codesmell.yahtzee

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.MotionScene
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// qua mettiamo tutte le schermate dell'app

val configuration : ProvidableCompositionLocal<Configuration>? = null
var isPortrait : Boolean = false
var isLandscape : Boolean = false

var screenHeight : Dp = 0.dp
var screenWidth : Dp = 0.dp

    @Composable
    fun MainScreen(navController: NavController) {

        val configuration = LocalConfiguration.current
        isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        screenHeight = configuration.screenHeightDp.dp
        screenWidth = configuration.screenWidthDp.dp



        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = screenWidth*0.05f, end = screenWidth*0.05f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                composables?.funButton3D(
                    onClick = { navController.navigate("GameScreen") },
                    text = stringResource(R.string.mode_singleplayer),
                    color = Color.Red,
                    depth = 10,
                    screenWidth*0.6f,50.dp
                )
                composables?.funButton3D(
                    onClick = { provas() },
                    text = stringResource(R.string.highscores),
                    color = Color(0xFFFF772E),
                    depth = 10,
                    screenWidth*0.3f,50.dp
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                composables?.funButton3D(
                    onClick = { navController.navigate("GameScreen"); gameLogic.multiPlayer=true}, // col punto e virgola posso fargli fare più cose
                    text = stringResource(R.string.mode_multiplayer),
                    color = Color.Red,
                    depth = 10,
                    screenWidth*0.9f,50.dp
                )
                composables?.funButton3D(
                    onClick = { navController.navigate("Screen2") },
                    text = stringResource(R.string.mode_testscreen),
                    color = Color.Blue,
                    depth = 10,
                    screenWidth*0.9f,50.dp
                )

                composables?.funButton3D(
                    onClick = { navController.navigate("OptionScreen") },
                    text = stringResource(R.string.settings),
                    color = Color.Blue,
                    depth = 10,
                    screenWidth*0.9f,50.dp
                )
            }
        }
    }

    @Composable
    fun OptionScreen(navController: NavController){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            composables?.funButton3D(::switchVibMode, stringResource(R.string.settings_hapticsMode), color = if(hfx?.hasRichHaptics == true) Color.Blue else Color.Red, depth = 10,150.dp,50.dp)
            composables?.funButton3D(::switchTheme, if (darkTheme) "☾" else "☼", color = Color.Blue, depth = 10,150.dp,50.dp)
        }


    }


@Composable
    fun Screen2(navController: NavController) {
        var isOn by remember { mutableStateOf(true) }
        var isMoved = false
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            composables?.funButton3D(onClick = {}, text = "palle Lunghe", color = Color.Red, depth = 50,30.dp,150.dp)
            Row(){
                composables?.funButton3D(onClick = {}, text = "palle", color = Color.Blue, depth = 13,30.dp,150.dp)
                composables?.funButton3D(onClick = {}, text = "pallepalle", color = Color.Black, depth = 10,30.dp,150.dp)
            }

        }

    }


    @Composable
    fun GameScreen(gameLogic: GameLogic) {
        var showOverlay by remember { mutableStateOf(false) }
        val context = LocalContext.current


        var hasReset by rememberSaveable { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (!hasReset) {
                gameLogic.resetGame()
                hasReset = true
            }
        }


        LaunchedEffect(gameLogic.gameOver) {
            if (gameLogic.gameOver) {
                showOverlay = true
            }
        }
        LaunchedEffect(gameLogic.bonusJustAwarded) {
            if (gameLogic.bonusJustAwarded) {
                Log.d("bonus", "🎉 Bonus attivato!")

                Toast.makeText(
                    context,
                    "Hai ottenuto il bonus di +35 punti!",
                    Toast.LENGTH_SHORT
                ).show()

                // Ritarda il reset giusto per evitare race condition visiva (facoltativo)
                delay(300)

                gameLogic.bonusJustAwarded = false // Reset flag
                Log.d("bonus", "✅ bonusJustAwarded reset a ${gameLogic.bonusJustAwarded}")
            }
        }



        //var totalScore by remember { mutableStateOf(0) }


        var dr = IntArray(diceAmount)
        for (i in 0..diceAmount - 1) {
            dr[i] = gameLogic.dice[i] //i dadi presi da gameLogic, da mandare all'interfaccia
        }
        val configuration = LocalConfiguration.current

        //Portrait ------------------------------------------------------------------------------------------------------------
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(modifier = Modifier //punteggio
                    .padding(top = screenHeight*0.05f, bottom = screenHeight*0.05f)
                ){
                    Row(){
                        val scoreToShow =
                        if (gameLogic.multiPlayer) gameLogic.currentTotalScore else gameLogic.totalScore
                        Text(
                            text = "Punteggio Totale: $scoreToShow",
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                        if (gameLogic.multiPlayer) {
                            Text(
                                text = if (gameLogic.isPlayerOneTurn) "Turno: Giocatore 1" else "Turno: Giocatore 2",
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                Box(){
                    composables?.swappingCards()
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

                    composables?.funButton3D(
                        onClick = { if (canRoll) gameLogic.rollSelectedDice() },
                        text = if (canRoll) "Tira Dadi" else "Scegli una combo",
                        color = MaterialTheme.colorScheme.primary,
                        depth = 10,
                        150.dp,50.dp
                    )
                }
            }
        }
        //Landscape ------------------------------------------------------------------------------------------------------------
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
                    composables?.swappingCards()
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

                        composables?.funButton3D(
                            onClick = { if (canRoll) gameLogic.rollSelectedDice() },
                            text = if (canRoll) "Tira Dadi" else "Scegli una combo",
                            color = MaterialTheme.colorScheme.primary,
                            depth = 10,
                            150.dp,50.dp
                        )

                    }
                }

            }

        }
        composables?.EndGameSquare(
            show = showOverlay,
            onDismiss = { showOverlay = false },
            p1Score = p1TotalScore,
            p2Score = p2TotalScore,
            isMultiplayer = gameLogic.multiPlayer
        )

    }

// -----

