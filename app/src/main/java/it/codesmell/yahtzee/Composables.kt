package it.codesmell.yahtzee


import android.annotation.SuppressLint
import kotlin.math.roundToInt
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import it.codesmell.yahtzee.dao.TableScore
import it.codesmell.yahtzee.font_handwriting


//mettiamo qui i composable, per avere un po' di ordine e per averli standardizzati per tutte le schermate
//possiamo fare dei composable ad uso generico, si possono passare le funzioni come argomenti

class Composables {




    //quadrato di fine partita

    @Composable
    fun WinnerPopup(showPopup: Boolean, onDismiss: () -> Unit) {
        if (showPopup) {
            val message = if (gameLogic.playerAmount > 1) {
                val winner = gameLogic.getWinner()
                "${stringResource(R.string.winner)}: ${stringResource(R.string.game_player)} $winner\n${stringResource(R.string.game_score)}: ${gameLogic.playerStatuses[winner].totalScore}"
            } else {
                "${stringResource(R.string.game_score)}: ${gameLogic.playerStatuses[gameLogic.currentPlayer].totalScore}"
            }

            AlertDialog(
                //modifier = Modifier.clip(shape = RoundedCornerShape(0.dp)), //non funziona?
                onDismissRequest = onDismiss,
                title = { Text(stringResource(R.string.gameover)) },
                text = { Text(message) },
                confirmButton = {
                    composables?.funButton3D(
                        onClick = onDismiss,
                        text = stringResource(R.string.close),
                        color = Color(0xFFEE3F62),
                        depth = 8,
                        120.dp, 50.dp
                    )
                },

            )
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
                                sfx?.btnDown()
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
                                    sfx?.btnUp()
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
            Modifier.padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
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


    @SuppressLint("UnusedBoxWithConstraintsScope")
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

        val labels = listOf("Aces", "Twos", "Threes", "Fours", "Fives", "Sixes")
        val diceEmojis = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")



        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(R.string.uppersection),
                fontSize = 18.sp,
                fontFamily = font_Squarewise,
                color = Color(0xFF414B3A),
            )
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
                        val isUsed = gameLogic.playerStatuses[gameLogic.currentPlayer].usedCombos.containsKey(label)
                        val confirmedScore = upperScores[label]
                        // Calcolo punteggio attuale in base ai dadi correnti
                        val potentialScore = gameLogic.calculateUpperSectionScore(label, dice)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(tileHeight)
                                //.clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF475447))
                                .clickable(enabled = confirmedScore == null && dice.isNotEmpty()) {
                                    onScoreConfirmed(label, potentialScore)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = diceEmojis.getOrNull(index) ?: "🎲",
                                    fontSize = 30.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = confirmedScore?.toString() ?: "$potentialScore",
                                    color = when {
                                        isUsed -> Color.White
                                        else -> Color(0xFF62775B)
                                    },
                                        fontSize = 30.sp,
                                    fontFamily = font_handwriting,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }




//-----------

    @Composable
    fun ClickableText(text: String) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
                .clickable {},
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontSize = 14.sp)
        }
    }

