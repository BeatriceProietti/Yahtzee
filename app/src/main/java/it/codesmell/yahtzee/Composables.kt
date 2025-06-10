package it.codesmell.yahtzee


import android.app.Notification
import android.app.Notification.MessagingStyle.Message
import kotlin.math.roundToInt
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.codesmell.yahtzee.ui.theme.YahtzeeTheme
import kotlinx.serialization.descriptors.StructureKind
import android.content.res.Configuration
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberOverscrollEffect


//mettiamo qui i composable, per avere un po' di ordine e per averli standardizzati per tutte le schermate
//possiamo fare dei composable ad uso generico, si possono passare le funzioni come argomenti

class Composables {


    //quadrato di fine partita

    @Composable
    fun EndGameSquare(show: Boolean, onDismiss: () -> Unit) {
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp.dp
        val screenHeightDp = configuration.screenHeightDp.dp

        val boxWidth = screenWidthDp * 0.7f
        val boxHeight = screenHeightDp * 0.5f

        val offScreenTargetY = boxHeight.value + 32f
        val offsetY = remember { Animatable(offScreenTargetY) }

        LaunchedEffect(show) {
            val target = if (show) 0f else offScreenTargetY
            offsetY.animateTo(
                target,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .size(boxWidth, boxHeight)
                    .clip(RoundedCornerShape(14.dp))
                    .offset(y = offsetY.value.dp)
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Quadrato interno senza bordo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                            ){
                            Text("Partita terminata", color = Color.Black)
                            Text("Punteggio:", color = Color.Black)
                            Text("${gameLogic.totalScore}")
                        }
                    }

                    // Pulsante per chiudere
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text("Chiudi")
                    }
                }
            }
        }
    }





    //bottone che esegue una funzione senza argomenti
    @Composable           // vv Unit sarebbe void. funzione che prende nulla e restituisce nulla
    fun funButton (onClick : () -> Unit, text : String, depth : Long, modifier: Modifier = Modifier){
        Button(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        //evento pressione del tasto
                        val downEvent =
                            awaitPointerEvent(PointerEventPass.Main)
                        downEvent.changes.forEach {
                            if (it.pressed) {
                                hfx?.btnDown(depth)
                                onClick() //eseguo la funzione passata come argomento
                            }
                        }
                        //loop che aspetta che il tasto venga rilasciato
                        var allUp = false
                        while (!allUp) {
                            val event =
                                awaitPointerEvent(PointerEventPass.Main)
                            if (event.changes.all { it.pressed.not() }) {
                                allUp = true
                                event.changes.forEach {
                                    hfx?.click(0.5f)
                                }
                            }
                        }
                    }
                },
            onClick = {Log.d("palle","palle")},
            shape = ButtonDefaults.shape,
        ){
            Text(text)
        }
    }


    @Composable
    fun diceRow(dice: Array<Int>, gameLogic: GameLogic) { // ricevuti da game logic
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            for (i in 0 until dice.size) {
                animationSquare(
                    onClick = { gameLogic.selectDie(i) },
                    text = numToDie(dice[i]),
                    index = i,
                    isMoved = gameLogic.selectedDice[i] // ← qui!
                )
            }
        }
    }


    //da rifare con dadi fighi
    fun numToDie(num : Int) : String{
        var newString : String = num.toString()

        if(num == 1){newString = "⚀"}
        else if(num == 2){newString = "⚁"}
        else if(num == 3){newString = "⚂"}
        else if(num == 4){newString = "⚃"}
        else if(num == 5){newString = "⚄"}
        else if(num == 6){newString = "⚅"}
        else if(num == 0){newString = "□"}

        return newString
    }



    //--------------------------------- tabella dei punteggi del gioco


    @Composable
    fun CombosGridComposition2(
        cardWidth: Dp,
        cardHeight: Dp,
        dice: List<Int>,                 // dadi attuali passati dal ViewModel
        upperScores: Map<String, Int?>, // punteggi già confermati (null se non ancora scelti)
        onScoreConfirmed: (String, Int) -> Unit
    ) {
        val cols = 2
        val rows = 3
        val spacing = 4.dp
        val padding = 4.dp

        val labels = listOf("Ones", "Twos", "Threes", "Fours", "Fives", "Sixes")
        val diceEmojis = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

        BoxWithConstraints(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .padding(padding)
        ) {
            val usableHeight = maxHeight - spacing * (rows - 1)
            val tileHeight = usableHeight / rows

            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                userScrollEnabled = false
            ) {
                items(labels.size) { index ->
                    val label = labels[index]
                    val confirmedScore = upperScores[label]
                    // Calcolo punteggio attuale in base ai dadi correnti
                    val potentialScore = gameLogic.calculateUpperSectionScore(label, dice)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(tileHeight)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray)
                            .clickable(enabled = confirmedScore == null && dice.isNotEmpty()) {
                                onScoreConfirmed(label, potentialScore)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = diceEmojis.getOrNull(index) ?: "🎲",
                                fontSize = 30.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = confirmedScore?.toString() ?: "$label: $potentialScore",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }




//-----------

    @Composable
    fun ClickableText(text: String, category: ComboCategory, onClick: (ComboCategory) -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
                .clickable { onClick(category) },
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontSize = 14.sp)
        }
    }


