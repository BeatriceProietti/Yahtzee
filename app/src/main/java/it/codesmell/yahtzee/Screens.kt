package it.codesmell.yahtzee

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
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
import java.time.LocalDate
import java.util.Calendar

// qua mettiamo tutte le schermate dell'app

val configuration: ProvidableCompositionLocal<Configuration>? = null
var isPortrait: Boolean = false
var isLandscape: Boolean = false

var screenHeight: Dp = 0.dp
var screenWidth: Dp = 0.dp

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
        horizontalAlignment = CenterHorizontally
    ) {
        //Titolo
        composables?.title()

        Spacer(modifier = Modifier.height(screenHeight * 0.075f))

        //Riga un giocatore - Classifica
        composables?.titleLabel(stringResource(R.string.one_player))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = screenWidth * 0.05f, end = screenWidth * 0.05f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = CenterVertically,
        ) {
            composables?.funButton3D(
                onClick = { navController.navigate("GameScreen");gameLogic.initGame(1) },
                text = stringResource(R.string.mode_singleplayer),
                color = Color.Red,
                depth = 10,
                sizeX = screenWidth * 0.55f,
                sizeY = 70.dp
            )
            composables?.funButton3D(
                onClick = { provas() },
                text = stringResource(R.string.highscores),
                color = Color(0xFFFF772E),
                depth = 10,
                sizeX = screenWidth * 0.35f,
                sizeY = 70.dp
            )
        }


        //Riga più giocatori
        Spacer(modifier = Modifier.height(15.dp))
        composables?.titleLabel(stringResource(R.string.multi_player))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            composables?.funButton3D(
                onClick = {
                    navController.navigate("GameScreen");gameLogic.initGame(2)
                }, // col punto e virgola posso fargli fare più cose
                text = stringResource(R.string.mode_multiplayer),
                color = Color.Red,
                depth = 10,
                screenWidth * 0.9f, 50.dp
            )
            composables?.funButton3D(
                onClick = { navController.navigate("Screen2") },
                text = stringResource(R.string.mode_testscreen),
                color = Color.Blue,
                depth = 10,
                screenWidth * 0.9f, 50.dp
            )

            composables?.funButton3D(
                onClick = { navController.navigate("OptionScreen") },
                text = stringResource(R.string.settings),
                color = Color.Blue,
                depth = 10,
                screenWidth * 0.9f, 50.dp
            )
        }
    }
}

@Composable
fun OptionScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        composables?.funButton3D(
            ::switchVibMode,
            stringResource(R.string.settings_hapticsMode),
            color = if (hfx?.hasRichHaptics == true) Color.Blue else Color.Red,
            depth = 10,
            150.dp,
            50.dp
        )
        composables?.funButton3D(
            ::switchTheme,
            if (darkTheme) "☾" else "☼",
            color = Color.Blue,
            depth = 10,
            150.dp,
            50.dp
        )
    }
}


@Composable
fun Screen2(
    navController: NavController,
    viewModel: ScoreListViewModel,
    onEvent: (ScoreListEvent) -> Unit
) {
    val state by viewModel.state.collectAsState()

    var selectedScoreId by rememberSaveable { mutableStateOf<Int?>(null) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp, 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        //verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SortType.values().forEach { sortType ->
                Row(
                    modifier = Modifier
                        .clickable {
                            onEvent(ScoreListEvent.sortScores(sortType))
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = CenterVertically,
                ) {
                    RadioButton(
                        selected = state.sortType == sortType,
                        onClick = { onEvent(ScoreListEvent.sortScores(sortType)) }
                    )
                    Text(text = sortType.toString())
                }
            }
        }

        LazyColumn() {
            items(state.scores) { score ->
                composables?.funButton3D(
                    onClick = { selectedScoreId = score.id },
                    text = "${score.finalScore} ${score.date}", //valori delo score
                    color = Color.Blue,
                    depth = 15,
                    screenWidth * 0.87f, 50.dp
                )

            }
        }

        // find the TableScore that has been selected
        val selectedScore = state.scores.find { it.id == selectedScoreId }

        // show corresponding popup
        if (selectedScore != null) {
            ScoresPopUp(
                showDialog = true,
                onDismiss = { selectedScoreId = null },
                onEvent = onEvent,
                tableScore = selectedScore
            )
        }
    }

}