//-----------


    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable
    fun CombosGridComposition(
        cardWidth: Dp,
        cardHeight: Dp,
        gameLogic: GameLogic,
        confirmedScores: (String, Int) -> Unit
    ) {
        val cols = 2
        val rows = 7
        val spacing = 4.dp
        val padding = 4.dp

        val labels = listOf(// Label delle combinazioni adattate alla localizzazione
            "combo_3kind" to R.string.combo_3kind,
            "combo_4kind" to R.string.combo_4kind,
            "combo_fullhouse" to R.string.combo_fullhouse,
            "combo_sstraight" to R.string.combo_sstraight,
            "combo_lstraight" to R.string.combo_lstraight,
            "combo_5kind" to R.string.combo_5kind,
            "combo_chance" to R.string.combo_chance
        )

        // FILTRO SOLO I DADI NON SELEZIONATI
        val diceForScoring = gameLogic.dice.filterIndexed { index, _ -> !gameLogic.selectedDice[index] }

        // controllo che **tutti i dadi non selezionati siano effettivamente presenti** (almeno 1 dado)
        val shouldShowScores = diceForScoring.isNotEmpty() && diceForScoring.any { it != 0 }

        // Uso remember con chiavi di dipendenza per ricalcolare i punteggi
        val scores = remember(diceForScoring, gameLogic.playerStatuses[gameLogic.currentPlayer].usedCombos) {
            if (shouldShowScores) {
                gameLogic.calculatePossibleScores(diceForScoring)
            } else {
                emptyMap()
            }
        }
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(R.string.lowersection),
                fontSize = 18.sp,
                fontFamily = font_Squarewise,
                color = Color.DarkGray,
            )
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
                        val (comboKey, resId) = labels[row] // associa la key alla stringa localizzata
                        val label = stringResource(resId)
                        val isUsed = gameLogic.playerStatuses[gameLogic.currentPlayer].usedCombos.containsKey(comboKey)
                        val score = if (isUsed) gameLogic.playerStatuses[gameLogic.currentPlayer].usedCombos[comboKey] ?: 0 else scores[comboKey] ?: 0
                        //se ho già usato una combo ci mette il valore che ho salvato prima selezionando la combo nella tabella altrimenti mostro il nuovo punteggio generato

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(tileHeight)
                                //.clip(RoundedCornerShape(8.dp))
                                .background(Color.DarkGray)
                                .clickable(enabled = col == 1 && !isUsed) { //la casella è cliccabile solo se non è stata già usata
                                    confirmedScores(comboKey, score)  //fissa la combinazione quando premi
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (col == 0) {
                                Text(label,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontFamily = font_Hershey,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = score.toString(),
                                    color = when {
                                        isUsed -> Color.White
                                        else -> Color(0xFF707070)
                                    },
                                    fontSize = 32.sp,
                                    fontFamily = font_handwriting,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
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
        //onComboClick: () -> Unit
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
                        //.clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF92BB92))
                ) {
                    CombosGridComposition2(
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        dice = gameLogic.dice,
                        upperScores = gameLogic.playerStatuses[gameLogic.currentPlayer].upperSectionScores,
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
                        //.clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                ) {
                    CombosGridComposition(
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        gameLogic = gameLogic,
                        confirmedScores = { combo, score ->
                            gameLogic.confirmScore(combo, score)
                        }
                    )


                }
            }
            Box(modifier = Modifier.padding(top = 25.dp)) {
                funButton3D(
                    onClick = { isFirstOnTop = !isFirstOnTop },
                    stringResource(R.string.game_swapsection),
                    color = MaterialTheme.colorScheme.primary,
                    depth = 10,
                    sizeX = screenWidth*0.7f,
                    sizeY = 50.dp
                )
            }
        }
    }


    //-------------------------------
    sealed class Perspective(
    ) {
        data class Left(
            val bottomEdgeColor: Color, val rightEdgeColor: Color
        ) : Perspective()

        data class Right(
            val topEdgeColor: Color, val leftEdgeColor: Color
        ) : Perspective()

        data class Top(
            val bottomEdgeColor: Color
        ) : Perspective()
    }

    @Composable
    fun funButton3D(onClick : () -> Unit, text : String, color : Color, depth: Long, sizeX : Dp, sizeY : Dp){

        var saturation = 0.7f

        MealCalendar(
            perspective = Composables.Perspective.Left(
                bottomEdgeColor = lerp(Color(0x878787ff), color, saturation),
                rightEdgeColor = lerp(Color(0xd4d4d4ff), color, saturation),
            ),
            edgeOffset = depth.toInt().dp //devo averlo come long nell'argomento perchè depth della vibrazione vuole un long
        ) {
            Box(
                modifier = Modifier
                    .size(sizeX, sizeY)
                    .background(lerp(Color(0xffffffff), color, saturation))
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            //evento pressione del tasto
                            val downEvent =
                                awaitPointerEvent(PointerEventPass.Main)
                            downEvent.changes.forEach {
                                if (it.pressed) {
                                    hfx?.btnDown(depth)
                                    sfx?.btnDown()
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
                                        sfx?.btnUp()
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center) {
                Text(
                    text,
                    fontSize = 20.sp,
                    fontFamily = font_Squarewise,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MealCalendar( //bottone 3D
        perspective: Perspective = Perspective.Left(
            bottomEdgeColor = Color.Black, rightEdgeColor = Color.Black
        ), edgeOffset: Dp = 16.dp, content: @Composable () -> Unit
    ) {
        val interactionSource = remember {
            MutableInteractionSource()
        }

        val isPressed by interactionSource.collectIsPressedAsState()

        val offsetInPx = with(LocalDensity.current) {
            edgeOffset.toPx()
        }

        val elevationOffset by remember {
            derivedStateOf {
                if (isPressed) {
                    when (perspective) {
                        is Perspective.Left -> {
                            IntOffset(offsetInPx.toInt(), offsetInPx.toInt())
                        }

                        is Perspective.Right -> {
                            IntOffset(-offsetInPx.toInt(), -offsetInPx.toInt())
                        }

                        is Perspective.Top -> {
                            IntOffset(0, offsetInPx.toInt())
                        }
                    }
                } else {
                    IntOffset.Zero
                }
            }
        }


        Box(modifier = Modifier
            .combinedClickable(interactionSource = interactionSource, indication = null, onClick = {
            })
            .graphicsLayer {
                rotationX = when (perspective) {
                    is Perspective.Top -> {
                        16f
                    }

                    else -> {
                        0f
                    }
                }
            }
            .drawBehind {
                if (isPressed.not()) {
                    when (perspective) {
                        is Perspective.Left -> {
                            // right edge
                            val rightEdge = Path().apply {
                                moveTo(size.width, 0f)
                                lineTo(size.width + offsetInPx, offsetInPx)
                                lineTo(size.width + offsetInPx, size.height + offsetInPx)
                                lineTo(size.width, size.height)
                                close()
                            }
                            // bottom edge
                            val bottomEdge = Path().apply {
                                moveTo(size.width, size.height)
                                lineTo(size.width + offsetInPx, size.height + offsetInPx)
                                lineTo(offsetInPx, size.height + offsetInPx)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(
                                path = rightEdge, color = perspective.rightEdgeColor, style = Fill
                            )
                            drawPath(
                                path = bottomEdge, color = perspective.bottomEdgeColor, style = Fill
                            )
                        }

                        is Perspective.Top -> {
                            // bottom edge
                            val bottomEdge = Path().apply {
                                moveTo(0f, size.height)
                                lineTo(size.width, size.height)
                                lineTo(size.width - offsetInPx, size.height + offsetInPx)
                                lineTo(offsetInPx, size.height + offsetInPx)
                                close()
                            }
                            drawPath(
                                path = bottomEdge, color = perspective.bottomEdgeColor, style = Fill
                            )
                        }

                        is Perspective.Right -> {
                            val topEdge = Path().apply {
                                moveTo(-offsetInPx, -offsetInPx)
                                lineTo(size.width - offsetInPx, -offsetInPx)
                                lineTo(size.width, 0f)
                                lineTo(0f, 0f)
                                close()
                            }
                            val leftEdge = Path().apply {
                                moveTo(-offsetInPx, -offsetInPx)
                                lineTo(0f, 0f)
                                lineTo(0f, size.height)
                                lineTo(-offsetInPx, size.height - offsetInPx)
                                close()
                            }
                            drawPath(path = topEdge, color = perspective.topEdgeColor, style = Fill)
                            drawPath(
                                path = leftEdge, color = perspective.leftEdgeColor, style = Fill
                            )
                        }
                    }
                }
            }, contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.offset {
                    elevationOffset
                }, contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }

    @Composable
    fun title(){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth(),
        ){
            Text(
                text = stringResource(R.string.app_name),
                fontFamily = font_Square,
                fontSize = 72.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    @Composable
    fun titleLabel(s : String){
        Text(
            text = s,
            fontFamily = font_Hershey,
            fontSize = 24.sp
        )
    }

    @Composable
    fun scoreDisplay(){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.Center){
                Text(
                    text = "${gameLogic.playerStatuses[gameLogic.currentPlayer].totalScore}",
                    fontSize = 48.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp),
                    fontFamily = font_Squarewise
                )
                Text(
                    text = stringResource(R.string.game_player) + " : " + gameLogic.currentPlayer + " / " + gameLogic.playerAmount,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(8.dp),
                    fontFamily = font_Hershey
                )
                Text(
                    text = stringResource(R.string.game_round) + " : " + ((gameLogic.roundNumber/gameLogic.playerAmount) + 1) + " / " + 13,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(8.dp),
                    fontFamily = font_Hershey
                )
            }

            Column(verticalArrangement = Arrangement.Center){
                Text(
                    text = if (gameLogic.playerStatuses[gameLogic.currentPlayer].bonusJustAwarded) "Bonus Upper ottenuto" else "No bonus Upper",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontFamily = font_Hershey,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Yahtzee Bonus: " + gameLogic.playerStatuses[gameLogic.currentPlayer].YahtzeeBonusCount,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontFamily = font_Hershey,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }


    @Composable
    fun scoreDisplayLandscape(){
        Row(){
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(){
                    Text(
                        text = "${gameLogic.playerStatuses[gameLogic.currentPlayer].totalScore}",
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = font_Squarewise
                    )
                    Text(
                        text = stringResource(R.string.game_player) + " : " + gameLogic.currentPlayer + " / " + gameLogic.playerAmount,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = font_Hershey
                    )
                    Text(
                        text = stringResource(R.string.game_round) + " : " + ((gameLogic.roundNumber/gameLogic.playerAmount) + 1) + " / " + 13,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = font_Hershey
                    )

                }
                Column(){
                    Text(
                        text = if (gameLogic.playerStatuses[gameLogic.currentPlayer].bonusJustAwarded) "Bonus Upper ottenuto" else "No bonus Upper",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontFamily = font_Hershey,
                        fontWeight = FontWeight.Bold

                    )
                    Text(
                        text = "Yahtzee Bonus: " + gameLogic.playerStatuses[gameLogic.currentPlayer].YahtzeeBonusCount,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontFamily = font_Hershey,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }


    //popup del db

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
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.padding(5.dp).size(popUpWidth, popUpHeight),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        Modifier.size(popUpWidth, popUpHeight)
                            .padding(bottom = 20.dp),
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
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Fit,
                            )

                            Text(text = stringResource(R.string.scoredetails), fontSize = 20.sp)
                        }

                        //table content
                        Text(text = stringResource(R.string.date) + " : ${tableScore.date} ", fontSize = 20.sp)

                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            item { Text(stringResource(R.string.finalscore) + " : ${tableScore.finalScore}", fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp)) }
                            item { Spacer(modifier = Modifier.height(8.dp))}
                            item { Text(stringResource(R.string.uppersection), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold) }
                            item { Text(stringResource(R.string.upper_aces) + " : ${tableScore.aces}") }
                            item { Text(stringResource(R.string.upper_twos) + " : " + tableScore.twos) }
                            item { Text(stringResource(R.string.upper_threes) + " : " + tableScore.threes) }
                            item { Text(stringResource(R.string.upper_fours) + " : " + tableScore.fours) }
                            item { Text(stringResource(R.string.upper_fives) + " : " + tableScore.fives) }
                            item { Text(stringResource(R.string.upper_sixes) + " : " + tableScore.sixes) }
                            item { Text("Bonus : ${tableScore.bonusUpperSection}") }

                            item { Spacer(modifier = Modifier.height(8.dp))}
                            item { Text(stringResource(R.string.lowersection), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold) }
                            item { Text(stringResource(R.string.combo_3kind) + " : ${tableScore.threeOfAKind}") }
                            item { Text(stringResource(R.string.combo_4kind) + " : ${tableScore.fourOfAKind}") }
                            item { Text(stringResource(R.string.combo_fullhouse) + " : ${tableScore.fullHouse}") }
                            item { Text(stringResource(R.string.combo_sstraight) + " : ${tableScore.smallStraight}") }
                            item { Text(stringResource(R.string.combo_lstraight) + " : ${tableScore.largeStraight}") }
                            item { Text(stringResource(R.string.combo_chance) + " : ${tableScore.chance}") }
                            item { Text(stringResource(R.string.combo_5kind) + " : ${tableScore.yahtzee}") }
                            item { Text("Yahtzee Bonus : ${tableScore.yahtzeeBonus}") }
                        }


                        // delete button
                        composables?.funButton3D(
                            onClick = { onEvent(ScoreListEvent.deleteScore(tableScore)) }, //send action to viewModel
                            text = stringResource(R.string.delete),
                            color = Color.Red,
                            depth = 5,
                            screenWidth * 0.50f, 50.dp
                        )
                    }
                }
            }
        }
    }
}


