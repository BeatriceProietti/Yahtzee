package it.codesmell.yahtzee

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.MotionScene
import androidx.navigation.NavController

// qua mettiamo tutte le schermate dell'app

    @Composable
    fun MainScreen(navController: NavController) {

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val screenHeight = LocalWindowInfo.current.containerSize.height.dp
        val screenWidth = LocalWindowInfo.current.containerSize.width.dp



        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = screenWidth*0.05f, end = screenWidth*0.05f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box() {
                    composables?.funButton3D(
                        onClick = { navController.navigate("GameScreen") },
                        text = "Single",
                        color = Color.Red,
                        depth = 10,
                        paddingVal = 15.dp
                    )
                }
                Box() {
                    composables?.funButton3D(
                        onClick = { navController.navigate("GameScreen") },
                        text = "Multi",
                        color = Color.Red,
                        depth = 10,
                        paddingVal = 15.dp
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                composables?.funButton3D(
                    onClick = { navController.navigate("Screen2") },
                    text = "schermata di prova delle applicazioni",
                    color = Color.Blue,
                    depth = 10,
                    15.dp
                )
                composables?.funButton3D(
                    onClick = { provas() },
                    text = "partite precedenti",
                    color = Color.Blue,
                    depth = 10,
                    15.dp
                )
                composables?.funButton3D(
                    onClick = { navController.navigate("OptionScreen") },
                    text = "opzioni",
                    color = Color.Blue,
                    depth = 10,
                    15.dp
                )
            }
        }
    }

    @Composable
    fun OptionScreen(navController: NavController){

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val screenHeight = LocalWindowInfo.current.containerSize.height.dp
        val screenWidth = LocalWindowInfo.current.containerSize.width.dp


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = screenHeight * 0.02f, end = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            composables?.funButton3D(::switchVibMode, "\uD83D\uDCF3", color = Color.Blue, depth = 10,50.dp)
            composables?.funButton3D(::switchTheme, if (darkTheme) "☾" else "☼", color = Color.Blue, depth = 10,50.dp)
        }

        Text("Benvenuto allo Yahtzeello")
        composables?.funButton3D(
            onClick = { provas() },
            text = "sbrisculo aptico",
            color = Color.Blue,
            depth = 10,
            50.dp
        )


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
            verticalArrangement = Arrangement.Center
        ) {

            composables?.funButton3D(onClick = {}, text = "palle Lunghe", color = Color.Red, depth = 50,50.dp)
            Row(){
                composables?.funButton3D(onClick = {}, text = "palle", color = Color.Blue, depth = 13,50.dp)
                composables?.funButton3D(onClick = {}, text = "pallepalle", color = Color.Black, depth = 10,50.dp)
            }

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
    fun GameScreen(gameLogic: GameLogic) {

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

        var totalScore by remember { mutableStateOf(0) }



        var dr = IntArray(diceAmount)
        for (i in 0..diceAmount - 1) {
            dr[i] = gameLogic.dice[i] //i dadi presi da gameLogic, da mandare all'interfaccia
        }
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        //Portrait ------------------------------------------------------------------------------------------------------------
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
                        50.dp
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
                            50.dp

                        )

                    }
                }

            }

        }
        composables?.EndGameSquare(show = showOverlay, onDismiss = { showOverlay = false })
    }

// -----