//-----------


    @Composable
    fun CombosGridComposition(
        cardWidth: Dp,
        cardHeight: Dp,
        gameLogic: GameLogic,
        onScoreConfirmed: (String, Int) -> Unit
    ) {
        val cols = 2
        val rows = 7
        val spacing = 4.dp
        val padding = 4.dp

        val labels = listOf(
            "Three of a kind",
            "Four of a kind",
            "Full house",
            "Small straight",
            "Big straight",
            "Yahtzee!",
            "Chance"
        )

        // FILTRO SOLO I DADI NON SELEZIONATI
        val diceForScoring = gameLogic.dice.filterIndexed { index, _ -> !gameLogic.selectedDice[index] }

        // controllo che **tutti i dadi non selezionati siano effettivamente presenti** (almeno 1 dado)
        val shouldShowScores = diceForScoring.isNotEmpty() && diceForScoring.any { it != 0 }

        // Uso remember con chiavi di dipendenza per ricalcolare i punteggi
        val scores = remember(diceForScoring, gameLogic.usedCombos) {
            if (shouldShowScores) {
                gameLogic.calculatePossibleScores(diceForScoring)
            } else {
                emptyMap()
            }
        }

        BoxWithConstraints(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .padding(padding)
        ) {
            val usableHeight = maxHeight - spacing * (rows - 1)
            val tileHeight = usableHeight / rows

            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(spacing),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                userScrollEnabled = false
            ) {
                items(rows * cols) { index ->
                    val row = index / cols
                    val col = index % cols
                    val label = labels[row]
                    val isUsed = gameLogic.usedCombos.containsKey(label)
                    val score = if (isUsed) gameLogic.usedCombos[label] ?: 0 else scores[label] ?: 0

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(tileHeight)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray)
                            .clickable(enabled = col == 1 && !isUsed) {
                                onScoreConfirmed(label, score)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (col == 0) {
                            Text(label, color = Color.White, fontSize = 14.sp)
                        } else {
                            Text(
                                text = score.toString(),
                                color = when {
                                    isUsed -> Color.White
                                    else -> Color.LightGray
                                },
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    //---------------------------------

    @Composable
    fun animationSquare(
        onClick: () -> Unit,
        text: String,
        index: Int,
        isMoved: Boolean
    ) {
        val context = LocalContext.current

        val rotationZ by animateIntAsState( // animazione rotazione
            targetValue = if (isMoved) 180 else 0,
            animationSpec = tween(350),
            label = "rotation"
        )

        val offsetY = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(isMoved) {
            val targetY = if (isMoved) -130f else 0f
            offsetY.animateTo(
                targetY,
                animationSpec = spring(
                    dampingRatio = 0.4f,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .size(55.dp)
                .rotate(rotationZ.toFloat())
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val downEvent = awaitPointerEvent(PointerEventPass.Main)
                        downEvent.changes.forEach {
                            if (it.pressed) {
                                onClick()
                            }
                        }
                        var allUp = false
                        while (!allUp) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            if (event.changes.all { !it.pressed }) {
                                allUp = true
                                event.changes.forEach {
                                    hfx?.click(0.5f)
                                }
                            }
                        }
                    }
                }
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 50.sp
            )
        }
    }


//-------------------------------------


    @Composable
    fun swappingCards(
        scoreCard: ScoreCard,
        onComboClick: (ComboCategory) -> Unit
    ) {
        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        var isFirstOnTop by remember { mutableStateOf(true) }
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val cardWidth = if (isPortrait) screenWidth * 0.90f else screenWidth * 0.4f
        val cardHeight = if (isPortrait) screenHeight * 0.43f else screenHeight * 0.78f

        val firstOffset by animateDpAsState(
            targetValue = if (isFirstOnTop) 0.dp else 20.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ), label = "FirstCardOffset"
        )

        val secondOffset by animateDpAsState(
            targetValue = if (isFirstOnTop) 20.dp else 0.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ), label = "SecondCardOffset"
        )

        Column(
            modifier = Modifier
                .padding(32.dp)
                .height(cardHeight)
                .width(cardWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                // Second grid (numeri 1–6)
                Box(
                    modifier = Modifier
                        .offset(x = secondOffset, y = secondOffset)
                        .zIndex(if (isFirstOnTop) 0f else 1f)
                        .size(cardWidth, cardHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Green)
                ) {
                    CombosGridComposition2(
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        dice = gameLogic.dice,
                        upperScores = gameLogic.upperSectionScores,
                    ) { label, score ->
                        gameLogic.confirmScore(label, score)
                    }
                }

                // First grid (combo punteggiabile)
                Box(
                    modifier = Modifier
                        .offset(x = firstOffset, y = firstOffset)
                        .zIndex(if (isFirstOnTop) 1f else 0f)
                        .size(cardWidth, cardHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                ) {
                    CombosGridComposition(
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        gameLogic = gameLogic, // <--- qui
                        onScoreConfirmed = { combo, score ->
                            gameLogic.confirmScore(combo, score)
                        }
                    )

                }
            }

            Button(
                onClick = { isFirstOnTop = !isFirstOnTop },
                modifier = Modifier.padding(top = screenHeight * 0.05f)
            ) {
                Text("Scambia con rimbalzo")
            }
        }
    }

    @Composable
    fun GameCards(){



    }
}