@Composable
fun GameScreen(gameLogic: GameLogic, navController: NavController) {
    var showOverlay by remember { mutableStateOf(false) }
    val context = LocalContext.current
    BackHandler { // mi serve sennò non mi setta le variabili del multiplayer a falso e il gioco parte sempre in multi
        navController.popBackStack()
        gameLogic.multiPlayer = false
    }


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
    var showToastAni: Boolean
    showToastAni =
        gameLogic.bonusJustAwarded // mi creo una variabile che si prende lo stato del bonus jusr awarded e poi lo usa per mostrare il toast
    LaunchedEffect(showToastAni) {
        if (showToastAni) {
            Log.d("bonus", "🎉 Bonus attivato!")

            Toast.makeText(
                context,
                "Hai ottenuto il bonus di +35 punti!",
                Toast.LENGTH_SHORT
            ).show()

            // Ritarda il reset giusto per evitare race condition visiva (facoltativo)
            delay(300)

            showToastAni = false // Reset flag
            Log.d("bonus", "✅ bonusJustAwarded reset a ${gameLogic.bonusJustAwarded}")
        }
    }


    var dr = IntArray(gameLogic.diceAmount)
    for (i in 0..gameLogic.diceAmount - 1) {
        dr[i] = gameLogic.dice[i] //i dadi presi da gameLogic, da mandare all'interfaccia
    }
    val configuration = LocalConfiguration.current

    //Portrait ------------------------------------------------------------------------------------------------------------
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier //punteggio
                    .padding(top = screenHeight * 0.05f, bottom = screenHeight * 0.05f)
            ) {
                composables?.scoreDisplay()
            }

            Box() {
                composables?.swappingCards()
            }
            Box(
                modifier = Modifier // riga di dadi di merda
                    .padding(bottom = screenHeight * 0.05f, top = screenHeight * 0.05f)
            ) {
                composables?.diceRow(dice = dr.toTypedArray(), gameLogic = gameLogic)

            }
            Box(
                modifier = Modifier // bottone DUCE di merda
                    .padding(bottom = screenHeight * 0.05f)
            ) {

                val canRoll = gameLogic.rollsLeft > 0 && !gameLogic.gameOver

                Column() {
                    composables?.funButton3D(
                        onClick = { gameLogic.savePlayerStatus(gameLogic.playerStatuses[gameLogic.currentPlayer], ) },
                        text = "Ottieni Palle",
                        color = Color.Red,
                        depth = 10,
                        150.dp, 50.dp
                    )
                    composables?.funButton3D(
                        onClick = { if (canRoll) gameLogic.rollSelectedDice() },
                        text = if (canRoll) "Tira Dadi" else "Scegli una combo",
                        color = MaterialTheme.colorScheme.primary,
                        depth = 10,
                        150.dp, 50.dp
                    )
                }

            }
        }
    }
    //Landscape ------------------------------------------------------------------------------------------------------------
    else {

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier //punteggio
                    .padding(top = screenHeight * 0.05f, bottom = screenHeight * 0.05f)
            ) {
                composables?.scoreDisplayLandscape()
            }

            Box() {
                composables?.swappingCards()
            }
            Column(
                modifier = Modifier
                    .rotate(-90f)
                    .offset(y = 50.dp),

                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {//colonnadiceRow
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
                        150.dp, 50.dp
                    )

                }
            }

        }

    }
    composables?.EndGameSquare(
        show = showOverlay,
        onDismiss = { showOverlay = false; gameLogic.resetGame() },
        p1Score = gameLogic.p1TotalScore,
        p2Score = gameLogic.p2TotalScore,
        isMultiplayer = gameLogic.multiPlayer
    )

}

// -----

